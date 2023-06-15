package io.github.palexdev.enbmanager.backend.settings.base;

public class StringSetting extends Setting<String> {

    //================================================================================
    // Constructors
    //================================================================================
    public StringSetting(String name, String description, String defaultValue, Settings container) {
        super(name, description, defaultValue, container);
    }

    public static StringSetting of(String name, String description, String defaultValue, Settings container) {
        return new StringSetting(name, description, defaultValue, container);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public String get() {
        return container.prefs().get(name, defaultValue);
    }

    @Override
    public void set(String val) {
        container.prefs().put(name, val);
    }
}
