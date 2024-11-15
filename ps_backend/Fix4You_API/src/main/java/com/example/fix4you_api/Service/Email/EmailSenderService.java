package com.example.fix4you_api.Service.Email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String body) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setText(body);
            message.setSubject(subject);
            mailSender.send(message);


        System.out.println("Mail Send...");
    }

    public void sendEmail(String toEmail, String subject, String body, byte[] attachment, String attachmentName) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true); // true indicates multipart message

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body);

        ByteArrayResource attachmentData = new ByteArrayResource(attachment);

        helper.addAttachment(attachmentName, attachmentData, "application/pdf");

        mailSender.send(mimeMessage);

        System.out.println("Mail Send...");
    }
}