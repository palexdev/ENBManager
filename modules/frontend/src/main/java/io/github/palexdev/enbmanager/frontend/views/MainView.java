package io.github.palexdev.enbmanager.frontend.views;

import io.github.palexdev.enbmanager.backend.events.bus.SimpleEventBus;
import io.github.palexdev.enbmanager.backend.games.Game;
import io.github.palexdev.enbmanager.backend.games.GamesManager;
import io.github.palexdev.enbmanager.backend.settings.base.NumberSetting;
import io.github.palexdev.enbmanager.frontend.ENBManager;
import io.github.palexdev.enbmanager.frontend.components.dialogs.DialogBase;
import io.github.palexdev.enbmanager.frontend.components.dialogs.GamesDialog;
import io.github.palexdev.enbmanager.frontend.components.dialogs.IDialogs;
import io.github.palexdev.enbmanager.frontend.components.dialogs.IDialogs.DialogConfig;
import io.github.palexdev.enbmanager.frontend.events.ViewEvent;
import io.github.palexdev.enbmanager.frontend.model.ENBManagerModel;
import io.github.palexdev.enbmanager.frontend.settings.AppSettings;
import io.github.palexdev.enbmanager.frontend.theming.ThemeManager;
import io.github.palexdev.enbmanager.frontend.utils.GameUtils;
import io.github.palexdev.enbmanager.frontend.utils.StageUtils;
import io.github.palexdev.enbmanager.frontend.utils.UIUtils;
import io.github.palexdev.enbmanager.frontend.views.base.View;
import io.github.palexdev.mfxcomponents.behaviors.MFXIconButtonBehavior;
import io.github.palexdev.mfxcomponents.controls.MaterialSurface;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxcomponents.window.popups.MFXTooltip;
import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxcore.builders.bindings.ObjectBindingBuilder;
import io.github.palexdev.mfxcore.controls.Label;
import io.github.palexdev.mfxcore.enums.SelectionMode;
import io.github.palexdev.mfxcore.selection.SelectionGroup;
import io.github.palexdev.mfxcore.utils.EnumUtils;
import io.github.palexdev.mfxcore.utils.fx.RegionUtils;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.SequentialBuilder;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import io.inverno.core.annotation.Bean;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.scenicview.ScenicView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Bean
public class MainView extends View<MainView.MainPane> {
    //================================================================================
    // Properties
    //================================================================================
    private final Stage stage;
    private final IDialogs.Dialogs dialogs;
    private final ENBManagerModel model;
    private final GamesManager manager;
    private final ThemeManager themes;
    private final AppSettings settings;

    private View<?> currentView;
    private Animation animation;

    //================================================================================
    // Constructors
    //================================================================================
    public MainView(Stage stage, SimpleEventBus events,
                    IDialogs.Dialogs dialogs, ENBManagerModel model,
                    GamesManager manager, ThemeManager themes,
                    AppSettings settings
    ) {
        super(events);
        this.stage = stage;
        this.dialogs = dialogs;
        this.model = model;
        this.manager = manager;
        this.themes = themes;
        this.settings = settings;
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void switchView(View<?> view) {
        // Do not switch if still animating switch!
        // May cause exceptions in case of fast clicking
        if (Animations.isPlaying(animation)) return;

        Region region = view.toRegion();
        if (currentView == null) {
            currentView = view;
            root.addContent(region);
            return;
        }

        // Do not animate if the current view is already the requested one
        if (currentView == view) return;

        Duration d = M3Motion.MEDIUM4;
        Interpolator curve = M3Motion.STANDARD;
        region.setOpacity(0.0);
        Node old = currentView.toRegion();
        root.addContent(region);
        animation = TimelineBuilder.build()
            .add(KeyFrames.of(d, old.opacityProperty(), 0.0, curve))
            .add(KeyFrames.of(d, region.opacityProperty(), 1.0, curve))
            .setOnFinished(e -> root.removeContent(old))
            .getAnimation();
        animation.play();
        currentView = view;
        Optional.ofNullable(root.viewsButtons.get(view.getClass())).ifPresent(b -> b.setSelected(true));
    }

    protected void switchGame() {
        GamesDialog.Choice choice = dialogs.showGamesDialog(MainView.class,
            () -> new DialogConfig<GamesDialog>()
                .setOwner(stage)
                .setOwnerNode(root)
                .setShowMinimize(false)
                .setShowAlwaysOnTop(false)
                .setPreserveHeader(true)
                .setScrimOwner(true)
                .setModality(Modality.WINDOW_MODAL)
                .setOnConfigure(d -> d.setGames(manager.getGames()))
        );
        if (!choice.isValid()) return;
        GameUtils.setGame(model, settings, choice.game(), choice.path(), choice.remember())
            .onError(c -> dialogs.showDialog(MainView.class, DialogBase.error(), () -> new DialogConfig<>()
                .implicitOwner()
                .setShowAlwaysOnTop(false)
                .setShowMinimize(false)
                .setHeaderText("Error")
                .setContentText(c.message())
                .setOnConfigure(d -> d.addActions(
                    Map.entry(
                        new MFXButton("Ok").filled(),
                        e -> d.getScene().getWindow().hide()
                    )
                ))
            ));
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected MainPane build() {
        return new MainPane();
    }

    //================================================================================
    // Events
    //================================================================================
    @Override
    public void onAppReadyEvent() {
        super.onAppReadyEvent();

        // Init main window
        Size ws;
        NumberSetting<Double> windowWidth = settings.windowWidth;
        NumberSetting<Double> windowHeight = settings.windowHeight;
        try {
            double w = windowWidth.get();
            double h = windowHeight.get();
            ws = Size.of(w, h);
            StageUtils.clampWindowSizes(ws);
        } catch (Exception ex) {
            ws = Size.of(windowWidth.defValue(), windowHeight.defValue());
        }

        Scene scene = new Scene(root, ws.getWidth(), ws.getHeight());
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.titleProperty().bind(ENBManager.titleProperty());

        // Add event listeners
        events.subscribe(ViewEvent.ViewSwitchEvent.class, e -> switchView(e.data()));
        events.subscribe(ViewEvent.ShowToastEvent.class, e -> root.showToast(e.data()));
        ENBManager.setView(HomeView.class);

        // Finally show app
        if (settings.isDebug()) ScenicView.show(scene);
        stage.show();
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    public class MainPane extends StackPane {
        private final StackPane content;
        private final Label toastLabel;
        private Animation animation;

        private final Map<Class<? extends View<?>>, MFXIconButton> viewsButtons = new HashMap<>();
        private final SelectionGroup sg = new SelectionGroup(SelectionMode.SINGLE, true);
        private static final PseudoClass ENABLED = PseudoClass.getPseudoClass("enabled");

        MainPane() {
            Node header = buildHeader();
            Node sidebar = buildSidebar();
            content = new StackPane();

            toastLabel = new Label();
            toastLabel.setOpacity(0.0);
            toastLabel.setManaged(false);
            toastLabel.getStyleClass().add("toast");

            BorderPane container = new BorderPane();
            container.setTop(header);
            container.setCenter(content);
            container.setLeft(sidebar);

            getStyleClass().add("main-view");
            getChildren().addAll(container, toastLabel);
            StageUtils.makeResizable(stage, this);
        }

        Node buildHeader() {
            // Build game's icon view
            ImageView iv = new ImageView();
            iv.setFitWidth(48.0);
            iv.setFitHeight(48.0);
            iv.imageProperty().bind(ObjectBindingBuilder.<Image>build()
                .setMapper(() -> {
                    Game game = model.getGame();
                    if (game == null) return null;
                    return new Image(game.getIcon());
                })
                .addSources(model.gameProperty())
                .get()
            );

            StackPane wrapper = new StackPane();
            MaterialSurface surface = new MaterialSurface(wrapper);
            surface.setManaged(true);
            surface.setMaxSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
            wrapper.getChildren().addAll(iv, surface);
            wrapper.getStyleClass().add("img-wrapper");
            wrapper.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> switchGame());
            RegionUtils.makeRegionCircular(wrapper);
            MFXTooltip tooltip = UIUtils.installTooltip(wrapper, "");
            tooltip.textProperty().ifPresent(t -> t.bind(model.gameProperty().map(Game::getName)));

            // Build title and window buttons
            Label title = new Label();
            title.textProperty().bind(stage.titleProperty());
            title.getStyleClass().add("window-title");

            MFXIconWrapper aot = createHeaderIcon("aot", "Always on top");
            MFXIconWrapper minimize = createHeaderIcon("minimize", "Minimize");
            MFXIconWrapper maximize = createHeaderIcon("maximize", "Maximize");
            MFXIconWrapper close = createHeaderIcon("close", "Close");

            aot.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    stage.setAlwaysOnTop(!stage.isAlwaysOnTop());
                    aot.pseudoClassStateChanged(ENABLED, stage.isAlwaysOnTop());
                }
            });
            minimize.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getButton() == MouseButton.PRIMARY) stage.setIconified(true);
            });
            maximize.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getButton() == MouseButton.PRIMARY) stage.setMaximized(!stage.isMaximized());
            });
            close.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getButton() == MouseButton.PRIMARY) ENBManager.exit();
            });

            // Build a spacer to separate the icon and title from the buttons
            Region spacer = new Region();
            spacer.setMouseTransparent(true);
            spacer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Build container
            HBox box = new HBox(wrapper, title, spacer, aot, minimize, maximize, close);
            box.getStyleClass().add("window-header");
            StageUtils.makeDraggable(stage, box);
            return box;
        }

        Node buildSidebar() {
            MFXIconButton home = createSidebarButton("home", "Home");
            home.setOnAction(e -> ENBManager.setView(HomeView.class));
            viewsButtons.put(HomeView.class, home);

            MFXIconButton repo = createSidebarButton("repo", "Stored Configs");
            repo.setOnAction(e -> ENBManager.setView(RepoView.class));
            viewsButtons.put(RepoView.class, repo);

            MFXIconButton settings = createSidebarButton("settings", "Settings");
            settings.setOnAction(e -> ENBManager.setView(SettingsView.class));
            viewsButtons.put(SettingsView.class, settings);

            MFXIconButton info = createSidebarButton("info", "About");
            info.setOnAction(e -> ENBManager.setView(AboutView.class));
            viewsButtons.put(AboutView.class, info);

            MFXIconButton modeSwitch = createSidebarButton("theme-switch", "Toggle Light/Dark mode");
            modeSwitch.setSelectable(false);
            modeSwitch.setOnAction(e -> {
                ThemeManager.Mode newMode = EnumUtils.next(ThemeManager.Mode.class, themes.getMode());
                themes.setMode(newMode);
                modeSwitch.pseudoClassStateChanged(PseudoClass.getPseudoClass("dark"), newMode == ThemeManager.Mode.DARK);
            });

            Region separator = new Region();
            separator.setMaxHeight(Double.MAX_VALUE);
            VBox.setVgrow(separator, Priority.ALWAYS);

            VBox box = new VBox(home, repo, settings, info, separator, modeSwitch);
            box.getStyleClass().add("sidebar");
            return box;
        }

        MFXIconWrapper createHeaderIcon(String type, String tooltip) {
            MFXIconWrapper icon = new MFXIconWrapper();
            UIUtils.installTooltip(icon, tooltip);
            icon.getStyleClass().add(type);
            return icon;
        }

        MFXIconButton createSidebarButton(String type, String tooltip) {
            MFXIconButton btn = new MFXIconButton().filled().asToggle();
            btn.setBehaviorProvider(() -> new MFXIconButtonBehavior(btn) {
                @Override
                protected void handleSelection() {
                    // Selection is handled by switchView()
                    btn.fire();
                }
            });
            btn.setSelectionGroup(sg);
            MFXTooltip tp = UIUtils.installTooltip(btn, tooltip);
            tp.setAnchor(Pos.CENTER_RIGHT);
            tp.setEventDispatcher(getEventDispatcher());
            btn.getStyleClass().addAll("sidebar-button", type);
            return btn;
        }

        void addContent(Node content) {
            this.content.getChildren().add(content);
        }

        void removeContent(Node content) {
            this.content.getChildren().remove(content);
        }

        void showToast(String message) {
            toastLabel.setText(message);
            requestLayout();

            Duration d = M3Motion.MEDIUM4;
            Interpolator curve = M3Motion.EMPHASIZED;
            if (Animations.isPlaying(animation)) animation.stop();
            animation = SequentialBuilder.build()
                .add(TimelineBuilder.build()
                    .add(KeyFrames.of(d, toastLabel.opacityProperty(), 1.0, curve))
                    .add(KeyFrames.of(d, toastLabel.translateYProperty(), toastLabel.getHeight() + 40, curve))
                    .getAnimation()
                )
                .add(TimelineBuilder.build()
                    .setDelay(1500)
                    .add(KeyFrames.of(d, toastLabel.opacityProperty(), 0.0, curve))
                    .add(KeyFrames.of(d, toastLabel.translateYProperty(), 0.0, curve))
                    .getAnimation()
                ).getAnimation();
            animation.play();
        }

        @Override
        protected void layoutChildren() {
            super.layoutChildren();
            toastLabel.autosize();
            toastLabel.relocate((getWidth() - toastLabel.getWidth()) / 2, -toastLabel.getHeight() - 20);
        }
    }
}
