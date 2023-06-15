package io.github.palexdev.enbmanager.backend.events.bus;

import io.github.palexdev.enbmanager.backend.events.Event;

public interface IEventBus {

    <E extends Event> void subscribe(Class<E> evt, Subscriber<E> subscriber);

    <E extends Event> void unsubscribe(Class<E> evt, Subscriber<E> subscriber);

    <E extends Event> void publish(E event);
}
