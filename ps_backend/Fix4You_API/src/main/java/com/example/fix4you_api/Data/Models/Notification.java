package com.example.fix4you_api.Data.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    private String professionalId;
    private String message;
    private boolean read;
    private String type; // "fee"
    private String referenceId; // ID do ProfessionalFee

    private Date createdAt;

    // **Novos campos para detalhes da taxa**
    private double feeValue;
    private int numberServices;
    private String relatedMonthYear;
    private String paymentStatus;
    private Date paymentDate;

    // Getters e Setters são gerados automaticamente pela anotação @Data
}