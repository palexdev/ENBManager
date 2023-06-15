package io.github.palexdev.enbmanager.backend.events;

import io.github.palexdev.enbmanager.backend.repo.Config;

import java.util.Collection;

@SuppressWarnings("unchecked")
public class ModelEvent extends Event {

    //================================================================================
    // Constructors
    //================================================================================
    public ModelEvent(Object data) {super(data);}

    //================================================================================
    // Impl
    //================================================================================
    public static class ConfigsChangedEvent extends ModelEvent {

        public ConfigsChangedEvent(Collection<Config> configs) {
            super(configs);
        }

        @Override
        public Collection<Config> data() {
            return (Collection<Config>) super.data();
        }
    }
}
