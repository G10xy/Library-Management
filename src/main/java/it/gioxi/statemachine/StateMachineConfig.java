package it.gioxi.statemachine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
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
@EnableStateMachine
public class StateMachineConfig extends StateMachineConfigurerAdapter<BookStates, BookEvents> {

    private final BookService bookService;

    @Override
    public void configure(StateMachineStateConfigurer<BookStates, BookEvents> states) throws Exception {
        states
                .withStates()
                .initial(BookStates.AVAILABLE)
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
                .and()
                .withExternal()
                .source(BookStates.BORROWED).target(BookStates.ISSUED)
                .event(BookEvents.ISSUE_BOOK)
                .and()
                .withExternal()
                .source(BookStates.ISSUED).target(BookStates.AVAILABLE)
                .event(BookEvents.RETURN_BOOK)
                .action(sendEmailAction())
                .and()
                .withExternal()
                .source(BookStates.BORROWED).target(BookStates.OVERDUE)
                .event(BookEvents.MARK_OVERDUE);
    }

    @Bean
    public StateMachinePersister<BookStates, BookEvents, String> persister() {
        return new DefaultStateMachinePersister<>(new InMemoryStateMachinePersist());
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<BookStates, BookEvents> config) throws Exception {
        StateMachineListenerAdapter<BookStates, BookEvents> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<BookStates, BookEvents> from, State<BookStates, BookEvents> to) {
                log.info("stateChanged(from: %s, to: %s)", from == null ? "null" : from.getId(), to == null ? "null" : to.getId());
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
                Set<User> usersWhoBorrowed = bookService.findById(bookId).getUsersWhoBorrowed();
                for (var user : usersWhoBorrowed) {
                    // code to send email to the user
                }
            }
        };
    }

}

