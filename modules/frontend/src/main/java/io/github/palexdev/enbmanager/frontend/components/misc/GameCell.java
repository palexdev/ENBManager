package io.github.palexdev.enbmanager.frontend.components.misc;

import io.github.palexdev.enbmanager.backend.games.Game;
import io.github.palexdev.enbmanager.frontend.components.GamesGrid;
import io.github.palexdev.enbmanager.frontend.utils.UIUtils;
import io.github.palexdev.mfxcomponents.controls.MaterialSurface;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.virtualizedfx.cell.GridCell;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class GameCell extends StackPane implements GridCell<Game> {
    //================================================================================
    // Properties
    //================================================================================
    private final ReadOnlyObjectWrapper<Game> game = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyIntegerWrapper index = new ReadOnlyIntegerWrapper();
    private final MaterialSurface surface;
    private final ImageView view;
    private final Label label;

    //================================================================================
    // Constructors
    //================================================================================
    public GameCell(GamesGrid grid, Game game) {
        VBox box = new VBox();
        UIUtils.ImgWrapper wrapper = UIUtils.createIconView(box);
        StackPane container = wrapper.container();
        container.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        this.view = wrapper.view();
        this.surface = wrapper.surface();
        this.label = new Label();

        box.getChildren().addAll(container, label);
        getChildren().add(box);
        getStyleClass().add("game-cell");
        updateItem(game);

        SelectionModel<Game> model = grid.getSelectionModel();
        model.getSelection().addListener((InvalidationListener) i ->
            PseudoClasses.SELECTED.setOn(this, Objects.equals(getGame(), model.getSelectedItem()))
        );
        box.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> model.updateSelection(getIndex()));
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public void updateItem(Game item) {
        setGame(item);
        if (item == null) {
            view.setImage(null);
            label.setText("");
            setVisible(false);
            return;
        }
        view.setImage(new Image(item.getIcon()));
        label.setText(item.getName());
        setVisible(true);
    }

    @Override
    public void updateIndex(int index) {
        setIndex(index);
    }

    @Override
    public void dispose() {
        surface.dispose();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public Game getGame() {
        return game.get();
    }

    public ReadOnlyObjectProperty<Game> gameProperty() {
        return game.getReadOnlyProperty();
    }

    protected void setGame(Game game) {
        this.game.set(game);
    }

    public int getIndex() {
        return index.get();
    }

    public ReadOnlyIntegerProperty indexProperty() {
        return index.getReadOnlyProperty();
    }

    protected void setIndex(int index) {
        this.index.set(index);
    }
}
