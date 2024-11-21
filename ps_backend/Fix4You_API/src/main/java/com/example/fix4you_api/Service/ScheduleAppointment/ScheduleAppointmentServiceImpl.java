package com.example.fix4you_api.Service.ScheduleAppointment;

import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import com.example.fix4you_api.Data.Models.ScheduleAppointment;
import com.example.fix4you_api.Data.MongoRepositories.ScheduleAppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleAppointmentServiceImpl implements ScheduleAppointmentService {

    private final ScheduleAppointmentRepository scheduleAppointmentRepository;

    @Override
    public List<ScheduleAppointment> getScheduleAppointmentsByServiceIdAndStateAndDateFinishBetween(String serviceId, ScheduleStateEnum state, LocalDateTime startDate, LocalDateTime endDate){
        return this.scheduleAppointmentRepository.findByServiceIdAndStateAndDateFinishBetween(serviceId, state, startDate, endDate);
    }

    @Override
    @Transactional
    public void deleteScheduleAppointment(String serviceId) {
        scheduleAppointmentRepository.deleteByServiceId(serviceId);
    }

}