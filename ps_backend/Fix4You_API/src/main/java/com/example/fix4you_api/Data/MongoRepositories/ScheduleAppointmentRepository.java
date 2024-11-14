package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import com.example.fix4you_api.Data.Models.ScheduleAppointment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleAppointmentRepository extends MongoRepository<ScheduleAppointment, String> {
    List<ScheduleAppointment> findByProfessionalId(String id);
    List<ScheduleAppointment> findByClientId(String id);
    List<ScheduleAppointment> findByServiceId(String serviceId);
    List<ScheduleAppointment> findByServiceIdAndStateAndDateFinishBetween(String serviceId, ScheduleStateEnum state, LocalDateTime startDate, LocalDateTime endDate);
    void deleteByServiceId(String serviceId);
}
