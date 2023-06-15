package io.github.palexdev.enbmanager.frontend.theming;

import io.github.palexdev.enbmanager.backend.Dirs;
import io.github.palexdev.enbmanager.backend.settings.base.StringSetting;
import io.github.palexdev.enbmanager.frontend.settings.AppSettings;
import io.github.palexdev.mfxcomponents.theming.JavaFXThemes;
import io.github.palexdev.mfxcomponents.theming.MaterialThemes;
import io.github.palexdev.mfxcomponents.theming.UserAgentBuilder;
import io.github.palexdev.mfxcomponents.theming.base.Theme;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import io.inverno.core.annotation.Bean;
import io.inverno.core.annotation.Wrapper;
import io.methvin.watcher.DirectoryWatcher;
import io.methvin.watcher.hashing.FileHash;
import io.methvin.watcher.hashing.FileHasher;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static io.github.palexdev.enbmanager.frontend.ENBManager.LOGGER;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class ThemeManager {
    public enum Mode {
        LIGHT, DARK
    }

    //================================================================================
    // Properties
    //================================================================================
    private final ObjectProperty<Theme> theme = new SimpleObjectProperty<>() {
        @Override
        public void set(Theme newValue) {
            Theme val = getTheme(newValue.name(), getMode());
            if (val == null) val = newValue;
            super.set(val);
        }

        @Override
        protected void invalidated() {
            onThemeChanged();
        }
    };
    private final ObjectProperty<Mode> mode = new SimpleObjectProperty<>() {
        @Override
        public void set(Mode newValue) {
            Mode oldValue = get();
            super.set(newValue);
            if (oldValue != null && !Objects.equals(oldValue, newValue)) onModeChanged();
        }

        @Override
        protected void invalidated() {
            settings.lastThemeMode.set(get() == Mode.DARK);
        }
    };
    private final Theme appTheme = Stylesheets.APP_THEME;

    private CSSFragment uas;
    private final Dirs dirs;
    private final AppSettings settings;

    private final ScheduledExecutorService executor;
    private DirectoryWatcher watcher;
    private CompletableFuture<Void> watcherTask;
    private FileHash lastHash;

    //================================================================================
    // Constructors
    //================================================================================
    public ThemeManager(Dirs dirs, AppSettings settings) {
        this.dirs = dirs;
        this.settings = settings;
        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        JavaFXThemes.MODENA.deploy();
        MaterialThemes.PURPLE_LIGHT.deploy();

    }

    //================================================================================
    // Methods
    //================================================================================
    public void init() {
        StringSetting setting = settings.lastTheme;
        Mode mode = (settings.lastThemeMode.get()) ? Mode.DARK : Mode.LIGHT;
        setMode(mode);
        Theme theme = getTheme(setting.get(), mode);
        if (theme == null) {
            setting.reset();
            theme = getTheme(setting.get(), mode);
        }
        setTheme(theme);
    }

    public void debugWatch() {
        Path projectPath = dirs.projectPath();
        Path source = projectPath.resolve(
            "src/main/resources/io/github/palexdev/enbmanager/frontend/css/AppTheme.css"
        );
        Path target = projectPath.resolve(
            "build/resources/main/io/github/palexdev/enbmanager/frontend/css/AppTheme.css"
        );
        if (!Files.exists(source) || !Files.exists(target)) {
            LOGGER.error("Cannot debug watch!");
            return;
        }
        try {
            FileHasher hasher = FileHasher.DEFAULT_FILE_HASHER;
            executor.scheduleAtFixedRate(() -> {
                try {
                    FileHash hash = hasher.hash(source);
                    if (Objects.equals(lastHash, hash)) return;
                    lastHash = hash;
                    Files.writeString(target, Files.readString(source), CREATE, TRUNCATE_EXISTING);
                    buildUserAgent();
                } catch (Exception ex) {
                    LOGGER.error("", ex);
                }
            }, 0, 1, TimeUnit.SECONDS);
        } catch (Exception ex) {
            LOGGER.error("", ex);
        }
    }

    public void watch(boolean rebuild) {
        if (watcher != null) stopWatch();
        try {
            Path path = getCachePath();
            watcher = DirectoryWatcher.builder()
                .path(path)
                .listener(e -> {
                    String themeName = getThemeName();
                    if (themeName.isBlank()) return;
                    if (e.isDirectory()) return;
                    Path themePath = e.path();
                    if (themePath.getFileName().toString().equals(themeName))
                        reloadThemeFromDisk(themePath, rebuild);
                }).build();
            watcherTask = watcher.watchAsync(executor);
        } catch (IOException ignored) {}
    }

    public void stopWatch() {
        try {
            if (watcher != null) {
                watcher.close();
                watcherTask.cancel(true);
                watcher = null;
                watcherTask = null;
            }
        } catch (IOException ignored) {}
    }

    public void invalidateCache() {
        Path path = getCachePath();
        try {
            File[] files = path.toFile().listFiles((dir, name) -> name.endsWith(".theme"));
            if (files == null) return;
            for (File file : files) file.delete();
        } finally {
            buildUserAgent();
        }
    }

    protected void onThemeChanged() {
        // Check if cached of file-system
        try {
            Path cache = getCachePath().resolve(getThemeName());
            if (Files.exists(cache) && cache.toFile().isFile()) {
                reloadThemeFromDisk(cache, false);
            } else {
                buildUserAgent();
            }
        } catch (Exception ex) {
            buildUserAgent();
        } finally {
            settings.lastTheme.set(getTheme().name());

        }
    }

    protected void onModeChanged() {
        Theme theme = getTheme();
        Mode mode = getMode();
        String name = theme.name();
        if (mode == Mode.LIGHT) name = name.replace("DARK", "LIGHT");
        if (mode == Mode.DARK) name = name.replace("LIGHT", "DARK");
        setTheme(MaterialThemes.valueOf(name));
    }

    protected void reloadThemeFromDisk(Path path, boolean rebuild) {
        if (rebuild) {
            buildUserAgent();
            return;
        }
        try {
            String content = Files.readString(path);
            uas = new CSSFragment(content);
            Application.setUserAgentStylesheet(uas.toDataUri());
        } catch (Exception ignored) {}
    }

    protected void buildUserAgent() {
        // Build it
        Theme theme = getTheme();
        uas = UserAgentBuilder.builder()
            .themes(JavaFXThemes.MODENA, theme, appTheme)
            .setResolveAssets(true)
            .build();
        // Cache it
        try {
            String themeName = getThemeName();
            Path themePath = getCachePath().resolve(themeName);
            Files.writeString(themePath, uas.toString(), CREATE, TRUNCATE_EXISTING);
        } catch (Exception ignored) {}
        // Set it
        Application.setUserAgentStylesheet(uas.toDataUri());
    }

    private Theme getTheme(String name, Mode mode) {
        if (mode == Mode.LIGHT) name = name.replace("DARK", "LIGHT");
        if (mode == Mode.DARK) name = name.replace("LIGHT", "DARK");
        try {
            return MaterialThemes.valueOf(name);
        } catch (Exception ex) {
            return null;
        }
    }

    private String getThemeName() {
        Theme theme = getTheme();
        if (theme == null) return "";
        return theme.name() + ".theme";
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public Theme getTheme() {
        return theme.get();
    }

    public ObjectProperty<Theme> themeProperty() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme.set(theme);
    }

    public Mode getMode() {
        return mode.get();
    }

    public ObjectProperty<Mode> modeProperty() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode.set(mode);
    }

    public Path getCachePath() {
        return dirs.cachePath().get();
    }

    //================================================================================
    // Socket
    //================================================================================
    @Bean(name = "themeManager")
    @Wrapper
    public static class ThemeManagerWrap implements Supplier<ThemeManager> {
        private final ThemeManager manager;

        public ThemeManagerWrap(Dirs dirs, AppSettings settings) {
            manager = new ThemeManager(dirs, settings);
        }

        @Override
        public ThemeManager get() {
            return manager;
        }
    }
}
