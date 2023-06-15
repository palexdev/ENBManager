package io.github.palexdev.enbmanager.backend.games;

import io.github.palexdev.enbmanager.backend.settings.base.StringSetting;
import io.github.palexdev.enbmanager.backend.utils.GameUtils;
import io.inverno.core.annotation.Bean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Bean
public class GamesManager {
    //================================================================================
    // Properties
    //================================================================================
    private final List<Game> games;

    //================================================================================
    // Constructors
    //================================================================================
    public GamesManager(List<Game> games) {
        this.games = games;
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Queries the app settings to check whether a user chose a game in a previous session, and he decided to save the
     * preference for future sessions.
     * <p>
     * If the previous choice is detected, there's an additional check performed by {@link GameUtils#detectExecutable(Game)}
     * before returning.
     */
    public Optional<Game> detectLastGame(StringSetting lastGameSetting) {
        String s = lastGameSetting.get();
        if (s == null || s.isBlank()) return Optional.empty();
        Optional<Game> opt = games.stream()
            .filter(g -> g.getName().equals(s))
            .findFirst();
        if (opt.isPresent()) {
            Game game = opt.get();
            if (GameUtils.detectExecutable(game).isEmpty()) return Optional.empty();
        }
        return opt;
    }


    //================================================================================
    // Getters
    //================================================================================

    /**
     * @return an unmodifiable list containing all the supported games
     */
    public List<Game> getGames() {
        return Collections.unmodifiableList(games);
    }
}
