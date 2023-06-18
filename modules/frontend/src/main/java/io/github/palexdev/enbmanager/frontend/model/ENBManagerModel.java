package io.github.palexdev.enbmanager.frontend.model;

import io.github.palexdev.enbmanager.backend.events.ModelEvent;
import io.github.palexdev.enbmanager.backend.events.bus.SimpleEventBus;
import io.github.palexdev.enbmanager.backend.fp.Causes;
import io.github.palexdev.enbmanager.backend.fp.Result;
import io.github.palexdev.enbmanager.backend.games.Game;
import io.github.palexdev.enbmanager.backend.repo.Config;
import io.github.palexdev.enbmanager.backend.repo.GamesRepo;
import io.github.palexdev.enbmanager.backend.utils.FileUtils;
import io.github.palexdev.enbmanager.backend.utils.PathsComparator;
import io.github.palexdev.enbmanager.frontend.events.AppEvent;
import io.github.palexdev.mfxcore.collections.TransformableListWrapper;
import io.inverno.core.annotation.Bean;
import io.inverno.core.annotation.Wrapper;
import io.methvin.watcher.DirectoryWatcher;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static io.github.palexdev.enbmanager.frontend.ENBManager.LOGGER;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class ENBManagerModel {
    //================================================================================
    // Properties
    //================================================================================
    private final ObjectProperty<Game> game = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            repo.getConfigsRepo(get());
        }
    };
    private final ObjectProperty<Path> path = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            onPathChanged();
        }
    };
    private final ObservableList<Config> configs = FXCollections.observableArrayList();
    private final TransformableListWrapper<Path> files = new TransformableListWrapper<>(FXCollections.observableArrayList());
    private final Set<String> fileNames = new HashSet<>();

    private final Executor executor;
    private DirectoryWatcher watcher;
    private CompletableFuture<Void> watcherTask;

    private final GamesRepo repo;

    //================================================================================
    // Constructors
    //================================================================================
    public ENBManagerModel(GamesRepo repo, SimpleEventBus events) {
        this.repo = repo;
        executor = Executors.newFixedThreadPool(2, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
        files.setComparator(PathsComparator.instance());

        // Register events
        events.subscribe(AppEvent.AppReadyEvent.class, e -> {
            // Delay this when the app is ready.
            // getFileNames() need the repoPath to get the names from a text file
            // The repoPath is initialized by the Dirs bean. Calling this during the creation of this bean causes
            // the app to bypass the bootstrap checks for OS support and dirs init
            fileNames.addAll(getFileNames());
            updateFiles();
        });
        events.subscribe(AppEvent.AppCloseEvent.class, e -> {
            try {
                if (watcherTask != null) watcherTask.cancel(true);
                if (watcher != null) watcher.close();
            } catch (Exception ignored) {}
        });
        events.subscribe(ModelEvent.ConfigsChangedEvent.class, e -> {
            configs.clear();
            configs.addAll(e.data());
            // TODO setAll fails, VirtualizedFX bug. FIX ME. Possible solution, treat setAll as clear and then set, but preserve cells
        });
    }

    //================================================================================
    // Methods
    //================================================================================
    /* Actions */
    public Result<Boolean> load(Config config) {
        return repo.loadConfig(getGame(), getPath(), config);
    }

    public Result<Boolean> save(String name, Collection<? extends Path> files) {
        if (name.isBlank()) return Result.err(Causes.EMPTY);
        return repo.saveConfig(getGame(), getPath(), name, files);
    }

    public Result<Boolean> delete(Config config) {
        return repo.deleteConfig(getGame(), config);
    }

    public Result<Boolean> delete(Collection<? extends Path> files) {
        try {
            for (Path file : files) {
                FileUtils.delete(file);
            }
            return Result.ok(true);
        } catch (IOException ex) {
            return Result.err(Causes.cause("Failed to delete file because: %s".formatted(ex.getMessage())));
        }
    }

    public void refreshConfigs() {
        repo.refreshConfigs(getGame());
    }

    /* FileSystem */
    public void updateFiles() {
        Path path = getPath();
        if (path == null || !Files.isDirectory(path)) {
            files.clear();
            return;
        }
        Set<Path> files = fileNames.stream()
            .map(path::resolve)
            .filter(Files::exists)
            .collect(Collectors.toSet());
        Platform.runLater(() -> this.files.setAll(files));
    }

    protected void updateWatcher() {
        try {
            Path path = getPath();
            if (watcher != null) {
                watcher.close();
                watcherTask.cancel(true);
                watcher = null;
                watcherTask = null;
            }
            watcher = DirectoryWatcher.builder()
                .path(path)
                .listener(e -> updateFiles())
                .build();
            watcherTask = watcher.watchAsync(executor);
        } catch (IOException ex) {
            LOGGER.error("Failed to update directory watcher because: ", ex);
        }
    }

    protected void onPathChanged() {
        CompletableFuture.runAsync(() -> {
            updateFiles();
            updateWatcher();
        }, executor);
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public Game getGame() {
        return game.get();
    }

    /**
     * Specifies the current game manged by the app.
     */
    public ObjectProperty<Game> gameProperty() {
        return game;
    }

    public void setGame(Game game) {
        this.game.set(game);
    }

    public Path getPath() {
        return path.get();
    }

    /**
     * Specifies the current managed game's directory.
     */
    public ObjectProperty<Path> pathProperty() {
        return path;
    }

    public void setPath(Path path) {
        this.path.set(path);
    }

    /**
     * @return an unmodifiable list of the current game saved configurations
     */
    public ObservableList<Config> getConfigs() {
        return FXCollections.unmodifiableObservableList(configs);
    }

    /**
     * @return an unmodifiable list containing all the detected config files in the game's directory
     */
    public ObservableList<Path> getFiles() {
        return FXCollections.unmodifiableObservableList(files);
    }

    public Set<String> getFileNames() {
        if (fileNames.isEmpty()) {
            // Try loading from file
            Path repoPath = repo.getRepoPath();
            Path file = repoPath.resolve("files.txt");
            try {
                if (!Files.exists(file) || !file.toFile().isFile())
                    throw new FileNotFoundException();
                return Set.copyOf(Files.readAllLines(file));
            } catch (Exception ex) {
                Set<String> files = Set.of(
                    // Folders
                    "enbcache", "enbseries", "exes", "injFX_Shaders",
                    "_sample_enbraindrops", "ReShade", "reshade-shaders", "SweetFX",
                    "Data" + FileSystems.getDefault().getSeparator() + "Shaders",
                    // Files
                    "common.fhx", "d3d9.dll", "d3d9.fx", "d3d9injFX.dll",
                    "d3d9SFX.dll", "d3d9_aa.dll", "d3dcompiler_46e.dll", "d3d9_fx.dll",
                    "d3d9_fxaa.dll", "d3d9_SFX.dll", "d3d9_SFX_FXAA.dll", "d3d9_SFX_SMAA.dll",
                    "d3d9_Sharpen.dll", "d3d9_smaa.dll", "d3d9_SweetFX.dll", "d3d11.dll",
                    "d3dx9.dll", "dxgi.dll", "dxgi.fx", "dxgi.ini",
                    "eax.dll", "EED_verasansmono.bmp", "effect.txt", "enb.dll",
                    "enbadaptation.fx", "enbbloom.fx", "enbdepthoffield.fx", "enbdepthoffield.fx.ini",
                    "enbdepthoffield.ini", "enbeffect.fx", "enbeffectpostpass.fx", "enbeffectprepass.fx",
                    "enbhelper.dll", "enbhost.exe", "ENBInjector.exe", "enbinjector.ini",
                    "enblens.fx", "enblensmask.png", "enblensmask.bmp", "enblocal.ini",
                    "enbpalette.bmp", "enbpatch.ini", "enbraindrops.dds", "enbraindrops_small.dds",
                    "enbraindrops.png", "enbraindrops_small.png", "enbraindrops.tga", "enbraindrops_small.tga",
                    "enbseries.ini", "enbseries.dll", "enbspectrum.bmp", "enbsunsprite.bmp",
                    "enbsunsprite.fx", "enbsunsprite.tga", "enbunderwater.fx", "enbweather.bmp",
                    "enbunderwaternoise.bmp", "EnhancedENBDiagnostics.fxh", "FixForBrightObjects.txt", "FXAA.dll",
                    "FXAA_d3d9.dll", "FXAA_Tool.exe", "injector.ini", "injFX_Settings.h",
                    "injFXAA.dll", "INSTALL.txt", "lens.fx", "license.txt",
                    "license_en.txt", "license_ru.txt", "log.txt", "log.log",
                    "ParallaxMod.txt", "readme_en.txt", "ReShade.fx", "shader.fx",
                    "SkyrimCustomShader_Config.h", "SMAA.fx", "SMAA.h", "Sweet.fx",
                    "Sweetfx_d3d9.dll", "SweetFX_preset.txt", "SweetFX_settings.txt", "technique.fxh",
                    "uninstall.exe", "_weatherlist.ini", "aaa.ini", "bbb.ini",
                    "other_d3d9.dll"
                );
                try {
                    Files.deleteIfExists(file);
                } catch (Exception ignored) {
                }
                files.forEach(s -> {
                    try {
                        Files.writeString(file, s + "\n", CREATE, APPEND);
                    } catch (IOException iex) {
                        LOGGER.error("Failed to write config files to .txt because: ", iex);
                        try {Files.deleteIfExists(file);} catch (Exception ignored) {}
                    }
                });
                return files;
            }
        }
        return fileNames;
    }

    //================================================================================
    // Socket
    //================================================================================
    @Bean(name = "enbManagerModel")
    @Wrapper
    public static class ENBManagerModelWrap implements Supplier<ENBManagerModel> {
        private final ENBManagerModel model;

        public ENBManagerModelWrap(GamesRepo repo, SimpleEventBus events) {
            model = new ENBManagerModel(repo, events);
        }

        @Override
        public ENBManagerModel get() {
            return model;
        }
    }
}
