package io.github.palexdev.enbmanager.backend.events.bus;

import io.github.palexdev.enbmanager.backend.events.Event;
import io.inverno.core.annotation.Bean;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

@Bean
public class SimpleEventBus implements IEventBus {
    //================================================================================
    // Properties
    //===============================================================================
    private final Map<Class<? extends Event>, Set<Subscriber<Event>>> subscribers = new WeakHashMap<>();

    //================================================================================
    // Methods
    //================================================================================
    protected <E extends Event> void notifySubscribers(E event) {
        Set<Subscriber<Event>> subscribers = this.subscribers.get(event.getClass());
        if (subscribers == null || subscribers.isEmpty()) return;
        for (Subscriber<Event> s : subscribers) {
            s.handle(event);
        }
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Event> void subscribe(Class<E> evt, Subscriber<E> subscriber) {
        Set<Subscriber<Event>> set = subscribers.computeIfAbsent(evt, c -> new LinkedHashSet<>());
        set.add((Subscriber<Event>) subscriber);
    }

    @Override
    public <E extends Event> void unsubscribe(Class<E> evt, Subscriber<E> subscriber) {
        Set<Subscriber<Event>> set = subscribers.get(evt);
        if (set == null || set.isEmpty()) return;
        set.remove(subscriber);
    }

    @Override
    public <E extends Event> void publish(E event) {
        notifySubscribers(event);
    }
}
