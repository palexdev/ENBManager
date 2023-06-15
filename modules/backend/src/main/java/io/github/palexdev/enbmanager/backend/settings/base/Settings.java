package io.github.palexdev.enbmanager.backend.settings.base;

import io.inverno.core.annotation.Bean;
import io.inverno.core.annotation.Wrapper;

import java.util.*;
import java.util.function.Supplier;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

public abstract class Settings {
    //================================================================================
    // Properties
    //================================================================================
    protected final Preferences prefs;
    // DB with all settings, must be static to create a bean socket
    protected static final Map<Class<? extends Settings>, Set<Setting<?>>> settings = new HashMap<>();

    //================================================================================
    // Constructors
    //================================================================================
    protected Settings() {
        prefs = init();
    }

    //================================================================================
    // Abstract Methods
    //================================================================================
    protected abstract String node();

    //================================================================================
    // Methods
    //================================================================================
    protected Preferences init() {
        return Preferences.userRoot().node(node());
    }

    protected StringSetting registerString(String name, String description, String defaultValue) {
        StringSetting setting = StringSetting.of(name, description, defaultValue, this);
        Set<Setting<?>> set = settings.computeIfAbsent(getClass(), c -> new LinkedHashSet<>());
        set.add(setting);
        return setting;
    }

    protected BooleanSetting registerBoolean(String name, String description, boolean defaultValue) {
        BooleanSetting setting = BooleanSetting.of(name, description, defaultValue, this);
        Set<Setting<?>> set = settings.computeIfAbsent(getClass(), c -> new LinkedHashSet<>());
        set.add(setting);
        return setting;
    }

    protected NumberSetting<Double> registerDouble(String name, String description, double defaultValue) {
        NumberSetting<Double> setting = NumberSetting.forDouble(name, description, defaultValue, this);
        Set<Setting<?>> set = settings.computeIfAbsent(getClass(), c -> new LinkedHashSet<>());
        set.add(setting);
        return setting;
    }

    public void reset() {
        Optional.ofNullable(settings.get(getClass()))
            .ifPresent(s -> s.forEach(Setting::reset));
    }

    public void onChange(PreferenceChangeListener pcl) {
        prefs.addPreferenceChangeListener(pcl);
    }

    public void removeOnChange(PreferenceChangeListener pcl) {
        prefs.removePreferenceChangeListener(pcl);
    }

    //================================================================================
    // Static Methods
    //================================================================================
    public static String root() {
        return "/io/github/palexdev/enbmanager";
    }

    //================================================================================
    // Getters
    //================================================================================
    protected Preferences prefs() {
        return prefs;
    }

    public Set<Setting<?>> getSettings(Class<? extends Settings> c) {
        return Optional.ofNullable(settings.get(c))
            .map(Collections::unmodifiableSet)
            .orElse(Collections.emptySet());
    }

    //================================================================================
    // Sockets
    //================================================================================
    @Bean
    @Wrapper
    public static class SettingsAll implements Supplier<Map<Class<? extends Settings>, Set<Setting<?>>>> {
        private final Map<Class<? extends Settings>, Set<Setting<?>>> settings;

        public SettingsAll() {settings = Settings.settings;}

        @Override
        public Map<Class<? extends Settings>, Set<Setting<?>>> get() {
            return settings;
        }
    }
}
