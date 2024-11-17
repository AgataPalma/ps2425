package com.example.fix4you_api.Event.CategoryDescription;

import com.example.fix4you_api.Data.Models.CategoryDescription;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CategoryDescriptionCreationEvent extends ApplicationEvent {

    private final CategoryDescription categoryDescription;

    public CategoryDescriptionCreationEvent(Object source, CategoryDescription categoryDescription) {
        super(source);
        this.categoryDescription = categoryDescription;
    }
}
