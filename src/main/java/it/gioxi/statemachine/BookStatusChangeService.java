package it.gioxi.statemachine;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BookStatusChangeService {

    private final StateMachine<BookStates, BookEvents> stateMachine;
    private final InMemoryStateMachinePersist persister;
    private final BookService bookService;

    public void doAction(Long bookId, BookEvents event) throws Exception {
        var smContext  = persister.read(bookId.toString());
        var currentState = (smContext != null) ? smContext.getState() : stateMachine.getInitialState().getId();
        var eventFromContext = (smContext != null) ? smContext.getEvent() : null;

        Message<BookEvents> message = MessageBuilder.withPayload(event)
                .setHeader("bookId", bookId)
                .build();
        stateMachine.startReactively().subscribe();
        stateMachine.sendEvent(Mono.just(message)).subscribe();

        var newStateMachineStatus = stateMachine.getState().getId();
        StateMachineContext<BookStates, BookEvents> context = new DefaultStateMachineContext<>(newStateMachineStatus, eventFromContext, null, null);
        persister.write(context, bookId.toString());

        if(newStateMachineStatus == currentState) {
            throw new RuntimeException("It was not possible to do the action " + event);
        }
        bookService.updateStatus(bookId, newStateMachineStatus);
    }
}
