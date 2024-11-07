package com.example.fix4you_api.Data.MongoRepositories;

import com.example.fix4you_api.Data.Models.PortfolioItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PortfolioItemRepository extends MongoRepository<PortfolioItem, String> {
    List<PortfolioItem> findByProfessionalId(String id);
    void deleteByProfessionalId(String professionalId);
}
