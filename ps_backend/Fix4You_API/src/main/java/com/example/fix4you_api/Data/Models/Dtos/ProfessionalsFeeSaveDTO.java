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
public class ProfessionalsFeeSaveDTO {

    private static final float FEE_PRICE = 20.0f;

    @Id
    private String id;

    @NotNull(message = "Profissional não pode ser nulo")
    private SimpleProfessionalDTO professional;

    @NotNull(message = "Valor não pode ser nulo")
    @DecimalMin(value = "0.0", message = "O valor deve ser um valor positivo")
    @Digits(integer = 3, fraction = 2, message = "O valor deve ser um valor numérico com um máximo de 3 dígitos e 2 casas decimais")
    private float value;

    @NotNull(message = "O número de serviços não pode ser nulo")
    @PositiveOrZero(message = "O número de serviços deve ser zero ou um número positivo")
    private int numberServices;

    @NotBlank(message = "O mês relacionado não pode estar em branco")
    private String relatedMonthYear;

    private LocalDateTime paymentDate;

    @NotNull(message = "O estado do pagamento não pode ser nulo")
    private PaymentStatusEnum paymentStatus;

    public ProfessionalsFee toDomain() {
        ProfessionalsFee fee = new ProfessionalsFee();
        BeanUtils.copyProperties(this, fee, "invoice");
        return fee;
    }
}