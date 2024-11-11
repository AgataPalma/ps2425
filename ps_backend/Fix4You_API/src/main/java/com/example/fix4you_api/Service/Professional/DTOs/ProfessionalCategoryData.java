package com.example.fix4you_api.Service.Professional.DTOs;

import com.example.fix4you_api.Data.Enums.EnumCategories;
import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Enums.LanguageEnum;
import com.example.fix4you_api.Data.Enums.PaymentTypesEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ProfessionalCategoryData {
    private String id;
    private String email;
    private LocalDateTime dateCreation;
    private EnumUserType userType;
    private String name;
    private String phoneNumber;
    private String location;
    private byte[] profileImage;
    private String description;
    private String nif;
    private List<LanguageEnum> languages;
    private int locationsRange;
    private List<PaymentTypesEnum> acceptedPayments;
    private EnumCategories category;
    private boolean chargesTravels;
    private boolean providesInvoices;
    private float mediumPricePerService;
    private float rating;
}
