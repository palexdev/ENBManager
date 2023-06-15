package io.github.palexdev.enbmanager.frontend.settings;

import io.github.palexdev.enbmanager.backend.settings.base.BooleanSetting;
import io.github.palexdev.enbmanager.backend.settings.base.NumberSetting;
import io.github.palexdev.enbmanager.backend.settings.base.Settings;
import io.github.palexdev.enbmanager.backend.settings.base.StringSetting;
import io.github.palexdev.mfxcomponents.theming.MaterialThemes;
import io.inverno.core.annotation.Bean;
import javafx.application.Application;

import java.util.Map;

@Bean
public class AppSettings extends Settings {
    //================================================================================
    // Settings
    //================================================================================
    public final NumberSetting<Double> windowWidth = registerDouble("window.width", "", 1024.0);
    public final NumberSetting<Double> windowHeight = registerDouble("window.height", "", 720.0);
    public final StringSetting lastGame = registerString("last.game", "Last session's game", "");
    public final StringSetting lastTheme = registerString("last.theme", "Last session's theme", MaterialThemes.INDIGO_LIGHT.name());
    public final BooleanSetting lastThemeMode = registerBoolean("last.theme.mode", "Last session's theme mode", false);

    private final Application.Parameters parameters;
    private Boolean cleanup = null;
    private Boolean debug = null;
    private Boolean resetSettings = null;
    private Boolean invalidateThemesCache = null;

    //================================================================================
    // Constructors
    //================================================================================
    public AppSettings(Application.Parameters parameters) {
        this.parameters = parameters;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected String node() {
        return root();
    }

    //================================================================================
    // Getters
    //================================================================================
    public boolean isCleanup() {
        if (cleanup == null) {
            Map<String, String> named = parameters.getNamed();
            cleanup = Boolean.parseBoolean(named.getOrDefault("cleanup", "false"));
        }
        return cleanup;
    }

    public boolean isDebug() {
        if (debug == null) {
            Map<String, String> named = parameters.getNamed();
            debug = Boolean.parseBoolean(named.getOrDefault("debug", "false"));
        }
        return debug;
    }

    public boolean isResetSettings() {
        if (resetSettings == null) {
            Map<String, String> named = parameters.getNamed();
            resetSettings = Boolean.parseBoolean(named.getOrDefault("reset-settings", "false"));
        }
        return resetSettings;
    }

    public boolean isInvalidateThemesCache() {
        if (invalidateThemesCache == null) {
            Map<String, String> named = parameters.getNamed();
            invalidateThemesCache = Boolean.parseBoolean(named.getOrDefault("invalidate-themes-cache", "false"));
        }
        return invalidateThemesCache;
    }
}
