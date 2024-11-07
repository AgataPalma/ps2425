package com.example.fix4you_api.Service.ScheduleAppointment;

import com.example.fix4you_api.Data.MongoRepositories.ScheduleAppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleAppointmentServiceImpl implements ScheduleAppointmentService {

    private final ScheduleAppointmentRepository scheduleAppointmentRepository;

    @Override
    @Transactional
    public void deleteScheduleAppointment(String serviceId) {
        scheduleAppointmentRepository.deleteByServiceId(serviceId);
    }
}