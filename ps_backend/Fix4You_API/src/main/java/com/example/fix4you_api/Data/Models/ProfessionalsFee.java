package com.example.fix4you_api.Data.Models;

import com.example.fix4you_api.Data.Enums.PaymentStatusEnum;
import com.example.fix4you_api.Data.Models.Dtos.SimpleProfessionalDTO;
import jakarta.validation.constraints.*;
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
@Document("ProfessionalsFees")
public class ProfessionalsFee {

    private static final float FEE_PRICE = 20.0f;

    @Id
    private String id;

    @Field
    @NotNull(message = "Profissional não pode ser nulo")
    private SimpleProfessionalDTO professional;

    @Field
    @NotNull(message = "O valor não pode ser nulo")
    @DecimalMin(value = "0.0", message = "O valor deve ser um valor positivo")
    @Digits(integer = 3, fraction = 2, message = "O valor deve ser um valor numérico com um máximo de 3 dígitos e 2 casas decimais")
    private float value;

    @Field
    @NotNull(message = "O número de serviços não pode ser nulo")
    @PositiveOrZero(message = "O número de serviços deve ser zero ou um número positivo")
    private int numberServices;

    @Field
    @NotBlank(message = "O mês relacionado não pode estar em branco")
    private String relatedMonthYear;

    @Field
    private LocalDateTime paymentDate;

    @Field
    @NotNull(message = "O estado do pagamento não pode ser nulo")
    private PaymentStatusEnum paymentStatus;

    @Field
    private byte[] invoice;

    public ProfessionalsFee(SimpleProfessionalDTO professional, int numberServices, String relatedMonthYear, PaymentStatusEnum paymentStatus) {
        this.professional = professional;
        this.value = FEE_PRICE;
        this.numberServices = numberServices;
        this.relatedMonthYear = relatedMonthYear;
        this.paymentStatus = paymentStatus;
    }

}