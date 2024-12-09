package com.example.fix4you_api.Data.Models;

import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "Service ID não pode ser nulo")
    private String serviceId;

    @Field
    @NotNull(message = "Professional ID não pode ser nulo")
    private String professionalId;

    @Field
    @NotNull(message = "Data de começo não pode ser nula")
    private LocalDateTime dateStart;

    @Field
    @NotNull(message = "Data de término não pode ser nula")
    private LocalDateTime dateFinish;

    @Field
    @NotNull(message = "Client ID não pode ser nulo")
    private String clientId;

    @Field
    @NotNull(message = "Estado não pode ser nulo")
    private ScheduleStateEnum state;

    @Field
    private boolean jobChecked;
}