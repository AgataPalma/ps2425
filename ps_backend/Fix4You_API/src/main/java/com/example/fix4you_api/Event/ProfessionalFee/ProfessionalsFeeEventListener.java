package com.example.fix4you_api.Event.ProfessionalFee;

import com.example.fix4you_api.Controllers.SseNotificationController;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.Models.ProfessionalsFee;
import com.example.fix4you_api.Service.Email.EmailSenderService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfessionalsFeeEventListener {

    private final SseNotificationController sseNotificationController;
    private final EmailSenderService emailSenderService;

    @EventListener
    public void handleFeeCreationEvent(FeeCreationEvent event) {
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

    @EventListener
    public void handleFeePaymentCompletionEvent(FeePaymentCompletionEvent event) throws MessagingException {
        ProfessionalsFee fee = event.getProfessionalsFee();
        Professional professional = event.getProfessional();
        emailSenderService.sendEmail(
                professional.getEmail(), "Payment Invoice", "",
                fee.getInvoice(), String.format("Invoice %s ", fee.getPaymentDate())
        );
    }

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
