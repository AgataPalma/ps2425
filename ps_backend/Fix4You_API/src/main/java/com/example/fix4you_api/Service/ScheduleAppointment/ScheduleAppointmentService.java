package com.example.fix4you_api.Service.ScheduleAppointment;

import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import com.example.fix4you_api.Data.Models.ScheduleAppointment;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleAppointmentService {
    List<ScheduleAppointment> getScheduleAppointmentsByServiceIdAndStateAndDateFinishBetween(String serviceId, ScheduleStateEnum state, LocalDateTime startDate, LocalDateTime endDate);
    void deleteScheduleAppointment(String serviceId);
    void updateScheduleAppointmentState(String id, ScheduleStateEnum state);
}
