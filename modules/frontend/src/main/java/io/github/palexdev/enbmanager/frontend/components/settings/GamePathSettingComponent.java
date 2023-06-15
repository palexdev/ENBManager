package io.github.palexdev.enbmanager.frontend.components.settings;

import io.github.palexdev.enbmanager.backend.games.Game;
import io.github.palexdev.enbmanager.backend.settings.base.Setting;
import io.github.palexdev.enbmanager.frontend.components.FloatingField;
import io.github.palexdev.enbmanager.frontend.utils.UIUtils;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxcore.utils.converters.FunctionalStringConverter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

public class GamePathSettingComponent extends FieldSettingComponent<String> {
    //================================================================================
    // Properties
    //================================================================================
    private final ObjectProperty<Game> game = new SimpleObjectProperty<>();
    private final ObjectProperty<Path> gamePath = new SimpleObjectProperty<>();

    //================================================================================
    // Constructors
    //================================================================================
    public GamePathSettingComponent(Setting<String> setting, Supplier<FloatingField> fieldFactory) {
        super(setting, fieldFactory);
        setConverter(FunctionalStringConverter.converter(
            s -> s,
            s -> s
        ));
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public List<String> defaultStyleClasses() {
        return List.of("setting-component", "path");
    }

    @Override
    protected MFXSkinBase<?, ?> buildSkin() {
        return new Skin(this);
    }

    @Override
    public Supplier<SettingComponentBehavior<String, Setting<String>>> defaultBehaviorProvider() {
        return () -> new SettingComponentBehavior<>(this) {
            @Override
            public void reset(MouseEvent me) {
                // Null events can come from the component's delegate method
                if (me == null || me.getButton() == MouseButton.PRIMARY) {
                    SettingComponent<String, Setting<String>> component = getNode();
                    component.getSetting().set(component.getInitialValue());
                    setGamePath(Path.of(component.getSetting().get()));
                }
            }
        };
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public Game getGame() {
        return game.get();
    }

    public ObjectProperty<Game> gameProperty() {
        return game;
    }

    public void setGame(Game game) {
        this.game.set(game);
    }

    public Path getGamePath() {
        return gamePath.get();
    }

    public ObjectProperty<Path> gamePathProperty() {
        return gamePath;
    }

    public void setGamePath(Path gamePath) {
        this.gamePath.set(gamePath);
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    class Skin extends FieldSettingComponent<String>.Skin {
        private final HBox container;

        protected Skin(FieldSettingComponent<String> component) {
            super(component);
            MFXIconButton chooseBtn = new MFXIconButton().filled();
            chooseBtn.setOnAction(e -> pickGameFolder());
            chooseBtn.getStyleClass().add("choose-icon");
            field.field().textProperty().bind(gamePathProperty().asString());
            field.setEditable(false);
            field.setMaxWidth(Double.MAX_VALUE);
            container = new HBox(field, chooseBtn, resetIcon);
            container.getStyleClass().add("container");
            HBox.setHgrow(field, Priority.ALWAYS);
            getChildren().setAll(container);
        }

        protected void pickGameFolder() {
            Game game = getGame();
            Path path = getGamePath();
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(UIUtils.toFilter(game));
            if (path != null) fc.setInitialDirectory(path.toFile());

            File file = fc.showOpenDialog(getScene().getWindow());
            if (file == null) return;
            path = file.toPath().getParent();
            if (path == null) return;

            setGamePath(path);
            getSetting().set(path.toString());
        }

        @Override
        protected void setText(String val) {
            // This is a bound value, no need to set
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            container.resizeRelocate(x, y, w, h);
        }
    }
}
