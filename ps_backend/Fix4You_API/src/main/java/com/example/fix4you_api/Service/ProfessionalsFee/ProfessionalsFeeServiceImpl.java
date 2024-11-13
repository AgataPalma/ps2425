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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessionalsFeeServiceImpl implements ProfessionalsFeeService{

    private final ProfessionalFeeRepository professionalFeeRepository;
    private final ProfessionalService professionalService;
    private final ServiceService serviceService;
    private final ScheduleAppointmentService scheduleAppointmentService;

    @Override
    public ProfessionalsFee createProfessionalsFee(String professionalId, int numberServices, String relatedMonthYear) {
        ProfessionalsFee newProfessionalsFee = new ProfessionalsFee(professionalId, 20, numberServices, relatedMonthYear, PaymentStatusEnum.PENDING);
        return professionalFeeRepository.save(newProfessionalsFee);
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
                createProfessionalsFee(professional.getId(), completedLastMonthServicesCount, lastMonth.toString());
            }

        }
    }

}