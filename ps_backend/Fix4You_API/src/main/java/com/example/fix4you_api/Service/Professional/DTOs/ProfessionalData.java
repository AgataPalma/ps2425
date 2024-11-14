package com.example.fix4you_api.Service.Professional.DTOs;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Enums.LanguageEnum;
import com.example.fix4you_api.Data.Enums.PaymentTypesEnum;
import com.example.fix4you_api.Data.Models.CategoryDescription;
import com.example.fix4you_api.Data.Models.PortfolioFile;
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
    private List<LanguageEnum> languages;
    private int locationsRange;
    private List<PaymentTypesEnum> acceptedPayments;
    private List<CategoryDescription> categories;
    private List<PortfolioItem> portfolioItems;
    private float rating;
}
