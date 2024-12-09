package com.example.fix4you_api.Data.Models;

import com.example.fix4you_api.Data.Enums.TicketStatusEnum;
import com.example.fix4you_api.Data.Models.Dtos.SimpleUserDTO;
import jakarta.validation.constraints.NotBlank;
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
@Document("Tickets")
public class Ticket {
    @Id
    private String id;

    @Field
    @NotNull(message = "O utilizador não pode ser nulo")
    private SimpleUserDTO user;

    @Field
    private SimpleUserDTO admin;

    @Field
    @NotBlank(message = "O título não pode estar em branco")
    private String title;

    //@Field
    //@NotBlank(message = "Description cannot be blank")
    //private String description;

    @Field
    @NotNull(message = "O estado não pode ser nulo")
    private TicketStatusEnum status;

    @Field
    @NotNull(message = "A data de início do ticket não pode ser nula")
    @PastOrPresent(message = "A data de início do ticket deve ser no passado ou no presente")
    private LocalDateTime ticketStartDate;

    @Field
    @PastOrPresent(message = "Admin Assignment Date must be in the past or present")
    private LocalDateTime adminAssignmentDate;

    @Field
    @PastOrPresent(message = "A data de término do ticket tem de ser no passado ou no presente")
    private LocalDateTime ticketCloseDate;
}