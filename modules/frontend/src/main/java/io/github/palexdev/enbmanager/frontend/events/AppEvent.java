package io.github.palexdev.enbmanager.frontend.events;

import io.github.palexdev.enbmanager.backend.events.Event;

public class AppEvent extends Event {

    //================================================================================
    // Impl
    //================================================================================
    public static class AppCloseEvent extends AppEvent {}

    public static class AppReadyEvent extends AppEvent {}
}
