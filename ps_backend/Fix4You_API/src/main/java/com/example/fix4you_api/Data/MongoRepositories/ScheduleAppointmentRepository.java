package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import com.example.fix4you_api.Data.Models.ScheduleAppointment;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleAppointmentRepository extends MongoRepository<ScheduleAppointment, String> {
    List<ScheduleAppointment> findByProfessionalId(String id);
    List<ScheduleAppointment> findByClientId(String id);
    List<ScheduleAppointment> findByServiceId(String serviceId);
    List<ScheduleAppointment> findByState(ScheduleStateEnum state);
    List<ScheduleAppointment> findByProfessionalIdAndState(String id, ScheduleStateEnum state);
    List<ScheduleAppointment> findByStateMatchesAndDateFinishAfter(ScheduleStateEnum state, LocalDateTime localDateTime);
    List<ScheduleAppointment> findByServiceIdAndStateAndDateFinishBetween(String serviceId, ScheduleStateEnum state, LocalDateTime startDate, LocalDateTime endDate);
    void deleteByServiceId(String serviceId);
}
