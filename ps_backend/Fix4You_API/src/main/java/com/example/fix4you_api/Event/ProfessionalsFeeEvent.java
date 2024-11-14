package com.example.fix4you_api.Event;

import com.example.fix4you_api.Data.Models.ProfessionalsFee;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ProfessionalsFeeEvent extends ApplicationEvent {

    private final ProfessionalsFee professionalsFee;

    public ProfessionalsFeeEvent(Object source, ProfessionalsFee professionalsFee) {
        super(source);
        this.professionalsFee = professionalsFee;
    }
}
