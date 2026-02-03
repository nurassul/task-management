package com.project.notificationservice.api.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public void sendTaskNotification(String to, Long id, String taskStatus) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject("Уведомление по задаче: " + id);

            String htmlBody = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2 style='color: #004a99;'>Обновление в Task Management</h2>" +
                    "<p>Привет! У нас есть новости по твоей задаче:</p>" +
                    "<div style='background-color: #f4f4f4; padding: 15px; border-left: 4px solid #004a99;'>" +
                    "<p><strong>Статус:</strong> <span style='color: green;'>" + taskStatus + "</span></p>" +
                    "</div>" +
                    "<br>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlBody, true);

            mailSender.send(mimeMessage);
            log.info("Email успешно отправлен на адрес: {}", to);

        } catch (MessagingException e) {
            log.error("Ошибка при формировании/отправке email: {}", e.getMessage());
        }
    }
}
