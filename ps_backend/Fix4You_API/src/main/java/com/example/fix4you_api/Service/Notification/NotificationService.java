package com.example.fix4you_api.Service.Notification;

import com.example.fix4you_api.Data.Models.Notification;
import com.example.fix4you_api.Data.MongoRepositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByProfessionalId(String professionalId) {
        return notificationRepository.findByProfessionalId(professionalId);
    }

    public void deleteNotification(String id) {
        notificationRepository.deleteById(id);
    }

    public void markAsRead(String id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
