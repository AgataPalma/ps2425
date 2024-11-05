package com.example.fix4you_api.Data.Enums;

public enum EnumCategories {
    CLEANING,
    PLUMBING,
    ELECTRICAL,
    GARDENING,
    PAINTING,
    OTHER;

    public boolean contains(String category) {
        if(category.contains("CLEANING") || category.contains("PLUMBING") || category.contains("ELECTRICAL")
            || category.contains("GARDENING") || category.contains("PAINTING") || category.contains("OTHER")){
            return true;
        }
        return false;
    }
}
