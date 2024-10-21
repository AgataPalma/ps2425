package com.example.fix4you_api.Data.Models;

import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("ScheduleAppointments")
public class ScheduleAppointment {

    @Id
    private String id;

    @Field
    @NotNull(message = "Professional ID cannot be null")
    private String professionalId;

    @Field
    @NotNull(message = "Start date cannot be null")
    @PastOrPresent(message = "Start date must be in the past or present")
    private LocalDateTime dateStart;

    @Field
    @NotNull(message = "Finish date cannot be null")
    @Future(message = "Finish date must be in the future")
    private LocalDateTime dateFinish;

    @Field
    @NotNull(message = "Client ID cannot be null")
    private String clientId;

    @Field
    @NotNull(message = "State cannot be null")
    private ScheduleStateEnum state;
}