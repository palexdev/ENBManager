package io.github.palexdev.enbmanager.backend.utils;

import io.github.palexdev.enbmanager.backend.games.Game;
import io.github.palexdev.enbmanager.backend.settings.base.GameSettings;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class GameUtils {

    //================================================================================
    // Constructors
    //================================================================================
    private GameUtils() {}

    //================================================================================
    // Static Methods
    //================================================================================

    /**
     * For the given game, retrieves its settings by using {@link Game#getSettings()}, and checks whether the game
     * executable is still present on the previously saved path.
     * <p></p>
     * For convenience the return value is an {@link Optional} that contains the path if invalid otherwise is empty.
     */
    public static Optional<Path> detectExecutable(Game game) {
        GameSettings gSettings = game.getSettings();
        Path path = Path.of(gSettings.path.get());
        return Files.isDirectory(path) && Files.exists(path.resolve(game.getExeName())) ?
            Optional.of(path) :
            Optional.empty();
    }
}
