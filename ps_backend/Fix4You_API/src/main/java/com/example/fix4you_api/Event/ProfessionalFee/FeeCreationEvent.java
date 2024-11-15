package com.example.fix4you_api.Event.ProfessionalFee;

import com.example.fix4you_api.Data.Models.ProfessionalsFee;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FeeCreationEvent extends ApplicationEvent {

    private final ProfessionalsFee professionalsFee;

    public FeeCreationEvent(Object source, ProfessionalsFee professionalsFee) {
        super(source);
        this.professionalsFee = professionalsFee;
    }
}
