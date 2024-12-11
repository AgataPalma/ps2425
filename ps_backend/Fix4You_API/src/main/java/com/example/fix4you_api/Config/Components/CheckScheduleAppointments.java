package com.example.fix4you_api.Config.Components;

import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import com.example.fix4you_api.Data.Enums.ServiceStateEnum;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.Models.ScheduleAppointment;
import com.example.fix4you_api.Data.Models.Service;
import com.example.fix4you_api.Data.MongoRepositories.ProfessionalRepository;
import com.example.fix4you_api.Data.MongoRepositories.ScheduleAppointmentRepository;
import com.example.fix4you_api.Data.MongoRepositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class CheckScheduleAppointments {

    @Autowired
    private ScheduleAppointmentRepository scheduleAppointmentRepository;
    private ServiceRepository serviceRepository;
    private ProfessionalRepository professionalRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Async // Execute in a separate thread
    public void task1() {
        List<ScheduleAppointment> scheduleAppointments = this.scheduleAppointmentRepository.findByState(ScheduleStateEnum.PENDING);
        for (var i=0; i< scheduleAppointments.size(); i++){
            ScheduleAppointment scheduleAppointment = scheduleAppointments.get(i);
            Optional<Service> service = serviceRepository.findById(scheduleAppointment.getServiceId());
            if(service.get().getAcceptedDate() != null) {
                Duration d = Duration.between(service.get().getAcceptedDate(), LocalDateTime.now());
                if (d.toDays() >= 3) {
                    scheduleAppointment.setState(ScheduleStateEnum.CANCELED);
                    scheduleAppointmentRepository.save(scheduleAppointment);
                    Optional<Professional> professionalOpt = professionalRepository.findById(service.get().getProfessionalId());
                    professionalOpt.get().setStrikes(professionalOpt.get().getStrikes() + 1);
                    if (professionalOpt.get().getStrikes() == 3) {
                        professionalOpt.get().setSupended(true);
                        professionalOpt.get().setSuspensionReason("Você acumulou 3 strikes!");
                    }
                    professionalRepository.save(professionalOpt.get());
                }
            }
        }
    }
}
