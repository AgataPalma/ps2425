package com.example.fix4you_api.Event.ProfessionalFee;

import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.Models.ProfessionalsFee;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FeePaymentCompletionEvent extends ApplicationEvent {

    private final ProfessionalsFee professionalsFee;
    private final Professional professional;

    public FeePaymentCompletionEvent(Object source, ProfessionalsFee professionalsFee, Professional professional) {
        super(source);
        this.professionalsFee = professionalsFee;
        this.professional = professional;
    }
}
