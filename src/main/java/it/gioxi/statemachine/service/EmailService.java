package it.gioxi.statemachine.service;

import it.gioxi.statemachine.model.BookEntity;
import it.gioxi.statemachine.model.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendBookReturnConfirmation(BookEntity book, UserEntity user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Book Return Confirmation");
            message.setText(String.format(
                    "Dear %s %s,\n\n" +
                            "Thank you for returning the book '%s'.\n\n" +
                            "Best regards",
                    user.getName(), user.getSurname(), book.getTitle()
            ));

            mailSender.send(message);
            log.info("Return confirmation email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", user.getEmail(), e.getMessage());
        }
    }
}
