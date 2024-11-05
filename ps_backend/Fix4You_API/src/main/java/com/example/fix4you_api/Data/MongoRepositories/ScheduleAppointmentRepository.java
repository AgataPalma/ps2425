package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Models.ScheduleAppointment;
import com.example.fix4you_api.Data.Models.Service;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ScheduleAppointmentRepository extends MongoRepository<ScheduleAppointment, String> {
    List<ScheduleAppointment> findByProfessionalId(String id);

    List<ScheduleAppointment> findByClientId(String id);
}
