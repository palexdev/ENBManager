package io.github.palexdev.enbmanager.frontend.views;

import io.github.palexdev.enbmanager.backend.events.bus.SimpleEventBus;
import io.github.palexdev.enbmanager.backend.repo.Config;
import io.github.palexdev.enbmanager.frontend.ENBManager;
import io.github.palexdev.enbmanager.frontend.components.ActionsPane;
import io.github.palexdev.enbmanager.frontend.components.ConfigsList;
import io.github.palexdev.enbmanager.frontend.components.dialogs.DialogBase;
import io.github.palexdev.enbmanager.frontend.components.dialogs.IDialogs;
import io.github.palexdev.enbmanager.frontend.components.dialogs.IDialogs.DialogConfig;
import io.github.palexdev.enbmanager.frontend.components.misc.SelectionModel;
import io.github.palexdev.enbmanager.frontend.model.ENBManagerModel;
import io.github.palexdev.enbmanager.frontend.utils.UIUtils;
import io.github.palexdev.enbmanager.frontend.views.RepoView.RepoPane;
import io.github.palexdev.enbmanager.frontend.views.base.View;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.virtualizedfx.controls.VirtualScrollPane;
import io.github.palexdev.virtualizedfx.utils.VSPUtils;
import io.inverno.core.annotation.Bean;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import static io.github.palexdev.enbmanager.frontend.ENBManager.LOGGER;

@Bean
public class RepoView extends View<RepoPane> {
    //================================================================================
    // Properties
    //================================================================================
    private final IDialogs.Dialogs dialogs;
    private final ENBManagerModel model;

    //================================================================================
    // Constructors
    //================================================================================
    public RepoView(SimpleEventBus events, IDialogs.Dialogs dialogs, ENBManagerModel model) {
        super(events);
        this.dialogs = dialogs;
        this.model = model;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected RepoPane build() {
        return new RepoPane();
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    public class RepoPane extends StackPane {
        private final ConfigsList list;

        RepoPane() {
            // Init list
            list = new ConfigsList(model.getConfigs());
            SelectionModel<Config> sModel = list.getSelectionModel();
            VirtualScrollPane vsp = list.wrap();
            vsp.getStylesheets().clear();
            VBox.setVgrow(vsp, Priority.ALWAYS);
            Runnable speedAction = () -> {
                double ch = list.getCellSize();
                VSPUtils.setVSpeed(vsp, ch / 3, ch, ch / 2);
            };
            When.onInvalidated(list.cellSizeProperty())
                .then(i -> speedAction.run())
                .executeNow()
                .listen();

            // Init actions
            ActionsPane pane = new ActionsPane(vsp);
            MFXIconButton refresh = createAction("refresh", "Refresh configs", e -> refresh());
            MFXIconButton load = createAction("load", "Load selected", e -> load());
            load.disableProperty().bind(sModel.emptyProperty());
            MFXIconButton remove = createAction("delete", "Delete selected", e -> delete());
            remove.disableProperty().bind(sModel.emptyProperty());
            pane.addActions(refresh, load, remove);

            // Finalize init
            getChildren().add(pane);
            getStyleClass().add("repo-view");
        }

        MFXIconButton createAction(String type, String tooltip, EventHandler<ActionEvent> action) {
            MFXIconButton btn = new MFXIconButton().filled();
            btn.getStyleClass().add(type);
            btn.setOnAction(action);
            UIUtils.installTooltip(btn, tooltip);
            return btn;
        }

        void refresh() {
            model.refreshConfigs();
        }

        void load() {
            Config config = list.getSelectionModel().getSelectedItem();
            if (config == null) return;
            DialogBase.DialogType type = DialogBase.DialogType.INFO;
            StringBuilder sb = new StringBuilder("Are you sure you want to load configuration: " + config.name());
            if (!model.getFiles().isEmpty()) {
                type = DialogBase.DialogType.WARN;
                sb.append("\nFiles of an existing configuration have been detected!");
            }
            boolean confirm = dialogs.showConfirmDialog(MainView.class, type, "Load",
                () -> new DialogConfig<>()
                    .implicitOwner()
                    .setShowMinimize(false)
                    .setModality(Modality.APPLICATION_MODAL)
                    .setHeaderText("Load configuration!")
                    .setContentText(sb.toString())
            );
            if (!confirm) return;
            model.load(config).accept(
                f -> {
                    ENBManager.showToast("Config %s was not loaded correctly".formatted(config.name()));
                    LOGGER.error(f.message());
                },
                s -> ENBManager.showToast("Config %s was loaded correctly".formatted(config.name()))
            );
        }

        void delete() {
            Config config = list.getSelectionModel().getSelectedItem();
            boolean confirm = dialogs.showConfirmDialog(MainView.class, DialogBase.DialogType.WARN, "Delete",
                () -> new DialogConfig<>()
                    .implicitOwner()
                    .setShowMinimize(false)
                    .setModality(Modality.APPLICATION_MODAL)
                    .setHeaderText("Config deletion!")
                    .setContentText("Delete configuration %s and all its files?".formatted(config.name()))
            );
            if (!confirm) return;
            model.delete(config).accept(
                f -> {
                    ENBManager.showToast("Config was not deleted correctly");
                    LOGGER.error(f.message());
                },
                s -> ENBManager.showToast("Config was deleted correctly")
            );
        }
    }
}
