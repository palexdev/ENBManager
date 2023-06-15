package io.github.palexdev.enbmanager.frontend.views;

import io.github.palexdev.enbmanager.backend.events.bus.SimpleEventBus;
import io.github.palexdev.enbmanager.frontend.ENBManager;
import io.github.palexdev.enbmanager.frontend.components.ActionsPane;
import io.github.palexdev.enbmanager.frontend.components.FilesTable;
import io.github.palexdev.enbmanager.frontend.components.dialogs.ConfigSaveDialog;
import io.github.palexdev.enbmanager.frontend.components.dialogs.DialogBase;
import io.github.palexdev.enbmanager.frontend.components.dialogs.IDialogs;
import io.github.palexdev.enbmanager.frontend.components.dialogs.IDialogs.DialogConfig;
import io.github.palexdev.enbmanager.frontend.components.misc.SelectionModel;
import io.github.palexdev.enbmanager.frontend.model.ENBManagerModel;
import io.github.palexdev.enbmanager.frontend.utils.UIUtils;
import io.github.palexdev.enbmanager.frontend.views.HomeView.HomePane;
import io.github.palexdev.enbmanager.frontend.views.base.View;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxcomponents.window.popups.MFXTooltip;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.virtualizedfx.controls.VirtualScrollPane;
import io.github.palexdev.virtualizedfx.utils.VSPUtils;
import io.inverno.core.annotation.Bean;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.nio.file.Path;

import static io.github.palexdev.enbmanager.frontend.ENBManager.LOGGER;

@Bean
public class HomeView extends View<HomePane> {
    //================================================================================
    // Properties
    //================================================================================
    private final IDialogs.Dialogs dialogs;
    private final ENBManagerModel model;

    //================================================================================
    // Constructors
    //================================================================================
    public HomeView(SimpleEventBus events, IDialogs.Dialogs dialogs, ENBManagerModel model) {
        super(events);
        this.dialogs = dialogs;
        this.model = model;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected HomePane build() {
        return new HomePane();
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    public class HomePane extends StackPane {
        private final FilesTable table;

        HomePane() {
            // Init table
            table = new FilesTable(model.getFiles());
            SelectionModel<Path> sModel = table.getSelectionModel();
            VirtualScrollPane vsp = table.wrap();
            VBox.setVgrow(vsp, Priority.ALWAYS);
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
            table.autosizeColumns();

            // Init actions
            ActionsPane pane = new ActionsPane(vsp);
            MFXIconButton autosize = createAction("autosize", "Autosize columns", e -> table.autosizeColumns());
            MFXIconButton refresh = createAction("refresh", "Refresh files", e -> refresh());
            MFXIconButton save = createAction("save", "Save selection", e -> save());
            save.disableProperty().bind(sModel.emptyProperty());
            MFXIconButton delete = createAction("delete", "Delete selection", e -> delete());
            delete.disableProperty().bind(sModel.emptyProperty());
            pane.addActions(autosize, refresh, save, delete);

            // Finalize init
            getChildren().add(pane);
            getStyleClass().add("home-view");
        }

        MFXIconButton createAction(String type, String tooltip, EventHandler<ActionEvent> action) {
            MFXIconButton btn = new MFXIconButton().filled();
            btn.getStyleClass().addAll("action-button", type);
            btn.setOnAction(action);
            MFXTooltip tp = UIUtils.installTooltip(btn, tooltip);
            tp.setEventDispatcher(getEventDispatcher());
            // This is needed otherwise events are captured and consumed by the tooltip
            return btn;
        }

        void refresh() {
            model.updateFiles();
        }

        void save() {
            ObservableMap<Integer, Path> selection = table.getSelectionModel().getSelection();
            ConfigSaveDialog.Result result = dialogs.showConfigSaveDialog(MainView.class,
                () -> new DialogConfig<ConfigSaveDialog>()
                    .implicitOwner()
                    .setModality(Modality.APPLICATION_MODAL)
                    .setHeaderText("Save config")
                    .setContentText("Config name")
            );
            model.save(result.name(), selection.values()).accept(
                f -> {
                    ENBManager.showToast("Config %s was not saved".formatted(result.name()));
                    LOGGER.error(f.message());
                },
                s -> {
                    ENBManager.showToast("Config %s was saved".formatted(result.name()));
                    if (result.deleteOnSave()) model.delete(selection.values());
                }
            );
        }

        void delete() {
            ObservableMap<Integer, Path> selection = table.getSelectionModel().getSelection();
            boolean confirm = dialogs.showConfirmDialog(MainView.class, DialogBase.DialogType.WARN, "Delete",
                () -> new DialogConfig<>()
                    .implicitOwner()
                    .setModality(Modality.APPLICATION_MODAL)
                    .setHeaderText("File deletion")
                    .setContentText("Delete selected files?")
            );
            if (!confirm) return;
            model.delete(selection.values()).accept(
                f -> {
                    ENBManager.showToast("Files were not deleted successfully");
                    LOGGER.error(f.message());
                },
                s -> {
                    ENBManager.showToast("Files were deleted successfully");
                    table.getSelectionModel().clearSelection(); // TODO: upon delete, all elements become selected, FIX ME
                }
            );
        }
    }
}
