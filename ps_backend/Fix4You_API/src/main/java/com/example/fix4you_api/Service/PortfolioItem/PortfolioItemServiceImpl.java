package com.example.fix4you_api.Service.PortfolioItem;

import com.example.fix4you_api.Data.MongoRepositories.PortfolioItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PortfolioItemServiceImpl implements PortfolioItemService {

    private final PortfolioItemRepository portfolioItemRepository;

    @Override
    @Transactional
    public void deletePortfolioItemsForProfessional(String professionalId) {
        portfolioItemRepository.deleteByProfessionalId(professionalId);
    }

}