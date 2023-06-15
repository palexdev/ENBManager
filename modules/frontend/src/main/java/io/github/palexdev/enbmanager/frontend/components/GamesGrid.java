package io.github.palexdev.enbmanager.frontend.components;

import io.github.palexdev.enbmanager.backend.games.Game;
import io.github.palexdev.enbmanager.frontend.components.misc.GameCell;
import io.github.palexdev.enbmanager.frontend.components.misc.SelectionModel;
import io.github.palexdev.mfxcomponents.controls.base.MFXStyleable;
import io.github.palexdev.mfxcore.collections.ObservableGrid;
import io.github.palexdev.virtualizedfx.cell.GridCell;
import io.github.palexdev.virtualizedfx.controls.VirtualScrollPane;
import io.github.palexdev.virtualizedfx.grid.VirtualGrid;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class GamesGrid extends VirtualGrid<Game, GridCell<Game>> implements MFXStyleable {
    //================================================================================
    // Properties
    //================================================================================
    private final SelectionModel<Game> model = new SelectionModel<>();

    //================================================================================
    // Constructors
    //================================================================================
    public GamesGrid() {
        itemsProperty().addListener(i -> updateModelItems());
        setCellFactory(g -> new GameCell(this, g));
        model.setAllowMultipleSelection(false);
        getStyleClass().addAll(defaultStyleClasses());
        setOnMousePressed(e -> requestFocus());
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * {@link SelectionModel} uses data structures of type {@link ObservableList} for the items,
     * while the grid uses {@link ObservableGrid}. This method uses {@link ObservableGrid#getData()} to
     * update the model when needed.
     */
    protected void updateModelItems() {
        List<Game> data = getItems().getData();
        model.setItems(FXCollections.observableArrayList(data));
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public List<String> defaultStyleClasses() {
        return List.of("games-grid");
    }

    @Override
    public VirtualScrollPane wrap() {
        VirtualScrollPane vsp = super.wrap();
        vsp.getStylesheets().clear();
        return vsp;
    }

    //================================================================================
    // Getters
    //================================================================================
    public SelectionModel<Game> getSelectionModel() {
        return model;
    }
}
