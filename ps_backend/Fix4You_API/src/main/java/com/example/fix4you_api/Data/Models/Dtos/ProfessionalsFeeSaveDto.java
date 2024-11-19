package com.example.fix4you_api.Data.Models.Dtos;

import com.example.fix4you_api.Data.Enums.PaymentStatusEnum;
import com.example.fix4you_api.Data.Models.ProfessionalsFee;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfessionalsFeeSaveDto {

    private static final float FEE_PRICE = 20.0f;

    @Id
    private String id;

    @NotNull(message = "Professional cannot be null")
    private ProfessionalsFee.Professional professional;

    @NotNull(message = "Value cannot be null")
    @DecimalMin(value = "0.0", message = "Value must be a positive value")
    @Digits(integer = 3, fraction = 2, message = "Value must be a numeric value with up to 3 digits and 2 decimal places")
    private float value;

    @NotNull(message = "Number of services cannot be null")
    @PositiveOrZero(message = "Number of services must be zero or a positive number")
    private int numberServices;

    @NotBlank(message = "Related month cannot be blank")
    private String relatedMonthYear;

    private LocalDateTime paymentDate;

    @NotNull(message = "Payment status cannot be null")
    private PaymentStatusEnum paymentStatus;

    public ProfessionalsFee toDomain() {
        ProfessionalsFee fee = new ProfessionalsFee();
        BeanUtils.copyProperties(this, fee, "invoice");
        return fee;
    }
}
