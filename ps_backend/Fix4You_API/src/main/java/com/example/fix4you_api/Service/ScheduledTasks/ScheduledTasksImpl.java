package com.example.fix4you_api.Service.ScheduledTasks;

import com.example.fix4you_api.Service.ProfessionalsFee.ProfessionalsFeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledTasksImpl implements ScheduledTasks {

    private final ProfessionalsFeeService professionalsFeeService;

    // Executa no início de cada mês
    @Scheduled(cron = "0 0 0 1 * ?")
    public void runMonthlyFeeCheck() {
        professionalsFeeService.checkAndCreateMonthlyFees();
    }
}