package it.gioxi.statemachine.event;

import it.gioxi.statemachine.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookEventListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void handleBookReturnedEvent(BookReturnedEvent event) {
        log.info("Processing book return for: {}", event.getBook().getTitle());
        emailService.sendBookReturnConfirmation(event.getBook(), event.getUser());
    }
}
