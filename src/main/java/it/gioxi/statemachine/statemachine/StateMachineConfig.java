package it.gioxi.statemachine.statemachine;

import it.gioxi.statemachine.service.BookService;
import it.gioxi.statemachine.model.UserEntity;
import it.gioxi.statemachine.model.enums.BookEvents;
import it.gioxi.statemachine.model.enums.BookStates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<BookStates, BookEvents> {

    private final BookService bookService;
    private final InMemoryStateMachinePersist persister;

    @Override
    public void configure(StateMachineStateConfigurer<BookStates, BookEvents> states) throws Exception {
        states
                .withStates()
                .initial(BookStates.AVAILABLE)
                .end(BookStates.ISSUED)
                .states(EnumSet.allOf(BookStates.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<BookStates, BookEvents> transitions) throws Exception {
        transitions
                .withExternal()
                .source(BookStates.AVAILABLE).target(BookStates.BORROWED)
                .event(BookEvents.BORROW_BOOK)
                .guard(bookIsAvailableGuard())
                .and()
                .withExternal()
                .source(BookStates.BORROWED).target(BookStates.AVAILABLE)
                .event(BookEvents.RETURN_BOOK)
                .action(sendEmailAction())
                .and()
                .withExternal()
                .source(BookStates.BORROWED).target(BookStates.OVERDUE)
                .event(BookEvents.MARK_OVERDUE)
                .and()
                .withExternal()
                .source(BookStates.OVERDUE).target(BookStates.AVAILABLE)
                .event(BookEvents.RETURN_BOOK)
                .action(sendEmailAction())
                .and()
                .withExternal()
                .source(BookStates.BORROWED).target(BookStates.ISSUED)
                .event(BookEvents.ISSUE_BOOK);
    }

    @Bean
    public StateMachinePersister<BookStates, BookEvents, Long> persister() {
        return new DefaultStateMachinePersister<>(persister);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<BookStates, BookEvents> config) throws Exception {
        StateMachineListenerAdapter<BookStates, BookEvents> adapter = new StateMachineListenerAdapter<>() {

            private StateMachine<BookStates, BookEvents> stateMachine;

            @Override
            public void stateChanged(State<BookStates, BookEvents> from, State<BookStates, BookEvents> to) {
                log.info("stateChanged from: " + (from == null ? null : from.getId().name()) + " to: " + to.getId().name());
            }

            @Override
            public void stateContext(StateContext<BookStates, BookEvents> stateContext) {
                this.stateMachine = stateContext.getStateMachine();
            }

            @Override
            public void eventNotAccepted(Message event) {
                stateMachine.setStateMachineError(new RuntimeException());
                log.error("Transition rejected");
            }

        };

        config
                .withConfiguration()
                .autoStartup(true)
                .listener(adapter);
    }

    @Bean
    public Guard<BookStates, BookEvents> bookIsAvailableGuard() {
        return context -> {
            Long bookId = context.getMessageHeaders().get("bookId", Long.class);
            if (bookId != null) {
                return bookService.findById(bookId).getState().name().equals(BookStates.AVAILABLE.name());
            }
            return false;
        };
    }

    @Bean
    public Action<BookStates, BookEvents> sendEmailAction() {
        return context -> {
            Long bookId = context.getMessageHeaders().get("bookId", Long.class);
            if (bookId != null) {
                Set<UserEntity> usersWhoBorrowed = bookService.findById(bookId).getUsersWhoBorrowed();
                for (var user : usersWhoBorrowed) {
                    // code to send email to the user
                }
            }
        };
    }

}

