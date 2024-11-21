package com.example.fix4you_api.Service.ScheduleAppointment;

import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import com.example.fix4you_api.Data.Models.ScheduleAppointment;
import com.example.fix4you_api.Service.ScheduleAppointment.DTOs.GoogleCalendarEvent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleAppointmentService {
    List<ScheduleAppointment> getScheduleAppointmentsByServiceIdAndStateAndDateFinishBetween(String serviceId, ScheduleStateEnum state, LocalDateTime startDate, LocalDateTime endDate);
    void deleteScheduleAppointment(String serviceId);
    void connectUserToGoogleToken(String userId, String token, String refreshToken);
    String createGoogleCalendarEvent(String userId, String appointmentId) throws IOException;
    boolean isTokenValid(String accessToken) throws IOException;
    String refreshToken(String refreshToken);
    List<GoogleCalendarEvent> getGoogleCalendarEventsBetween(String userId, LocalDateTime start, LocalDateTime end);
}
