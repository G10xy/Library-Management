package it.gioxi.statemachine.event;


import it.gioxi.statemachine.model.BookEntity;
import it.gioxi.statemachine.model.UserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BookReturnedEvent extends ApplicationEvent {
    private final BookEntity book;
    private final UserEntity user;

    public BookReturnedEvent(Object source, BookEntity book, UserEntity user) {
        super(source);
        this.book = book;
        this.user = user;
    }
}
