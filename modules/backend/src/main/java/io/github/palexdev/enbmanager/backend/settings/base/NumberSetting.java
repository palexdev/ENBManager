package io.github.palexdev.enbmanager.backend.settings.base;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.prefs.Preferences;

public class NumberSetting<N extends Number> extends Setting<N> {
    protected Function<Preferences, N> fetcher;
    protected BiConsumer<Preferences, N> updater;

    //================================================================================
    // Constructors
    //================================================================================
    protected NumberSetting(String name, String description, N defaultValue, Settings container) {
        super(name, description, defaultValue, container);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public N get() {
        return fetcher.apply(container.prefs());
    }

    @Override
    public void set(N val) {
        updater.accept(container.prefs(), val);
    }

    //================================================================================
    // Impl
    //================================================================================
    public static NumberSetting<Double> forDouble(String name, String description, double defaultVal, Settings container) {
        NumberSetting<Double> setting = new NumberSetting<>(name, description, defaultVal, container);
        setting.setFetcher(p -> p.getDouble(name, defaultVal));
        setting.setUpdater((p, v) -> p.putDouble(name, v));
        return setting;
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public Function<Preferences, N> getFetcher() {
        return fetcher;
    }

    public void setFetcher(Function<Preferences, N> fetcher) {
        this.fetcher = fetcher;
    }

    public BiConsumer<Preferences, N> getUpdater() {
        return updater;
    }

    public void setUpdater(BiConsumer<Preferences, N> updater) {
        this.updater = updater;
    }
}
