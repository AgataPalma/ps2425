package com.example.fix4you_api.Service.PaymentMethod;

import com.example.fix4you_api.Data.Models.Language;
import com.example.fix4you_api.Data.Models.PaymentMethod;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.MongoRepositories.PaymentMethodRepository;
import com.example.fix4you_api.Service.Professional.ProfessionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final ProfessionalService professionalService;

    @Override
    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    @Override
    public PaymentMethod createPaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    @Transactional
    public PaymentMethod updatePaymentMethod(String id, PaymentMethod paymentMethod) {
        List<Professional> professionals = professionalService.getProfessionalsByPaymentMethod(paymentMethod.getId());

        if (professionals.isEmpty()) {

            PaymentMethod existingPaymentMethod = findOrThrow(id);
            BeanUtils.copyProperties(paymentMethod, existingPaymentMethod, "id");
            return paymentMethodRepository.save(existingPaymentMethod);

        }  else {
            throw new IllegalStateException("Não é possível editar totalmente o método de pagamento, pois possui profissionais associados.");
        }
    }

    @Override
    @Transactional
    public PaymentMethod partialUpdatePaymentMethod(String id, Map<String, Object> updates) {
        PaymentMethod existingPaymentMethod = findOrThrow(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> {
                    List<Professional> professionals = professionalService.getProfessionalsByPaymentMethod(id);

                    if (professionals.isEmpty()) {

                        existingPaymentMethod.setName((String) value);

                    }  else {
                        throw new IllegalStateException("Não é possível editar o método de pagamento, pois possui profissionais associados.\"");
                    }
                }
                default -> throw new RuntimeException("Campo inválido no pedido da atualização!");
            }
        });

        return paymentMethodRepository.save(existingPaymentMethod);
    }

    @Override
    @Transactional
    public void deletePaymentMethod(String id) {
        List<Professional> professionals = professionalService.getProfessionalsByPaymentMethod(id);

        if (professionals.isEmpty()) {

            paymentMethodRepository.deleteById(id);

        }  else {
            throw new IllegalStateException("Não é possível excluir o método de pagamento, pois possui profissionais associados.");
        }
    }

    private PaymentMethod findOrThrow(String id) {
        return paymentMethodRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Método de pagamento %s não encontrado!", id)));
    }

    @Override
    public boolean nameExists(String name) {
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findAll();

        for (PaymentMethod paymentMethod : paymentMethods) {
            if (paymentMethod.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

}