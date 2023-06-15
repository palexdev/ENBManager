package io.github.palexdev.enbmanager.backend.events;

import io.github.palexdev.enbmanager.backend.events.bus.IEvent;

public abstract class Event implements IEvent {
    private final Object data;

    protected Event() {
        this(null);
    }

    public Event(Object data) {
        this.data = data;
    }

    @Override
    public Object data() {
        return data;
    }
}
