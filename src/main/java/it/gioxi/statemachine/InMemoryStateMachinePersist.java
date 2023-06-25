package it.gioxi.statemachine;

import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryStateMachinePersist implements StateMachinePersist<BookStates, BookEvents, String> {

    private final Map<String, StateMachineContext<BookStates, BookEvents>> storage = new HashMap<>();

    @Override
    public void write(StateMachineContext<BookStates, BookEvents> context, String contextObj) throws Exception {
        storage.put(contextObj, context);
    }

    @Override
    public StateMachineContext<BookStates, BookEvents> read(String contextObj) throws Exception {
        return storage.get(contextObj);
    }
}

