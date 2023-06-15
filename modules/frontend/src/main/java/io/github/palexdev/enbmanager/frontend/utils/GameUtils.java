package io.github.palexdev.enbmanager.frontend.utils;

import io.github.palexdev.enbmanager.backend.fp.Causes;
import io.github.palexdev.enbmanager.backend.fp.Result;
import io.github.palexdev.enbmanager.backend.games.Game;
import io.github.palexdev.enbmanager.frontend.model.ENBManagerModel;
import io.github.palexdev.enbmanager.frontend.settings.AppSettings;

import java.nio.file.Path;

public class GameUtils {

    //================================================================================
    // Constructors
    //================================================================================
    private GameUtils() {}

    //================================================================================
    // Static Methods
    //================================================================================
    public static Result<Boolean> setGame(ENBManagerModel model, AppSettings settings, Game game, Path path, Boolean remember) {
        if (game.isRunning()) return Result.err(Causes.cause("Game %s is running!".formatted(game.getName())));
        model.setGame(game);
        model.setPath(path);
        if (remember != null) {
            if (remember) {
                settings.lastGame.set(game.getName());
            } else {
                settings.lastGame.reset();
            }
        }
        return Result.ok(true);
    }
}
