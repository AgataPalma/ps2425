package com.example.fix4you_api.Service.ProfessionalsFee;

import com.example.fix4you_api.Data.MongoRepositories.PortfolioItemRepository;
import com.example.fix4you_api.Data.MongoRepositories.ProfessionalFeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfessionalsFeeServiceImpl implements ProfessionalsFeeService{

    private final ProfessionalFeeRepository professionalFeeRepository;

    @Override
    @Transactional
    public void deleteProfessionalFees(String professionalId) {
        professionalFeeRepository.deleteByProfessionalId(professionalId);
    }
}
