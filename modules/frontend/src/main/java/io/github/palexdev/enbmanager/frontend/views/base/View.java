package io.github.palexdev.enbmanager.frontend.views.base;

import io.github.palexdev.enbmanager.backend.events.bus.SimpleEventBus;
import io.github.palexdev.enbmanager.frontend.events.AppEvent;
import io.inverno.core.annotation.Bean;
import io.inverno.core.annotation.Wrapper;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class View<P extends Pane> {
    //================================================================================
    // Properties
    //================================================================================
    protected P root;
    protected final SimpleEventBus events;

    //================================================================================
    // Constructors
    //================================================================================
    protected View(SimpleEventBus events) {
        this.events = events;
        events.subscribe(AppEvent.AppReadyEvent.class, e -> onAppReadyEvent());
    }

    //================================================================================
    // Abstract Methods
    //================================================================================
    protected abstract P build();

    //================================================================================
    // Methods
    //================================================================================
    public Region toRegion() {
        return root;
    }

    protected void onAppReadyEvent() {
        root = build();
    }

    //================================================================================
    // Sockets
    //================================================================================
    @Bean
    @Wrapper
    @SuppressWarnings("rawtypes")
    public static class Views implements Supplier<Map<Class<? extends View>, View<?>>> {
        private final Map<Class<? extends View>, View<?>> views;

        public Views(List<View<?>> views) {
            this.views = views.stream()
                .collect(Collectors.toMap(
                    View::getClass,
                    v -> v
                ));
        }

        @Override
        public Map<Class<? extends View>, View<?>> get() {
            return views;
        }
    }
}
