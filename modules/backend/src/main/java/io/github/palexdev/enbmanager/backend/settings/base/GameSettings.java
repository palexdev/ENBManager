package io.github.palexdev.enbmanager.backend.settings.base;

public abstract class GameSettings extends Settings {
    //================================================================================
    // Settings
    //================================================================================
    public final StringSetting path = registerString("path", "Game's path from last session", "");
}
