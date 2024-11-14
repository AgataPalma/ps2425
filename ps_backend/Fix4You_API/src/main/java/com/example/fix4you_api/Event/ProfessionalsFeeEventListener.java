package com.example.fix4you_api.Event;

import com.example.fix4you_api.Controllers.SseNotificationController;
import com.example.fix4you_api.Data.Models.ProfessionalsFee;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ProfessionalsFeeEventListener {

    private final SseNotificationController sseNotificationController;

    public ProfessionalsFeeEventListener(SseNotificationController sseNotificationController) {
        this.sseNotificationController = sseNotificationController;
    }

    @EventListener
    public void handleProfessionalsFeeEvent(ProfessionalsFeeEvent event) {
        ProfessionalsFee fee = event.getProfessionalsFee();
        String currentUserId = getCurrentUserId();
        String notification = String.format(
                "Payment Notification. " +
                        "Fee amount: %s. " +
                        "Number of services: %s. " +
                        "Related period: %s. " +
                        "Payment date: %s. " +
                        "Payment status: %s",
                fee.getValue(), fee.getNumberServices(), fee.getRelatedMonthYear(), fee.getPaymentDate(), fee.getPaymentStatus());

        if (fee.getProfessionalId().equals(currentUserId)) {
            sseNotificationController.sendNotificationToClients(notification);
        }
    }

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
