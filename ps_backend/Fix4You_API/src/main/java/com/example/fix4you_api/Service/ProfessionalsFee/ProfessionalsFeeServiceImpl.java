package com.example.fix4you_api.Service.ProfessionalsFee;

import com.example.fix4you_api.Data.Enums.PaymentStatusEnum;
import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import com.example.fix4you_api.Data.Enums.ServiceStateEnum;
import com.example.fix4you_api.Data.Enums.TicketStatusEnum;
import com.example.fix4you_api.Data.Models.*;
import com.example.fix4you_api.Data.Models.Dtos.SimpleProfessionalDTO;
import com.example.fix4you_api.Data.MongoRepositories.ProfessionalFeeRepository;
import com.example.fix4you_api.Event.ProfessionalFee.FeeCreationEvent;
import com.example.fix4you_api.Event.ProfessionalFee.FeePaymentCompletionEvent;
import com.example.fix4you_api.Service.Professional.ProfessionalService;
import com.example.fix4you_api.Service.ScheduleAppointment.ScheduleAppointmentService;
import com.example.fix4you_api.Service.Service.ServiceService;
import com.example.fix4you_api.Service.User.UserService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessionalsFeeServiceImpl implements ProfessionalsFeeService{

    private final ProfessionalFeeRepository professionalFeeRepository;
    private final ProfessionalService professionalService;
    private final UserService userService;
    private final ServiceService serviceService;
    private final ScheduleAppointmentService scheduleAppointmentService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<ProfessionalsFee> getAllProfessionalsFee() {
        return professionalFeeRepository.findAll();
    }

    @Override
    public ProfessionalsFee getProfessionalsFeeById(String id) {
        return findOrThrow(id);
    }

    @Override
    public List<ProfessionalsFee> getProfessionalsFeeForProfessionalId(String professionalId) {
        return professionalFeeRepository.findByProfessionalId(professionalId);
    }

    @Override
    public List<ProfessionalTotalSpent> getTopPriceProfessionals() {
        // Fetch the raw results: clientId and totalSpent
        List<ProfessionalsFee> results = professionalFeeRepository.findTopProfessionalsByTotalSpending();

        for(var i=0; i< results.size(); i++){
            if(results.get(i).getProfessional().getId() != null) {
                User user = userService.getUserById(results.get(i).getProfessional().getId());
                userService.sendEmailTopUsers(user);
            }
        }

        // Group by clientId and count services
        Map<String, Long> clientServiceCounts = results.stream()
                .filter(professionalFee -> professionalFee.getProfessional().getId() != null)
                .collect(Collectors.groupingBy(professionalFee -> professionalFee.getProfessional().getId(), Collectors.counting()));

        // Process the results
        return clientServiceCounts.entrySet().stream()
                .map(result -> new ProfessionalTotalSpent(
                        (String) result.getKey(),
                        ((Number) result.getValue()).doubleValue()     // totalSpent
                ))
                .sorted((a, b) -> Double.compare(b.getTotalSpent(), a.getTotalSpent())) // Sort by totalSpent (descending)
                .limit(10) // Top 10 clients
                .collect(Collectors.toList());
    }

    @Override
    public ProfessionalsFee createProfessionalsFee(ProfessionalsFee professionalsFee) {
        ProfessionalsFee fee = professionalFeeRepository.save(professionalsFee);
        eventPublisher.publishEvent(new FeeCreationEvent(this, fee));
        return fee;
    }

    @Override
    public ProfessionalsFee createProfessionalFeeForRespectiveMonth(String professionalId, int numberServices, String relatedMonthYear) {
        Professional professional = professionalService.getProfessionalById(professionalId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("isSupended", true);
        professionalService.partialUpdateProfessional(professionalId, updates);

        SimpleProfessionalDTO feeProfessional = new SimpleProfessionalDTO();
        feeProfessional.setId(professional.getId());
        feeProfessional.setEmail(professional.getEmail());
        feeProfessional.setName(professional.getName());
        feeProfessional.setNif(professional.getNif());

        ProfessionalsFee newProfessionalsFee = new ProfessionalsFee(feeProfessional, numberServices, relatedMonthYear, PaymentStatusEnum.PENDING);
        eventPublisher.publishEvent(new FeeCreationEvent(this, newProfessionalsFee));
        return professionalFeeRepository.save(newProfessionalsFee);
    }

    @Override
    @Transactional
    public ProfessionalsFee updateProfessionalsFee(String id, ProfessionalsFee professionalsFee) {
        ProfessionalsFee existingProfessionalsFee = findOrThrow(id);
        BeanUtils.copyProperties(professionalsFee, existingProfessionalsFee, "id");
        return professionalFeeRepository.save(existingProfessionalsFee);
    }

    @Override
    @Transactional
    public ProfessionalsFee partialUpdateProfessionalsFee(String id, Map<String, Object> updates) {
        ProfessionalsFee existingProfessionalsFee = findOrThrow(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "professional" -> existingProfessionalsFee.setProfessional((SimpleProfessionalDTO) value);
                case "value" -> existingProfessionalsFee.setValue((float) value);
                case "numberServices" -> existingProfessionalsFee.setNumberServices((int) value);
                case "relatedMonthYear" -> existingProfessionalsFee.setRelatedMonthYear((String) value);
                case "paymentDate" -> {
                    if (value instanceof LocalDateTime) {
                        existingProfessionalsFee.setPaymentDate((LocalDateTime) value);
                    } else if (value instanceof String) {
                        existingProfessionalsFee.setPaymentDate(LocalDateTime.parse((CharSequence) value));
                    }
                }
                case "paymentStatus" -> {
                    try {
                        existingProfessionalsFee.setPaymentStatus(PaymentStatusEnum.valueOf(value.toString().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Valor inválido para o estado: " + value);
                    }
                }
                default -> throw new RuntimeException("Campo inválido no pedido da atualização!\n");
            }
        });

        return professionalFeeRepository.save(existingProfessionalsFee);
    }

    @Override
    @Transactional
    public void deleteProfessionalFee(String id) {
        professionalFeeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteProfessionalFeesForProfessional(String professionalId) {
        professionalFeeRepository.deleteByProfessionalId(professionalId);
    }

    @Override
    @Transactional
    public void checkAndCreateMonthlyFees() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        LocalDateTime startOfLastMonth = lastMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfLastMonth = lastMonth.atEndOfMonth().atStartOfDay();

        List<Professional> professionals = professionalService.getAllActiveProfessionals();

        for (Professional professional : professionals) {
            int completedLastMonthServicesCount = 0;

            List<com.example.fix4you_api.Data.Models.Service> completedServices = serviceService.getServicesByProfessionalIdAndState(professional.getId(), ServiceStateEnum.COMPLETED);

            for (com.example.fix4you_api.Data.Models.Service service : completedServices) {
                List<ScheduleAppointment> completedLastMonthScheduleAppointments = scheduleAppointmentService.getScheduleAppointmentsByServiceIdAndStateAndDateFinishBetween(service.getId(), ScheduleStateEnum.COMPLETED, startOfLastMonth, endOfLastMonth);

                if(!completedLastMonthScheduleAppointments.isEmpty()) {
                    completedLastMonthServicesCount++;
                }

            }

            if (completedLastMonthServicesCount >= 10) {
                createProfessionalFeeForRespectiveMonth(professional.getId(), completedLastMonthServicesCount, lastMonth.toString());
            }

        }
    }

    @Transactional
    @Override
    public ProfessionalsFee setFeeAsPaid(String id) throws DocumentException {
        ProfessionalsFee fee = findOrThrow(id);
        Professional professional = professionalService.getProfessionalById(fee.getProfessional().getId());

        fee.setPaymentStatus(PaymentStatusEnum.COMPLETED);
        fee.setPaymentDate(LocalDateTime.now());
        fee.setInvoice(generateInvoice(fee, professional));
        ProfessionalsFee updatedFee = professionalFeeRepository.save(fee);

        eventPublisher.publishEvent(new FeePaymentCompletionEvent(this, updatedFee, professional));

        return fee;
    }

    private byte[] generateInvoice(ProfessionalsFee fee, Professional professional) throws DocumentException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, byteArrayOutputStream);

        document.open();
        document.add(new Paragraph("Invoice"));
        document.add(new Paragraph("Amount paid: " + fee.getValue()));
        document.add(new Paragraph("Name: " + professional.getName()));
        document.add(new Paragraph("NIF: " + professional.getNif()));
        document.add(new Paragraph("Invoice Date: " + LocalDateTime.now()));

        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    private ProfessionalsFee findOrThrow(String id) {
        return professionalFeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Taxa do profissional com o id %s não encontrada!", id)));
    }

}