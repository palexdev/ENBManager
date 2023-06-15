package io.github.palexdev.enbmanager.backend.settings.base;

public class BooleanSetting extends Setting<Boolean> {
    //================================================================================
    // Constructors
    //================================================================================
    public BooleanSetting(String name, String description, boolean defaultValue, Settings container) {
        super(name, description, defaultValue, container);
    }

    public static BooleanSetting of(String name, String description, boolean defaultValue, Settings container) {
        return new BooleanSetting(name, description, defaultValue, container);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Boolean get() {
        return container.prefs().getBoolean(name, defaultValue);
    }

    @Override
    public void set(Boolean val) {
        container.prefs().putBoolean(name, val);
    }
}
