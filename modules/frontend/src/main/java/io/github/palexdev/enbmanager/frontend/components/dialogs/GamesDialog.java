package io.github.palexdev.enbmanager.frontend.components.dialogs;

import io.github.palexdev.enbmanager.backend.games.Game;
import io.github.palexdev.enbmanager.backend.settings.base.GameSettings;
import io.github.palexdev.enbmanager.backend.utils.GameUtils;
import io.github.palexdev.enbmanager.frontend.components.FloatingField;
import io.github.palexdev.enbmanager.frontend.components.GamesGrid;
import io.github.palexdev.enbmanager.frontend.components.misc.SelectionModel;
import io.github.palexdev.enbmanager.frontend.utils.UIUtils;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxcomponents.controls.checkbox.MFXCheckBox;
import io.github.palexdev.mfxcomponents.theming.Fonts;
import io.github.palexdev.mfxcore.base.properties.resettable.ResettableObjectProperty;
import io.github.palexdev.mfxcore.builders.InsetsBuilder;
import io.github.palexdev.mfxcore.collections.ObservableGrid;
import io.github.palexdev.virtualizedfx.controls.VirtualScrollPane;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This dialog is used by the app to let the user choose the game to manage, as well as the path
 * at which the game resides.
 */
public class GamesDialog extends DialogBase {
    //================================================================================
    // Properties
    //================================================================================
    private final ResettableObjectProperty<Game> game = new ResettableObjectProperty<>(null, null) {
        @Override
        protected void invalidated() {
            Game game = get();
            if (game == null) {
                return;
            }
            Optional<Path> opt = GameUtils.detectExecutable(game);
            opt.ifPresent(GamesDialog.this::setPath);
        }
    };
    private final ObjectProperty<Path> path = new SimpleObjectProperty<>(null);
    private final BooleanProperty remember = new SimpleBooleanProperty(false);

    private final GamesGrid grid = new GamesGrid();
    private final int N_COL = 4;

    //================================================================================
    // Constructors
    //================================================================================
    public GamesDialog() {
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        // If the dialog is called by ENBManager, so before the main scene is created, fonts won't still be available
        // Workaround: add them here too
        Fonts.ROBOTO.applyOn(this);
        build();

        addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.isSecondaryButtonDown()) getScene().getWindow().hide();
        });
    }

    /**
     * Builds this dialog's content.
     */
    protected void build() {
        // Set title
        setHeaderText("Choose the game");

        // Build the games grid
        SelectionModel<Game> sModel = grid.getSelectionModel();
        VirtualScrollPane vsp = grid.wrap();
        sModel.getSelection().addListener((InvalidationListener) i -> setGame(sModel.getSelectedItem()));

        // Build the UI to choose the path
        FloatingField field = new FloatingField("", "Game's directory");
        field.field().textProperty().bind(path.map(p -> {
            if (p == null) return "";
            return p.toString();
        }));
        field.setEditable(false);
        field.setMaxWidth(Double.MAX_VALUE);
        MFXIconButton chooseIcon = new MFXIconButton().filled();
        chooseIcon.disableProperty().bind(game.isNull());
        chooseIcon.setOnAction(e -> pickGameFolder());
        chooseIcon.getStyleClass().add("choose-icon");
        HBox pathBox = new HBox(field, chooseIcon);
        pathBox.getStyleClass().add("pbox");
        HBox.setHgrow(field, Priority.ALWAYS);

        // Build checkbox for remember setting
        MFXCheckBox rememberCheck = new MFXCheckBox("Remember choice");
        rememberCheck.selectedProperty().bindBidirectional(remember);

        // Build dialog actions
        MFXButton ok = new MFXButton("Confirm").filled();
        ok.disableProperty().bind(path.isNull());
        ok.setOnAction(e -> getScene().getWindow().hide());
        addActions(ok);

        // Build container
        VBox box = new VBox(vsp, pathBox, rememberCheck);
        box.getStyleClass().add("box");
        VBox.setVgrow(vsp, Priority.ALWAYS);
        VBox.setMargin(rememberCheck, InsetsBuilder.left(6));
        setContent(box);
    }

    protected void pickGameFolder() {
        Game game = getGame();
        Path path = getPath();
        GameSettings settings = game.getSettings();
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(UIUtils.toFilter(game));
        if (path != null) fc.setInitialDirectory(path.toFile());

        File file = fc.showOpenDialog(getScene().getWindow());
        if (file == null) return;
        path = file.toPath().getParent();
        if (path == null) return;

        setPath(path);
        settings.path.set(path.toString());
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public List<String> defaultStyleClasses() {
        return List.of("dialog-base", "games-dialog");
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public Choice getChoice() {
        return Choice.of(getGame(), getPath(), isRemember());
    }

    public Game getGame() {
        return game.get();
    }

    protected void setGame(Game game) {
        this.game.set(game);
    }

    public Path getPath() {
        return path.get();
    }

    protected void setPath(Path path) {
        this.path.set(path);
    }

    public boolean isRemember() {
        return remember.get();
    }

    protected void setRemember(boolean remember) {
        this.remember.set(remember);
    }

    public void setGames(List<Game> games) {
        // An ObservableGrid MUST have a number of items that is multiple of the number of columns
        // otherwise it throws an exception
        int missing = (int) (Math.ceil(games.size() / (double) N_COL) * N_COL - games.size());
        List<Game> tmp = new ArrayList<>(games);
        for (int i = 0; i < missing; i++) tmp.add(null);
        grid.setItems(ObservableGrid.fromList(tmp, N_COL));
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    public record Choice(Game game, Path path, boolean remember) {
        public static Choice of(Game game, Path path, boolean remember) {
            return new Choice(game, path, remember);
        }

        public boolean isValid() {
            return game != null &&
                (path != null && Files.isDirectory(path));
        }
    }
}
