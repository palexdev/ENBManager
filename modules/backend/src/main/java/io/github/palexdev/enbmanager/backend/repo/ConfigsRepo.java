package io.github.palexdev.enbmanager.backend.repo;

import io.github.palexdev.enbmanager.backend.fp.Cause;
import io.github.palexdev.enbmanager.backend.fp.Causes;
import io.github.palexdev.enbmanager.backend.fp.Result;
import io.github.palexdev.enbmanager.backend.games.Game;
import io.github.palexdev.enbmanager.backend.utils.FileUtils;
import io.inverno.core.v1.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ConfigsRepo {
    //================================================================================
    // Properties
    //================================================================================
    private final GamesRepo repo;
    private final Game game;
    private Path configsPath;
    private final Map<Path, Config> configs = new LinkedHashMap<>();
    private final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    //================================================================================
    // Constructors
    //================================================================================
    public ConfigsRepo(GamesRepo repo, Game game) {
        this.repo = repo;
        this.game = game;
    }

    //================================================================================
    // Methods
    //================================================================================
    public Result<Path> init(Path repoPath) {
        if (configsPath == null) {
            try {
                Path path = repoPath.resolve(game.getName());
                if (!Files.isDirectory(path)) Files.createDirectories(path);
                configsPath = path;
                detectConfigs();
            } catch (IOException ex) {
                return Result.err(Causes.cause("Failed to create configs repo for game %s because: %s".formatted(game.getName(), ex.getMessage())));
            }
        }
        return Result.ok(configsPath);
    }

    public Result<Boolean> load(Path gamePath, Config config) {
        if (!configs.containsKey(config.path())) return Result.err(Causes.IRRELEVANT);
        return doLoad(gamePath, config);
    }

    public Result<Boolean> save(Path gamePath, String name, Collection<? extends Path> files) {
        Path path = configsPath.resolve(name);
        if (configs.containsKey(path)) return Result.err(Causes.EXISTING);
        Config config = Config.from(path).addFiles(files);
        return doSave(gamePath, config)
            .onSuccessDo(() -> {
                configs.put(path, config);
                configsChanged();
            });
    }

    public Result<Boolean> overwrite(Path gamePath, String name, Collection<? extends Path> files) {
        Path path = configsPath.resolve(name);
        if (configs.containsKey(path)) {
            Result<Boolean> delete = doDelete(configs.get(path));
            if (delete.isFailure())
                return Result.err(Causes.cause("Failed to overwrite config %s because: %s".formatted(name, delete.onError(Cause::message))));
        }
        return save(gamePath, name, files);
    }

    public Result<Boolean> delete(Config config) {
        if (!configs.containsKey(config.path())) return Result.err(Causes.IRRELEVANT);
        return doDelete(config).onSuccessDo(this::configsChanged);
    }

    protected Result<Boolean> doLoad(Path gamePath, Config config) {
        try {
            Path path = config.path();
            for (Path source : config.files()) {
                Path relative = path.relativize(source);
                Path target = gamePath.resolve(relative);
                FileUtils.copy(source, target);
            }
            return Result.ok(true);
        } catch (IOException ex) {
            return Result.err(Causes.cause("Failed to load config %s because: %s".formatted(config.name(), ex.getMessage())));
        }
    }

    protected Result<Boolean> doSave(Path gamePath, Config config) {
        try {
            Path path = config.path();
            for (Path source : config.files()) {
                Path relative = gamePath.relativize(source);
                Path target = path.resolve(relative);
                FileUtils.copy(source, target);
            }
            return Result.ok(true);
        } catch (IOException ex) {
            return Result.err(Causes.cause("Failed to save config %s because: %s".formatted(config.name(), ex.getMessage())));
        }
    }

    protected Result<Boolean> doDelete(Config config) {
        try {
            FileUtils.delete(config.path());
            return Result.ok(true);
        } catch (IOException ex) {
            return Result.err(Causes.cause(
                "Failed to delete config %s because: %s%nConfig won't be available anymore, but leftovers may be present on the disk"
                    .formatted(config.name(), ex.getMessage())
            ));
        } finally {
            configs.remove(config.path());
        }
    }

    protected void detectConfigs() {
        if (configsPath == null) return;
        configs.clear();
        try (Stream<Path> stream = Files.list(configsPath)) {
            List<Path> dirs = stream.filter(Files::isDirectory).toList();
            for (Path dir : dirs) {
                Config config = readConfigDir(dir);
                if (config != null) configs.put(dir, config);
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to detect configs because: ", ex);
        } finally {
            configsChanged();
        }
    }

    protected Config readConfigDir(Path dir) {
        try (Stream<Path> stream = Files.list(dir)) {
            Config config = Config.from(dir);
            stream.forEach(config::addFiles);
            return config;
        } catch (IOException ex) {
            LOGGER.error("Failed to read config %s because: ".formatted(dir.getFileName()), ex);
            return null;
        }
    }

    protected void configsChanged() {
        repo.onConfigsChanged(configs.values());
    }

    //================================================================================
    // Getters
    //================================================================================
    public Game getGame() {
        return game;
    }

    public Path getConfigsPath() {
        return configsPath;
    }

    public boolean isInitialized() {
        return configsPath != null;
    }
}
