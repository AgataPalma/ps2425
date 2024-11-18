package com.example.fix4you_api.Service.Professional.DTOs;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Models.CategoryDescription;
import com.example.fix4you_api.Data.Models.Language;
import com.example.fix4you_api.Data.Models.PaymentMethod;
import com.example.fix4you_api.Data.Models.PortfolioItem;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ProfessionalData {
    private String id;
    private String email;
    private LocalDateTime dateCreation;
    private EnumUserType userType;
    private String name;
    private String phoneNumber;
    private String location;
    private String description;
    private String nif;
    private List<Language> languages;
    private int locationsRange;
    private List<PaymentMethod> acceptedPayments;
    private List<CategoryDescription> categories;
    private List<PortfolioItem> portfolioItems;
    private float rating;
}
