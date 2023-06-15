package io.github.palexdev.enbmanager.frontend.events;

import io.github.palexdev.enbmanager.backend.events.Event;
import io.github.palexdev.enbmanager.frontend.views.base.View;

public class ViewEvent extends Event {

    //================================================================================
    // Constructors
    //================================================================================
    public ViewEvent() {}

    public ViewEvent(Object data) {super(data);}

    //================================================================================
    // Impl
    //================================================================================
    public static class ViewSwitchEvent extends ViewEvent {
        public ViewSwitchEvent(View<?> view) {
            super(view);
        }

        @Override
        public View<?> data() {
            return (View<?>) super.data();
        }
    }

    public static class ShowToastEvent extends ViewEvent {
        public ShowToastEvent(String message) {
            super(message);
        }

        @Override
        public String data() {
            return (String) super.data();
        }
    }
}
