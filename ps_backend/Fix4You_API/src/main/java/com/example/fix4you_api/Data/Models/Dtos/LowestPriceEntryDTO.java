package com.example.fix4you_api.Data.Models.Dtos;

public class LowestPriceEntryDTO {
    private String userId;
    private String categoryName;
    private String price;

    // Default constructor
    public LowestPriceEntryDTO() {}

    public LowestPriceEntryDTO (String userId, String categoryName, String price) {
        this.userId = userId;
        this.categoryName = categoryName;
        this.price = price;
    }

    public String getUserId() {
        return userId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getPrice() {
        return price;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
