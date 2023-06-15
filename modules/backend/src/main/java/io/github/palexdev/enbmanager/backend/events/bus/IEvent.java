package io.github.palexdev.enbmanager.backend.events.bus;

@FunctionalInterface
public interface IEvent {
    Object data();
}
