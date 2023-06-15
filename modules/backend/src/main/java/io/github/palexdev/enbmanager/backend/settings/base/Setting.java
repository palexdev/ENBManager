package io.github.palexdev.enbmanager.backend.settings.base;

public abstract class Setting<T> {
    //================================================================================
    // Properties
    //================================================================================
    protected final String name;
    protected final String description;
    protected final T defaultValue;
    protected final Settings container;
    protected boolean avoidEmpty = false;

    //================================================================================
    // Constructors
    //================================================================================
    protected Setting(String name, String description, T defaultValue, Settings container) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.container = container;
    }

    //================================================================================
    // Abstract Methods
    //================================================================================
    public abstract T get();

    public abstract void set(T val);

    //================================================================================
    // Methods
    //================================================================================
    public void reset() {
        set(defaultValue);
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public T defValue() {
        return defaultValue;
    }

    public Settings container() {
        return container;
    }

    public boolean isAvoidEmpty() {
        return avoidEmpty;
    }

    public Setting<T> setAvoidEmpty(boolean avoidEmpty) {
        this.avoidEmpty = avoidEmpty;
        return this;
    }
}
