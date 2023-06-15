package io.github.palexdev.enbmanager.backend.games;

import io.github.palexdev.enbmanager.backend.settings.base.GameSettings;
import io.github.palexdev.enbmanager.backend.utils.OSUtils;

import java.io.InputStream;

public interface Game {

    InputStream getIcon();

    String getName();

    String getExeName();

    GameSettings getSettings();

    default boolean isRunning() {
        return OSUtils.isProcessRunning(getExeName());
    }
}
