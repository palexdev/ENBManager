package io.github.palexdev.enbmanager.frontend.components.dialogs;

import io.github.palexdev.enbmanager.backend.repo.Config;
import io.github.palexdev.enbmanager.frontend.components.FilesTable;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.virtualizedfx.controls.VirtualScrollPane;
import io.github.palexdev.virtualizedfx.utils.VSPUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Objects;

public class ConfigDetailsDialog extends DialogBase {
    //================================================================================
    // Static Properties
    //================================================================================
    // Since this is a pretty heavy-weight dialog (due to the table), it would be better to use a single one
    // and just update the shown config
    private static final ConfigDetailsDialog instance = new ConfigDetailsDialog(null);

    public static ConfigDetailsDialog instance() {
        return instance;
    }

    //================================================================================
    // Properties
    //================================================================================
    private final ObjectProperty<Config> config = new SimpleObjectProperty<>() {
        @Override
        public void set(Config newValue) {
            Config oldValue = get();
            super.set(newValue);
            if (!Objects.equals(oldValue, newValue)) {
                initDialog();
                table.autosizeColumns();
            }
        }
    };
    private final FilesTable table;

    //================================================================================
    // Constructors
    //================================================================================
    public ConfigDetailsDialog(Config config) {
        // Build view
        table = new FilesTable();
        table.getColumns().remove(0); // Check column is not needed here
        VirtualScrollPane vsp = table.wrap();
        Runnable speedAction = () -> {
            double ch = table.getCellHeight();
            double cw = table.getColumns().stream()
                .mapToDouble(c -> c.getRegion().getWidth())
                .min()
                .orElse(ch);
            VSPUtils.setHSpeed(vsp, cw / 3, cw, cw / 2);
            VSPUtils.setVSpeed(vsp, ch / 3, ch, ch / 2);
        };
        When.onInvalidated(table.estimatedSizeProperty())
            .then(i -> speedAction.run())
            .executeNow()
            .listen();
        setContent(vsp);

        // Set config thus initializing the dialog
        setConfig(config);

        getStyleClass().add("config-details-dialog");
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void initDialog() {
        Config config = getConfig();
        if (config != null) {
            table.getItems().setAll(config.files());
            return;
        }
        table.getItems().clear();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public Config getConfig() {
        return config.get();
    }

    public ObjectProperty<Config> configProperty() {
        return config;
    }

    public void setConfig(Config config) {
        this.config.set(config);
    }
}
