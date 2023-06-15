package io.github.palexdev.enbmanager.frontend.components;

import io.github.palexdev.enbmanager.backend.utils.FileUtils;
import io.github.palexdev.enbmanager.frontend.components.misc.CheckColumn;
import io.github.palexdev.enbmanager.frontend.components.misc.SelectionModel;
import io.github.palexdev.materialfx.utils.ListChangeProcessor;
import io.github.palexdev.mfxcomponents.controls.base.MFXStyleable;
import io.github.palexdev.mfxcore.utils.fx.ListChangeHelper;
import io.github.palexdev.virtualizedfx.cell.TableCell;
import io.github.palexdev.virtualizedfx.controls.VirtualScrollPane;
import io.github.palexdev.virtualizedfx.enums.ColumnsLayoutMode;
import io.github.palexdev.virtualizedfx.table.VirtualTable;
import io.github.palexdev.virtualizedfx.table.defaults.DefaultTableColumn;
import io.github.palexdev.virtualizedfx.table.defaults.SimpleTableCell;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Set;

public class FilesTable extends VirtualTable<Path> implements MFXStyleable {
    //================================================================================
    // Properties
    //================================================================================
    private final SelectionModel<Path> model = new SelectionModel<>();
    private final ListChangeListener<? super Path> itemsChanged = this::itemsChanged;

    //================================================================================
    // Constructors
    //================================================================================
    public FilesTable() {
        initialize();
    }

    public FilesTable(ObservableList<Path> items) {
        super();
        setItems(items);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().setAll(defaultStyleClasses());
        model.itemsProperty().bind(itemsProperty());
        setColumnsLayoutMode(ColumnsLayoutMode.VARIABLE);
        setOnMousePressed(e -> requestFocus());

        itemsProperty().addListener((ob, o, n) -> {
            if (o != null) o.removeListener(itemsChanged);
            if (n != null) n.addListener(itemsChanged);
        });
        getItems().addListener(itemsChanged);

        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        CheckColumn<Path> checkColumn = new CheckColumn<>(this);
        DefaultTableColumn<Path, TableCell<Path>> name = new DefaultTableColumn<>(this, "Name");
        name.setCellFactory(p -> new SimpleTableCell<>(p, Path::getFileName));
        DefaultTableColumn<Path, TableCell<Path>> folder = new DefaultTableColumn<>(this, "Parent");
        folder.setCellFactory(p -> new SimpleTableCell<>(p, cp -> cp.getParent().getFileName()));
        DefaultTableColumn<Path, TableCell<Path>> modified = new DefaultTableColumn<>(this, "Last Modified");
        modified.setCellFactory(p -> new SimpleTableCell<>(p, cp -> {
            Instant instant = Instant.ofEpochMilli(cp.toFile().lastModified());
            LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            return dtf.format(ldt);
        }));
        DefaultTableColumn<Path, TableCell<Path>> type = new DefaultTableColumn<>(this, "Type");
        type.setCellFactory(p -> new SimpleTableCell<>(p, cp -> {
            if (Files.isDirectory(cp)) return "Directory";
            return "File";
        }));
        DefaultTableColumn<Path, TableCell<Path>> size = new DefaultTableColumn<>(this, "Size");
        size.setCellFactory(p -> new SimpleTableCell<>(p, cp -> {
            if (Files.isDirectory(cp)) return "";
            return FileUtils.sizeToString(cp.toFile());
        }));
        getColumns().addAll(checkColumn, name, folder, modified, type, size);
    }

    protected void itemsChanged(ListChangeListener.Change<? extends Path> change) {
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
        return List.of("files-table");
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
    public SelectionModel<Path> getSelectionModel() {
        return model;
    }

}
