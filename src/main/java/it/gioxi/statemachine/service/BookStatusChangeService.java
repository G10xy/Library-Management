package it.gioxi.statemachine.service;

import it.gioxi.statemachine.model.enums.BookEvents;
import it.gioxi.statemachine.model.enums.BookStates;
import it.gioxi.statemachine.statemachine.InMemoryStateMachinePersist;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BookStatusChangeService {

    private final StateMachineFactory<BookStates, BookEvents> factory;
    private final InMemoryStateMachinePersist persister;
    private final BookService bookService;

    private StateMachine<BookStates, BookEvents> initStateMachine(Long bookId) throws Exception {
        StateMachine<BookStates, BookEvents> stateMachine = factory.getStateMachine(bookId.toString());
        StateMachineContext<BookStates, BookEvents> smContext = persister.read(bookId);
        if (smContext != null) {
            stateMachine.getStateMachineAccessor().doWithAllRegions(access -> access.resetStateMachine(smContext));
        } else {
            stateMachine.startReactively().subscribe();
        }
        return stateMachine;
    }

    private void persistStateMachine(Long bookId, StateMachine<BookStates, BookEvents> stateMachine) throws Exception {
        StateMachineContext<BookStates, BookEvents> context = new DefaultStateMachineContext<>(stateMachine.getState().getId(), null, null, null);
        persister.write(context, bookId);
    }

    public void doAction(Long bookId, BookEvents event) throws Exception {
        StateMachine<BookStates, BookEvents> stateMachine = initStateMachine(bookId);

        Message<BookEvents> message = MessageBuilder.withPayload(event)
                .setHeader("bookId", bookId)
                .build();
        stateMachine.sendEvent(Mono.just(message)).subscribe();

        BookStates newState = stateMachine.getState().getId();
        persistStateMachine(bookId, stateMachine);

        if (!stateMachine.hasStateMachineError()) {
            bookService.updateStatus(bookId, newState);
        } else {
            throw new RuntimeException("It was not possible to do the action " + event);
        }
    }
}
