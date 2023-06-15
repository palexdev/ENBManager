package io.github.palexdev.enbmanager.frontend.components.misc;

import io.github.palexdev.enbmanager.frontend.components.FilesTable;
import io.github.palexdev.mfxcomponents.controls.checkbox.MFXCheckBox;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.mfxcore.builders.bindings.BooleanBindingBuilder;
import io.github.palexdev.virtualizedfx.cell.TableCell;
import io.github.palexdev.virtualizedfx.table.TableColumn;
import io.github.palexdev.virtualizedfx.table.VirtualTable;
import io.github.palexdev.virtualizedfx.table.defaults.DefaultTableRow;
import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

import java.nio.file.Path;

public class CheckCell<T> extends HBox implements TableCell<T> {
    //================================================================================
    // Properties
    //================================================================================
    private TableColumn<T, ? extends TableCell<T>> column;
    private DefaultTableRow<T> row;

    private final ReadOnlyObjectWrapper<T> item = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyIntegerWrapper index = new ReadOnlyIntegerWrapper(-1);
    private final ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper(false) {
        @Override
        protected void invalidated() {
            boolean state = get();
            PseudoClasses.SELECTED.setOn(CheckCell.this, state);
            if (row != null) PseudoClasses.SELECTED.setOn(row, state);
        }
    };

    private final MFXCheckBox checkbox;

    //================================================================================
    // Constructors
    //================================================================================
    public CheckCell(T item) {
        setItem(item);
        checkbox = new MFXCheckBox();
        getChildren().setAll(checkbox);
        getStyleClass().add("check-cell");
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void initSelection() {
        VirtualTable<T> table = column.getTable();
        if (!(table instanceof FilesTable ft)) return;
        SelectionModel<Path> model = ft.getSelectionModel();

        selected.bind(BooleanBindingBuilder.build()
            .setMapper(() -> {
                boolean contained = model.getSelection().containsKey(getIndex());
                checkbox.setSelected(contained);
                return contained;
            })
            .addSources(model.getSelection(), index)
            .get()
        );
        checkbox.selectedProperty().addListener((ob, o, n) -> updateSelection(n));
    }

    protected void updateSelection(boolean checked) {
        if (column == null) return;
        if (!(column.getTable() instanceof FilesTable ft)) return;

        SelectionModel<Path> model = ft.getSelectionModel();
        int index = getIndex();
        if (checked) {
            model.updateSelection(index);
        } else {
            model.deselectIndex(index);
        }
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public void updateItem(T item) {
        setItem(item);
    }

    @Override
    public void updateColumn(TableColumn<T, ? extends TableCell<T>> column) {
        if (this.column == null && column != null) {
            this.column = column;
            initSelection();
            return;
        }
        this.column = column;
    }

    @Override
    public void updateRow(int rIndex, DefaultTableRow<T> row) {
        this.row = row;
        setIndex(rIndex);
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public T getItem() {
        return item.get();
    }

    public ReadOnlyObjectProperty<T> itemProperty() {
        return item.getReadOnlyProperty();
    }

    protected void setItem(T item) {
        this.item.set(item);
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

    public boolean isSelected() {
        return selected.get();
    }

    public ReadOnlyBooleanProperty selectedProperty() {
        return selected.getReadOnlyProperty();
    }

    protected void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
