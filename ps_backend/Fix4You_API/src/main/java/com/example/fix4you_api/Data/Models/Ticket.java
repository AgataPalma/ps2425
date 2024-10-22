package com.example.fix4you_api.Data.Models;

import com.example.fix4you_api.Data.Enums.TicketStatusEnum;
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
    @NotNull(message = "User ID cannot be null")
    private String userId;

    @Field
    @NotNull(message = "Admin ID cannot be null")
    private String adminId;

    @Field
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @Field
    @NotBlank(message = "Description cannot be blank")
    private String description;

    @Field
    @NotNull(message = "Status cannot be null")
    private TicketStatusEnum status;

    @Field
    @NotNull(message = "Ticket date cannot be null")
    @PastOrPresent(message = "Ticket date must be in the past or present")
    private LocalDateTime ticketDate;
}