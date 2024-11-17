package com.example.fix4you_api.Event.CategoryDescription;

import com.example.fix4you_api.Service.ProfessionalCategory.ProfessionalCategoryViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryDescriptionEventListener {

    private final ProfessionalCategoryViewService professionalCategoryViewService;

    @EventListener
    public void handleProfessionalCategoryCreationEvent(CategoryDescriptionCreationEvent event) {
        professionalCategoryViewService.createViews();
    }
}
