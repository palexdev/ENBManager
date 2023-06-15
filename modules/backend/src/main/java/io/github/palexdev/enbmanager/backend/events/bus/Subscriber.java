package io.github.palexdev.enbmanager.backend.events.bus;

import io.github.palexdev.enbmanager.backend.events.Event;

@FunctionalInterface
public interface Subscriber<E extends Event> {
    void handle(E event);
}
