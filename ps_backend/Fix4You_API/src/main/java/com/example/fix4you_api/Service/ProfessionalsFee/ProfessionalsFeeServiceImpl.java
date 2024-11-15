package com.example.fix4you_api.Service.ProfessionalsFee;

import com.example.fix4you_api.Data.Enums.PaymentStatusEnum;
import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import com.example.fix4you_api.Data.Enums.ServiceStateEnum;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.Models.ProfessionalsFee;
import com.example.fix4you_api.Data.Models.ScheduleAppointment;
import com.example.fix4you_api.Data.MongoRepositories.ProfessionalFeeRepository;
import com.example.fix4you_api.Service.Professional.ProfessionalService;
import com.example.fix4you_api.Service.ScheduleAppointment.ScheduleAppointmentService;
import com.example.fix4you_api.Service.Service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProfessionalsFeeServiceImpl implements ProfessionalsFeeService{

    private final ProfessionalFeeRepository professionalFeeRepository;
    private final ProfessionalService professionalService;
    private final ServiceService serviceService;
    private final ScheduleAppointmentService scheduleAppointmentService;

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
    public ProfessionalsFee createProfessionalsFee(ProfessionalsFee professionalsFee) {
       return professionalFeeRepository.save(professionalsFee);
    }

    @Override
    public ProfessionalsFee createProfessionalFeeForRespectiveMonth(String professionalId, int numberServices, String relatedMonthYear) {
        ProfessionalsFee newProfessionalsFee = new ProfessionalsFee(professionalId, 20, numberServices, relatedMonthYear, PaymentStatusEnum.PENDING);
        professionalService.setProfessionalIsSuspended(professionalId, true);
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
                case "professionalId" -> existingProfessionalsFee.setProfessionalId((String) value);
                case "value" -> existingProfessionalsFee.setValue((float) value);
                case "numberServices" -> existingProfessionalsFee.setNumberServices((int) value);
                case "relatedMonthYear" -> existingProfessionalsFee.setRelatedMonthYear((String) value);
                case "paymentDate" -> existingProfessionalsFee.setPaymentDate((LocalDateTime) value);
                case "paymentStatus" -> existingProfessionalsFee.setPaymentStatus((PaymentStatusEnum) value);
                default -> throw new RuntimeException("Invalid field update request");
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
    public void deleteProfessionalFees(String professionalId) {
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

    private ProfessionalsFee findOrThrow(String id) {
        return professionalFeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("ProfessionalsFee %s not found", id)));
    }

}