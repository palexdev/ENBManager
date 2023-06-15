package io.github.palexdev.enbmanager.frontend.components;

import io.github.palexdev.enbmanager.backend.repo.Config;
import io.github.palexdev.enbmanager.frontend.components.misc.ConfigCell;
import io.github.palexdev.enbmanager.frontend.components.misc.SelectionModel;
import io.github.palexdev.materialfx.utils.ListChangeProcessor;
import io.github.palexdev.mfxcomponents.controls.base.MFXStyleable;
import io.github.palexdev.mfxcore.utils.fx.ListChangeHelper;
import io.github.palexdev.virtualizedfx.cell.Cell;
import io.github.palexdev.virtualizedfx.flow.VirtualFlow;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Set;

public class ConfigsList extends VirtualFlow<Config, Cell<Config>> implements MFXStyleable {
    //================================================================================
    // Properties
    //================================================================================
    private final SelectionModel<Config> model = new SelectionModel<>();
    private final ListChangeListener<? super Config> itemsChanged = this::itemsChanged;

    //================================================================================
    // Constructors
    //================================================================================
    public ConfigsList() {
        initialize();
    }

    public ConfigsList(ObservableList<Config> items) {
        super();
        setItems(items);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().setAll(defaultStyleClasses());
        model.setAllowMultipleSelection(false);
        model.itemsProperty().bind(itemsProperty());
        itemsProperty().addListener((ob, o, n) -> {
            if (o != null) o.removeListener(itemsChanged);
            if (n != null) n.addListener(itemsChanged);
        });
        getItems().addListener(itemsChanged);

        setCellFactory(t -> new ConfigCell(this, t));
    }

    protected void itemsChanged(ListChangeListener.Change<? extends Config> change) {
        if (model.getSelection().isEmpty()) return;
        if (change.getList().isEmpty()) {
            model.clearSelection();
            return;
        }

        ListChangeHelper.Change c = ListChangeHelper.instance().processChange(change).get(0);
        ListChangeProcessor updater = new ListChangeProcessor(model.getSelectedIndexes());
        switch (c.getType()) {
            case REPLACE -> model.replaceSelection(c.getIndexes().toArray(Integer[]::new));
            case ADD -> {
                Set<Integer> added = c.getIndexes();
                updater.computeAddition(added.size(), c.getFrom());
                model.replaceSelection(updater.getIndexes().toArray(Integer[]::new));
            }
            case REMOVE -> {
                Set<Integer> removed = c.getIndexes();
                updater.computeRemoval(removed, c.getFrom());
                model.replaceSelection(updater.getIndexes().toArray(Integer[]::new));
            }
        }
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public List<String> defaultStyleClasses() {
        return List.of("configs-list");
    }

    //================================================================================
    // Getters
    //================================================================================
    public SelectionModel<Config> getSelectionModel() {
        return model;
    }
}
