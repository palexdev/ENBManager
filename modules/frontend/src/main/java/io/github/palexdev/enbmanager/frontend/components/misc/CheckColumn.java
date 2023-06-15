package io.github.palexdev.enbmanager.frontend.components.misc;

import io.github.palexdev.enbmanager.frontend.components.FilesTable;
import io.github.palexdev.mfxcomponents.behaviors.MFXCheckBoxBehavior;
import io.github.palexdev.mfxcomponents.controls.MaterialSurface;
import io.github.palexdev.mfxcomponents.controls.checkbox.MFXCheckBox;
import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import io.github.palexdev.mfxcore.builders.bindings.BooleanBindingBuilder;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.virtualizedfx.table.VirtualTable;
import io.github.palexdev.virtualizedfx.table.defaults.DefaultTableColumn;
import io.github.palexdev.virtualizedfx.table.defaults.DefaultTableColumnSkin;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Skin;

import java.nio.file.Path;

public class CheckColumn<T> extends DefaultTableColumn<T, CheckCell<T>> {
    //================================================================================
    // Properties
    //================================================================================
    private final MFXCheckBox checkbox;

    //================================================================================
    // Constructors
    //================================================================================
    public CheckColumn(VirtualTable<T> table) {
        super(table);
        FilesTable ft = (FilesTable) table;
        SelectionModel<Path> model = ft.getSelectionModel();

        checkbox = new MFXCheckBox() {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                MaterialSurface surface = ((MaterialSurface) getChildren().get(0));
                surface.resize(surface.getPrefWidth(), surface.getPrefHeight());
                positionInArea(surface, 0, 0, getWidth(), getHeight(), 0, HPos.LEFT, VPos.CENTER);
                // TODO fix in MFXComponents
            }
        };
        checkbox.setAllowIndeterminate(true);
        checkbox.setBehaviorProvider(() -> new MFXCheckBoxBehavior(checkbox) {
            @Override
            protected void handleSelection() {
                MFXCheckBox check = getNode();
                if (check.selectedProperty().isBound() || check.indeterminateProperty().isBound()) {
                    check.fire();
                    return;
                }
                super.handleSelection();
            }
        });
        checkbox.selectedProperty().bind(BooleanBindingBuilder.build()
            .setMapper(() -> {
                ObservableMap<Integer, Path> selection = model.getSelection();
                return selection.size() == table.getItems().size() && table.getItems().size() > 0;
            })
            .addSources(model.getSelection())
            .addSources(table.getItems())
            .get()
        );
        checkbox.indeterminateProperty().bind(BooleanBindingBuilder.build()
            .setMapper(() -> {
                ObservableMap<Integer, Path> selection = model.getSelection();
                return selection.size() > 0 && selection.size() < table.getItems().size();
            })
            .addSources(model.getSelection())
            .addSources(table.getItems())
            .get()
        );
        checkbox.setOnAction(e -> {
            if (!checkbox.isSelected() || checkbox.isIndeterminate()) {
                model.replaceSelection(IntegerRange.expandRangeToArray(0, table.getItems().size() - 1));
                return;
            }
            model.clearSelection();
        });
        setGraphic(checkbox);

        setCellFactory(CheckCell::new);
        getStyleClass().add("check-column");
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void handleSelection() {
        VirtualTable<T> table = getTable();
        if (table instanceof FilesTable ft) {
            SelectionModel<Path> model = ft.getSelectionModel();
            ObservableMap<Integer, Path> selection = model.getSelection();
            int nItems = table.getItems().size();
            if (selection.isEmpty()) {
                checkbox.setIndeterminate(false);
                checkbox.setSelected(false);
                return;
            }
            boolean state = selection.size() == nItems;
            checkbox.setSelected(state);
            checkbox.setIndeterminate(!state);
        }
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return new DefaultTableColumnSkin<>(this) {
            final When<?> gWhen;

            {
                getChildren().clear();
                gWhen = When.onChanged(graphicProperty())
                    .then((o, n) -> {
                        if (o != null) getChildren().remove(o);
                        if (n != null) getChildren().add(n);
                    })
                    .executeNow()
                    .listen();
            }

            @Override
            public void dispose() {
                gWhen.dispose();
                super.dispose();
            }
        };
    }
}
