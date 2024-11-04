package com.example.fix4you_api.Service.CategoryDescription;

import com.example.fix4you_api.Data.Models.CategoryDescription;
import com.example.fix4you_api.Data.Models.Client;
import com.example.fix4you_api.Data.MongoRepositories.CategoryDescriptionRepository;
import com.example.fix4you_api.Data.MongoRepositories.ClientRepository;
import com.example.fix4you_api.Service.Client.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CategoryDescriptionServiceImpl implements CategoryDescriptionService {

    private final CategoryDescriptionRepository categoryDescriptionRepository;

    @Override
    public CategoryDescription createCategoryDescription(CategoryDescription categoryDescription) {
        return categoryDescriptionRepository.save(categoryDescription);
    }

}
