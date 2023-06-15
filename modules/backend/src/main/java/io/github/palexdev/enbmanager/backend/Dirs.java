package io.github.palexdev.enbmanager.backend;

import io.github.palexdev.enbmanager.backend.fp.Causes;
import io.github.palexdev.enbmanager.backend.fp.Result;
import io.github.palexdev.enbmanager.backend.utils.OSUtils;
import io.inverno.core.annotation.Bean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Bean
public class Dirs {
    //================================================================================
    // Properties
    //================================================================================
    private Path configsPath;
    private Path cachePath;

    //================================================================================
    // Methods
    //================================================================================
    public Result<Path> configPath() {
        if (configsPath == null) {
            OSUtils.OSType os = OSUtils.os();
            Path path = null;
            switch (os) {
                case Windows -> path = Path.of(System.getenv("APPDATA"));
                case Linux -> path = Path.of(System.getProperty("user.home"), ".config");
            }

            assert path != null; // This won't happen, a check in OSUtils ensures that we are on a supported platform
            if (!Files.isDirectory(path))
                return Result.err(Causes.cause("Path %s not found. App will shutdown!".formatted(path)));
            path = path.resolve("ENBManager");
            return tryCreate(path).onSuccess(p -> configsPath = p);
        }
        return Result.ok(configsPath);
    }

    public Result<Path> cachePath() {
        if (cachePath == null) {
            OSUtils.OSType os = OSUtils.os();
            Path path = null;
            switch (os) {
                case Windows -> path = Path.of(System.getenv("APPDATA"), "Cache");
                case Linux -> path = Path.of(System.getProperty("user.home"), ".cache");
            }

            assert path != null; // This won't happen, a check in OSUtils ensures that we are on a supported platform
            Path base = (os == OSUtils.OSType.Windows) ? path.getParent() : path;
            if (!Files.isDirectory(base))
                return Result.err(Causes.cause("Path %s not found. App will shutdown!".formatted(path)));
            path = path.resolve("ENBManager");
            return tryCreate(path).onSuccess(p -> cachePath = p);
        }
        return Result.ok(cachePath);
    }

    public Path projectPath() {
        return Path.of(System.getProperty("user.dir"));
    }

    protected Result<Path> tryCreate(Path path) {
        try {
            return Result.ok(Files.createDirectories(path));
        } catch (IOException ex) {
            return Result.err(Causes.cause(
                "Failed to create directory %s, because: %s%nApp will shutdown!".formatted(path, ex.getMessage())));
        }
    }
}
