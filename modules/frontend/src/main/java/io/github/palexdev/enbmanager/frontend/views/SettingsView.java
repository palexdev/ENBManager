package io.github.palexdev.enbmanager.frontend.views;

import io.github.palexdev.enbmanager.backend.events.bus.SimpleEventBus;
import io.github.palexdev.enbmanager.backend.games.Game;
import io.github.palexdev.enbmanager.frontend.components.FloatingField;
import io.github.palexdev.enbmanager.frontend.components.settings.GamePathSettingComponent;
import io.github.palexdev.enbmanager.frontend.model.ENBManagerModel;
import io.github.palexdev.enbmanager.frontend.theming.ThemeManager;
import io.github.palexdev.enbmanager.frontend.utils.UIUtils;
import io.github.palexdev.enbmanager.frontend.views.SettingsView.SettingsPane;
import io.github.palexdev.enbmanager.frontend.views.base.View;
import io.github.palexdev.mfxcomponents.controls.MaterialSurface;
import io.github.palexdev.mfxcomponents.theming.base.Theme;
import io.github.palexdev.mfxcore.observables.When;
import io.inverno.core.annotation.Bean;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;

import static io.github.palexdev.mfxcomponents.theming.MaterialThemes.INDIGO_LIGHT;
import static io.github.palexdev.mfxcomponents.theming.MaterialThemes.PURPLE_LIGHT;

@Bean
public class SettingsView extends View<SettingsPane> {
    //================================================================================
    // Properties
    //================================================================================
    private final ENBManagerModel model;
    private final ThemeManager themes;

    //================================================================================
    // Constructors
    //================================================================================
    public SettingsView(SimpleEventBus events, ENBManagerModel model, ThemeManager themes) {
        super(events);
        this.model = model;
        this.themes = themes;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected SettingsPane build() {
        return new SettingsPane();
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    public class SettingsPane extends StackPane {
        private final VBox container; // TODO wrap in a scroll pane
        private List<Node> gameSettingsNodes;

        SettingsPane() {
            container = new VBox();
            addThemeSettings();
            addSeparator(30);
            When.onInvalidated(model.gameProperty())
                .then(this::updateGameSettingsView)
                .executeNow(() -> model.getGame() != null)
                .listen();
            getStyleClass().add("settings-view");
            getChildren().add(container);
        }

        protected void addThemeSettings() {
            Label title = new Label("Appearance");
            title.getStyleClass().add("title");
            Label label = new Label("Available themes: ");
            FlowPane fp = new FlowPane();
            fp.getChildren().addAll(
                buildThemeRect("Indigo", INDIGO_LIGHT, "#4355b9"),
                buildThemeRect("Purple", PURPLE_LIGHT, "#6750A4")
            );
            HBox box = new HBox(label, fp);
            box.getStyleClass().add("box");
            container.getChildren().addAll(title, box);
        }

        protected void updateGameSettingsView(Game game) {
            if (gameSettingsNodes != null) container.getChildren().removeAll(gameSettingsNodes);
            Label title = new Label("%s settings".formatted(game.getName()));
            title.getStyleClass().add("title");
            GamePathSettingComponent component = new GamePathSettingComponent(game.getSettings().path, FloatingField::new);
            component.gameProperty().bind(model.gameProperty());
            component.gamePathProperty().bindBidirectional(model.pathProperty());
            gameSettingsNodes = List.of(title, component);
            container.getChildren().addAll(gameSettingsNodes);
        }

        protected Node buildThemeRect(String tooltip, Theme theme, String color) {
            StackPane p = new StackPane();
            MaterialSurface surface = new MaterialSurface(p);
            surface.setManaged(true);
            surface.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            p.getChildren().add(surface);
            p.setBackground(Background.fill(Color.web(color)));
            p.getStyleClass().add("theme-rect");
            p.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> themes.setTheme(theme));
            UIUtils.installTooltip(p, tooltip);
            return p;
        }

        protected void addSeparator(double size) {
            Region r = new Region();
            r.setMinHeight(USE_PREF_SIZE);
            r.setPrefHeight(size);
            r.setMaxHeight(USE_PREF_SIZE);
            container.getChildren().add(r);
        }
    }
}
