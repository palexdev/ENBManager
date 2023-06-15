package io.github.palexdev.enbmanager.frontend;

import io.github.palexdev.enbmanager.backend.Dirs;
import io.github.palexdev.enbmanager.backend.events.SettingsEvent;
import io.github.palexdev.enbmanager.backend.events.bus.SimpleEventBus;
import io.github.palexdev.enbmanager.backend.fp.Causes;
import io.github.palexdev.enbmanager.backend.fp.Result;
import io.github.palexdev.enbmanager.backend.fp.Tuple;
import io.github.palexdev.enbmanager.backend.games.Game;
import io.github.palexdev.enbmanager.backend.games.GamesManager;
import io.github.palexdev.enbmanager.backend.settings.base.Setting;
import io.github.palexdev.enbmanager.backend.settings.base.Settings;
import io.github.palexdev.enbmanager.backend.utils.FileUtils;
import io.github.palexdev.enbmanager.backend.utils.OSUtils;
import io.github.palexdev.enbmanager.frontend.components.dialogs.DialogBase;
import io.github.palexdev.enbmanager.frontend.components.dialogs.GamesDialog;
import io.github.palexdev.enbmanager.frontend.components.dialogs.IDialogs;
import io.github.palexdev.enbmanager.frontend.events.AppEvent;
import io.github.palexdev.enbmanager.frontend.events.ViewEvent;
import io.github.palexdev.enbmanager.frontend.model.ENBManagerModel;
import io.github.palexdev.enbmanager.frontend.settings.AppSettings;
import io.github.palexdev.enbmanager.frontend.theming.ThemeManager;
import io.github.palexdev.enbmanager.frontend.utils.GameUtils;
import io.github.palexdev.enbmanager.frontend.views.MainView;
import io.github.palexdev.enbmanager.frontend.views.base.View;
import io.inverno.core.annotation.Bean;
import io.inverno.core.annotation.Wrapper;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
public class ENBManager extends Application {
    //================================================================================
    // Properties
    //================================================================================
    public static final String APP_NAME = "ENBManager";
    private static final StringProperty title = new SimpleStringProperty(APP_NAME);

    // Module
    private Frontend frontend;

    // Extra beans
    private static ENBManager app;
    private static Stage stage;
    private static Parameters parameters;
    private static HostServices hostServices;

    // Dependencies
    private static Dirs dirs;
    private static ENBManagerModel model;
    private static AppSettings settings;
    private static GamesManager manager;
    private static ThemeManager themeManager;
    private static IDialogs.Dialogs dialogs;
    private static Map<Class<? extends View>, View<?>> views;
    private static Map<Class<? extends Settings>, Set<Setting<?>>> settingsDB;
    private static SimpleEventBus events;

    // Logging
    public static final Logger LOGGER = LoggerFactory.getLogger(io.inverno.core.v1.Application.class);

    //================================================================================
    // Startup/Shutdown
    //================================================================================
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Init extra beans
        ENBManager.app = this;
        ENBManager.stage = stage;
        ENBManager.parameters = getParameters();
        ENBManager.hostServices = getHostServices();

        // Bootstrap
        bootstrap().accept(
            f -> {
                if (Causes.IRRELEVANT.message().equals(f.message())) return;
                fatal(f.message());
            },
            s -> {
                frontend = s;
                events.publish(new AppEvent.AppReadyEvent());
                LOGGER.info("Boostrap completed successfully!");
            }
        );
    }

    @Override
    public void stop() {
        double w = (!Double.isNaN(stage.getWidth()) ? stage.getWidth() : settings.windowWidth.defValue());
        double h = (!Double.isNaN(stage.getHeight()) ? stage.getHeight() : settings.windowHeight.defValue());
        settings.windowWidth.set(w);
        settings.windowHeight.set(h);
        if (model.getGame() != null) settings.lastGame.set(model.getGame().getName());
        if (frontend != null && frontend.isActive()) frontend.stop();
    }

    public static void forceShutdown(int status) {
        exit();
        System.exit(status);
    }

    public static void exit() {
        events.publish(new AppEvent.AppCloseEvent());
        Platform.exit();
    }

    Result<Frontend> bootstrap() {
        // First of all start Inverno modules
        LOGGER.atInfo().log(() -> {
            ByteArrayOutputStream bannerStream = new ByteArrayOutputStream();
            new Banner().print(new PrintStream(bannerStream));
            return bannerStream.toString();
        });
        io.inverno.core.v1.Application.with(new Frontend.Builder()).run();

        // First of all check if platform is supported
        if (!OSUtils.supportedPlatform()) {
            themeManager.init(); // Init, otherwise un-styled failure dialogs
            return Result.err(Causes.cause("Unsupported OS detected %s.%nApp will shutdown!".formatted(OSUtils.os())));
        }

        // Then check correct initialization of app directories
        Result<Tuple.Tuple2<Path, Path>> dirRes = Result.all(dirs.configPath(), dirs.cachePath()).id();
        if (dirRes.isFailure()) {
            themeManager.init(); // Init, otherwise un-styled failure dialogs
            return Result.convert(dirRes, Result::err);
        }

        // Check if a cleanup has been requested, in such cases, clean and exit
        if (settings.isCleanup()) {
            cleanup().onError(c -> LOGGER.error(c.message()));
            forceShutdown(0);
        }

        // If bootstrap didn't fail, themeManager won't have been initialized yet
        themeManager.init();

        // Check if settings reset has been requested via arguments
        // Also add listener for ResetSettingEvents
        if (settings.isResetSettings()) resetSettings();
        events.subscribe(SettingsEvent.ResetSettingsEvent.class, this::resetSettings);

        // Check if themes cache must be invalidated
        if (settings.isInvalidateThemesCache()) themeManager.invalidateCache();

        // Check if debug is enabled via arguments
        if (settings.isDebug()) themeManager.debugWatch();
        else themeManager.watch(false);

        // Check if last session was saved. If it was, proceed with init, otherwise show Games choice dialog
        Optional<Game> opt = manager.detectLastGame(settings.lastGame);
        Game game;
        Path path;
        Boolean remember = null;
        if (opt.isEmpty()) {
            GamesDialog.Choice choice = dialogs.showGamesDialog(null, () -> new IDialogs.DialogConfig<GamesDialog>()
                .setShowMinimize(false)
                .setPreserveHeader(true)
                .setCenterInOwnerNode(false)
                .setOnConfigure(d -> d.setGames(manager.getGames()))
            );
            if (!choice.isValid()) {
                exit();
                return Result.err(Causes.IRRELEVANT);
            }
            game = choice.game();
            path = choice.path();
            remember = choice.remember();
        } else {
            game = opt.get();
            path = Path.of(opt.get().getSettings().path.get());
        }
        return GameUtils.setGame(model, settings, game, path, remember)
            .flatMap(r -> Result.ok(frontend));
    }

    Result<Boolean> cleanup() {
        try {
            resetSettings();
            Path repoPath = dirs.configPath().get();
            Path cachePath = dirs.cachePath().get();
            if (Files.exists(repoPath)) FileUtils.delete(repoPath);
            if (Files.exists(cachePath)) FileUtils.delete(cachePath);
            return Result.ok(true);
        } catch (Exception ex) {
            return Result.err(Causes.cause("Cleanup failed because: %s".formatted(ex.getMessage())));
        }
    }

    //================================================================================
    // Misc
    //================================================================================
    public static void setView(Class<? extends View<?>> view) {
        events.publish(new ViewEvent.ViewSwitchEvent(views.get(view)));
    }

    public static void showToast(String message) {
        events.publish(new ViewEvent.ShowToastEvent(message));
    }

    void resetSettings() {
        settingsDB.values().forEach(c -> c.forEach(Setting::reset));
    }

    void resetSettings(SettingsEvent.ResetSettingsEvent k) {
        settingsDB.get(k.data()).forEach(Setting::reset);
    }

    void fatal(String error) {
        dialogs.showDialog(MainView.class, DialogBase.fatal("Ok"), () -> new IDialogs.DialogConfig<>()
            .setShowAlwaysOnTop(false)
            .setShowMinimize(false)
            .setCenterInOwnerNode(false)
            .setModality(Modality.APPLICATION_MODAL)
            .setHeaderText("Fatal error")
            .setContentText(error));
    }


    //================================================================================
    // Getters/Setters
    //================================================================================
    public static String getTitle() {
        return ENBManager.title.get();
    }

    /**
     * Specifies the app's title.
     */
    public static StringProperty titleProperty() {
        return ENBManager.title;
    }

    public static void setTitle(String title) {
        ENBManager.title.set(title);
    }

    //================================================================================
    // Sockets
    //================================================================================
    @Bean
    @Wrapper
    public static class App implements Supplier<ENBManager> {
        private final ENBManager app;

        public App(
            Dirs dirs, ENBManagerModel model,
            AppSettings settings, GamesManager manager,
            ThemeManager themeManager, IDialogs.Dialogs dialogs,
            Map<Class<? extends View>, View<?>> views,
            Map<Class<? extends Settings>, Set<Setting<?>>> settingsDB,
            SimpleEventBus events
        ) {
            app = ENBManager.app;
            ENBManager.dirs = dirs;
            ENBManager.model = model;
            ENBManager.settings = settings;
            ENBManager.manager = manager;
            ENBManager.themeManager = themeManager;
            ENBManager.dialogs = dialogs;
            ENBManager.views = views;
            ENBManager.settingsDB = settingsDB;
            ENBManager.events = events;
        }

        @Override
        public ENBManager get() {
            return app;
        }
    }

    @Bean
    @Wrapper
    public static class StageWrap implements Supplier<Stage> {
        private final Stage stage;

        public StageWrap() {
            this.stage = ENBManager.stage;
        }

        @Override
        public Stage get() {
            return stage;
        }
    }

    @Bean
    @Wrapper
    public static class ParametersWrap implements Supplier<Parameters> {
        private final Parameters parameters;

        public ParametersWrap() {
            parameters = ENBManager.parameters;
        }

        @Override
        public Parameters get() {
            return parameters;
        }
    }

    @Bean
    @Wrapper
    public static class HostServicesWrap implements Supplier<HostServices> {
        private final HostServices hostServices;

        public HostServicesWrap() {
            hostServices = ENBManager.hostServices;
        }

        @Override
        public HostServices get() {
            return hostServices;
        }
    }

    //================================================================================
    // Banner
    //================================================================================
    @SuppressWarnings({"ConcatenationWithEmptyString", "TextBlockMigration"})
    public static class Banner implements io.inverno.core.v1.Banner {
        //================================================================================
        // Properties
        //================================================================================
        private final String appVer = "17.0.1";
        private final String invernoVer = "1.5.1";

        private final String header = "\n" +
            "╭──────────────────────────────────────────────────────────────────────────────────╮\n" +
            "│ ⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿                                                       │\n" +
            "│ ⣿⣿⣿⣿⣿⣿⣿⠻⢿⠀⠀⡿⠟⣿⣿⣿⣿⣿⣿⣿ ENBManager                                            │\n" +
            "│ ⣿⣿⡿⠿⣿⡏⢹⣦⣄⠀⠀⣠⣴⡏⢹⣿⠿⢿⣿⣿    Version: %s│\n".formatted(version(appVer)) +
            "│ ⣿⣿⣷⣄⡀⠁⠘⢿⠙⠀⠀⠋⡿⠃⠈⢀⣠⣾⣿⣿    At: https://github.com/palexdev/ENBManager         │\n" +
            "│ ⣿⣿⣿⣁⣠⣴⡦⠀⢀⣶⣶⡀⠀⢴⣦⣄⣈⣿⣿⣿                                                       │\n" +
            "│ ⣿⣿⣿⡉⠙⠻⠗⠀⠈⠿⠿⠁⠀⠺⠟⠋⢉⣿⣿⣿ Powered by: Inverno Framework                         │\n" +
            "│ ⣿⣿⡿⠋⠁⡀⢠⣾⣠⠀⠀⣄⣷⡄⢀⠈⠙⢿⣿⣿    Version: %s│\n".formatted(version(invernoVer)) +
            "│ ⣿⣿⣷⣶⣿⣇⣸⠟⠋⠀⠀⠙⠻⣇⣸⣿⣶⣾⣿⣿    At: https://inverno.io                             │\n" +
            "│ ⣿⣿⣿⣿⣿⣿⣿⣴⣾⠀⠀⣷⣦⣿⣿⣿⣿⣿⣿⣿    Modules: [ENBManager.backend, ENBManager.frontend] │\n" +
            "│ ⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿                                                       │\n" +
            "╰──────────────────────────────────────────────────────────────────────────────────╯";

        private final String games = "" +
            "╭──────────────────╮\n" +
            "│ Supported Games: │\n" +
            "│   - Skyrim       │\n" +
            "│   - Skyrim SE    │\n" +
            "╰──────────────────╯";

        private String banner;

        //================================================================================
        // Overridden Methods
        //================================================================================
        @Override
        public void print(PrintStream out) {
            out.println(banner());
        }

        @Override
        public String toString() {
            return banner();
        }

        //================================================================================
        // Methods
        //================================================================================
        public String banner() {
            if (banner == null) banner = buildBanner();
            return banner;
        }

        private String buildBanner() {
            return header + "\n" + games;
        }

        private String version(String input) {
            int availableSpace = "                                          ".length();
            int versionLength = input.length();
            int spaces = availableSpace - versionLength;
            return input + " ".repeat(spaces);
        }
    }
}
