package it.gioxi.statemachine.statemachine;

import it.gioxi.statemachine.model.enums.BookEvents;
import it.gioxi.statemachine.model.enums.BookStates;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryStateMachinePersist implements StateMachinePersist<BookStates, BookEvents, Long> {

    private final Map<Long, StateMachineContext<BookStates, BookEvents>> storage = new HashMap<>();

    @Override
    public void write(StateMachineContext<BookStates, BookEvents> context, Long contextObj) throws Exception {
        storage.put(contextObj, context);
    }

    @Override
    public StateMachineContext<BookStates, BookEvents> read(Long contextObj) throws Exception {
        return storage.get(contextObj);
    }
}

