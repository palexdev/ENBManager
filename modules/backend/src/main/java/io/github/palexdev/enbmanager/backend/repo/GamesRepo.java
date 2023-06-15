package io.github.palexdev.enbmanager.backend.repo;

import io.github.palexdev.enbmanager.backend.Dirs;
import io.github.palexdev.enbmanager.backend.events.ModelEvent;
import io.github.palexdev.enbmanager.backend.events.bus.SimpleEventBus;
import io.github.palexdev.enbmanager.backend.fp.Result;
import io.github.palexdev.enbmanager.backend.games.Game;
import io.inverno.core.annotation.Bean;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Bean
public class GamesRepo {
    //================================================================================
    // Properties
    //================================================================================
    private final SimpleEventBus events;
    private final Dirs dirs;
    private final Map<Game, ConfigsRepo> repos = new HashMap<>();

    //================================================================================
    // Constructors
    //================================================================================
    public GamesRepo(SimpleEventBus events, Dirs dirs) {
        this.events = events;
        this.dirs = dirs;
    }

    //================================================================================
    // Methods
    //================================================================================
    public Result<Boolean> loadConfig(Game game, Path gamePath, Config config) {
        return getConfigsRepo(game).fold(Result::err, r -> r.load(gamePath, config));
    }

    public Result<Boolean> saveConfig(Game game, Path gamePath, String name, Collection<? extends Path> files) {
        return getConfigsRepo(game).fold(Result::err, r -> r.save(gamePath, name, files));
    }

    public Result<Boolean> deleteConfig(Game game, Config config) {
        return getConfigsRepo(game).fold(Result::err, r -> r.delete(config));
    }

    public void refreshConfigs(Game game) {
        getConfigsRepo(game).onSuccess(ConfigsRepo::detectConfigs);
    }

    public Result<ConfigsRepo> getConfigsRepo(Game game) {
        if (repos.containsKey(game)) return Result.ok(repos.get(game));
        ConfigsRepo repo = new ConfigsRepo(this, game);
        return repo.init(getRepoPath())
            .map(() -> {
                repos.put(game, repo);
                return repo;
            });
    }

    protected void onConfigsChanged(Collection<Config> configs) {
        events.publish(new ModelEvent.ConfigsChangedEvent(configs));
    }

    //================================================================================
    // Getters
    //================================================================================
    public Path getRepoPath() {
        return dirs.configPath().get();
    }
}
