package io.github.palexdev.enbmanager.frontend.components.misc;

import io.github.palexdev.enbmanager.backend.repo.Config;
import io.github.palexdev.enbmanager.frontend.components.ConfigsList;
import io.github.palexdev.enbmanager.frontend.components.dialogs.ConfigDetailsDialog;
import io.github.palexdev.enbmanager.frontend.components.dialogs.DialogBase;
import io.github.palexdev.enbmanager.frontend.components.dialogs.IDialogs;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.mfxcomponents.controls.MaterialSurface;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxcomponents.controls.checkbox.MFXCheckBox;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.mfxcomponents.window.MFXPlainContent;
import io.github.palexdev.mfxcomponents.window.popups.MFXTooltip;
import io.github.palexdev.mfxcore.builders.bindings.BooleanBindingBuilder;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.virtualizedfx.cell.Cell;
import javafx.beans.property.*;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.util.Duration;

import java.util.Optional;

public class ConfigCell extends HBox implements Cell<Config> {
    private final ConfigsList list;

    private final ReadOnlyObjectWrapper<Config> item = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyIntegerWrapper index = new ReadOnlyIntegerWrapper(-1);
    private final ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper(false) {
        @Override
        protected void invalidated() {
            boolean state = get();
            PseudoClasses.SELECTED.setOn(ConfigCell.this, state);
        }
    };

    private final MaterialSurface surface;
    private final MFXCheckBox checkbox;
    private final MFXIconButton details;

    private static final MFXStageDialog sd;

    //================================================================================
    // Constructors
    //================================================================================
    public ConfigCell(ConfigsList list, Config item) {
        this.list = list;

        surface = new MaterialSurface(this);
        surface.getRippleGenerator().enable();
        checkbox = new MFXCheckBox();
        details = new MFXIconButton().filled();
        details.setOnAction(e -> showDetails());
        MFXTooltip tp = new MFXTooltip(details);
        tp.setContent(new MFXPlainContent("Details"));
        tp.setInDelay(M3Motion.SHORT2);
        tp.setOutDelay(Duration.ZERO);
        tp.install(details);

        getChildren().addAll(surface, checkbox, details);
        updateItem(item);
        initialize();
    }

    static {
        // Shared instance!
        sd = new MFXStageDialog();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add("config-cell");

        SelectionModel<Config> model = list.getSelectionModel();
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
        SelectionModel<Config> model = list.getSelectionModel();
        int index = getIndex();
        if (checked) {
            model.updateSelection(index);
        } else {
            model.deselectIndex(index);
        }
    }

    protected void showDetails() {
        Config config = getItem();
        ConfigDetailsDialog dialog = ConfigDetailsDialog.instance();
        dialog.setConfig(config);
        IDialogs.DialogConfig<DialogBase> dConfig = new IDialogs.DialogConfig<>()
            .implicitOwner()
            .setShowMinimize(false)
            .setModality(Modality.WINDOW_MODAL)
            .setHeaderText("Config: %s".formatted(config.name()));
        dConfig.configure(sd);
        dialog.alwaysOnTopProperty().bind(sd.alwaysOnTopProperty());
        dialog.setOnAlwaysOnTop(event -> sd.setAlwaysOnTop(!dialog.isAlwaysOnTop()));
        dialog.setOnMinimize(event -> sd.setIconified(true));
        dialog.setOnClose(e -> dConfig.getOnClose().accept(sd));
        dConfig.configure(dialog);
        sd.setContent(dialog);
        sd.showAndWait();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public void updateItem(Config item) {
        setItem(item);
        checkbox.setText(item.path().getFileName().toString());
    }

    @Override
    public void updateIndex(int index) {
        setIndex(index);
    }

    @Override
    public void dispose() {
        surface.dispose();
        Optional.ofNullable(details.getMFXTooltip()).ifPresent(MFXTooltip::dispose);
        getChildren().clear();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        double x = getLayoutX();
        double y = getLayoutX();
        double w = getWidth();
        double h = getHeight();
        surface.resizeRelocate(0, 0, w, h);
        positionInArea(details, x, y, w - snappedRightInset(), h, 0, HPos.RIGHT, VPos.CENTER);
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public Config getItem() {
        return item.get();
    }

    public ReadOnlyObjectProperty<Config> itemProperty() {
        return item.getReadOnlyProperty();
    }

    protected void setItem(Config item) {
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
