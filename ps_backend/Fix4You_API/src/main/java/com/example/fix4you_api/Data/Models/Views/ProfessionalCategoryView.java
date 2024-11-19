package com.example.fix4you_api.Data.Models.Views;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Models.Language;
import com.example.fix4you_api.Data.Models.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Document(collection = "ProfessionalCategoryView")
public class ProfessionalCategoryView {
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
    private List<Language> languages;
    //private Integer locationsRange;
    private List<PaymentMethod> acceptedPayments;
    private Float rating;
    private List<CategoryDescription> categoryDescriptions;

    @Data
    public static class CategoryDescription {
        private Boolean chargesTravels;
        //private Boolean providesInvoices;
        private Float mediumPricePerService;
        private Category category;

        @Data
        public static class Category {
            private String id;
            private String name;
        }
    }
}
