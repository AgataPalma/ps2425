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
public class CheckUrgentServices {

    @Autowired
    private ServiceRepository serviceRepository;
    private ProfessionalRepository professionalRepository;
    private ScheduleAppointmentRepository scheduleAppointmentRepository;


    @Scheduled(cron = "0 0 0 * * *")
    @Async // Execute in a separate thread
    public void task1() {
        // Task logic goes here
        List<Service> services = this.serviceRepository.findByUrgentTrueAndState(ServiceStateEnum.PENDING);
        for (var i=0; i< services.size(); i++){
            if(services.get(i).getAgreementDate() != null) {
                Duration d = Duration.between(services.get(i).getAgreementDate(), LocalDateTime.now());
                if (d.toDays() > 1) {
                    Optional<Professional> professionalOpt = professionalRepository.findById(services.get(i).getProfessionalId());
                    professionalOpt.get().setStrikes(professionalOpt.get().getStrikes() + 1);
                    services.get(i).setState(ServiceStateEnum.CANCELED);
                    serviceRepository.save(services.get(i));
                    if (professionalOpt.get().getStrikes() == 3) {
                        professionalOpt.get().setSuspended(true);
                        professionalOpt.get().setSuspensionReason("Você acumulou 3 strikes!");
                        professionalRepository.save(professionalOpt.get());
                    }
                }
            }
        }

        List<ScheduleAppointment> scheduleAppointments = this.scheduleAppointmentRepository.findByStateMatchesAndDateFinishAfter(ScheduleStateEnum.PENDING, LocalDateTime.now());
        for (var i=0; i< scheduleAppointments.size(); i++){
            Optional<Service> service = serviceRepository.findById(scheduleAppointments.get(i).getServiceId());
            if(service.get().isUrgent()) {
                if (scheduleAppointments.get(i).isJobChecked()) {
                    Optional<Professional> professionalOpt = professionalRepository.findById(scheduleAppointments.get(i).getProfessionalId());
                    professionalOpt.get().setStrikes(professionalOpt.get().getStrikes() + 1);
                    scheduleAppointments.get(i).setState(ScheduleStateEnum.CANCELED);
                    scheduleAppointmentRepository.save(scheduleAppointments.get(i));
                    if (professionalOpt.get().getStrikes() == 3) {
                        professionalOpt.get().setSuspended(true);
                        professionalOpt.get().setSuspensionReason("Você acumulou 3 strikes!");
                        professionalRepository.save(professionalOpt.get());
                    }
                }  else {
                    scheduleAppointments.get(i).setJobChecked(true);
                    scheduleAppointmentRepository.save(scheduleAppointments.get(i));
                }
            }
        }
    }

}
