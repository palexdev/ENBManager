package io.github.palexdev.enbmanager.frontend.components.dialogs;

import io.github.palexdev.enbmanager.frontend.ENBManager;
import io.github.palexdev.enbmanager.frontend.views.base.View;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.inverno.core.annotation.Bean;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This service API allows to show dialogs from everywhere in the app.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public interface IDialogs {

    GamesDialog.Choice showGamesDialog(Class<? extends View<?>> view, DialogConfig<GamesDialog> config);

    default GamesDialog.Choice showGamesDialog(Class<? extends View<?>> view, Supplier<DialogConfig<GamesDialog>> configSupplier) {
        return showGamesDialog(view, configSupplier.get());
    }

    String showFieldDialog(Class<? extends View<?>> view, DialogConfig<FieldDialog> config);

    default String showFieldDialog(Class<? extends View<?>> view, Supplier<DialogConfig<FieldDialog>> configSupplier) {
        return showFieldDialog(view, configSupplier.get());
    }

    ConfigSaveDialog.Result showConfigSaveDialog(Class<? extends View<?>> view, DialogConfig<ConfigSaveDialog> config);

    default ConfigSaveDialog.Result showConfigSaveDialog(Class<? extends View<?>> view, Supplier<DialogConfig<ConfigSaveDialog>> configSupplier) {
        return showConfigSaveDialog(view, configSupplier.get());
    }

    boolean showConfirmDialog(Class<? extends View<?>> view, DialogBase.DialogType type, String action, DialogConfig<DialogBase> config);

    default boolean showConfirmDialog(Class<? extends View<?>> view, DialogBase.DialogType type, String action, Supplier<DialogConfig<DialogBase>> configSupplier) {
        return showConfirmDialog(view, type, action, configSupplier.get());
    }

    void showDialog(Class<? extends View<?>> view, DialogBase content, DialogConfig config);

    default void showDialog(Class<? extends View<?>> view, DialogBase content, Supplier<DialogConfig> configSupplier) {
        showDialog(view, content, configSupplier.get());
    }

    //================================================================================
    // Impl
    //================================================================================
    @Bean
    class Dialogs implements IDialogs {
        private final Map<Class<? extends View<?>>, MFXStageDialog> cache = new HashMap<>();

        @Override
        public GamesDialog.Choice showGamesDialog(Class<? extends View<?>> view, DialogConfig<GamesDialog> config) {
            GamesDialog content = new GamesDialog();
            showDialog(view, content, config);
            return content.getChoice();
        }

        @Override
        public String showFieldDialog(Class<? extends View<?>> view, DialogConfig<FieldDialog> config) {
            FieldDialog content = new FieldDialog();
            showDialog(view, content, config);
            return content.getValue();
        }

        @Override
        public ConfigSaveDialog.Result showConfigSaveDialog(Class<? extends View<?>> view, DialogConfig<ConfigSaveDialog> config) {
            ConfigSaveDialog content = new ConfigSaveDialog();
            showDialog(view, content, config);
            return content.getResult();
        }

        @Override
        public boolean showConfirmDialog(Class<? extends View<?>> view, DialogBase.DialogType type, String action, DialogConfig<DialogBase> config) {
            AtomicBoolean choice = new AtomicBoolean(false);
            DialogBase content = switch (type) {
                case INFO -> DialogBase.info();
                case WARN -> DialogBase.warn();
                case ERROR -> DialogBase.error();
            };
            content.addActions(
                Map.entry(
                    new MFXButton(action).text(),
                    e -> {
                        choice.set(true);
                        content.getScene().getWindow().hide();
                    }
                ),
                Map.entry(
                    new MFXButton("Cancel").text(),
                    e -> content.getScene().getWindow().hide()
                )
            );
            showDialog(view, content, config);
            return choice.get();
        }

        @Override
        public void showDialog(Class<? extends View<?>> view, DialogBase content, DialogConfig config) {
            MFXStageDialog sd = cache.computeIfAbsent(view, v -> new MFXStageDialog());
            config.configure(sd);

            content.alwaysOnTopProperty().bind(sd.alwaysOnTopProperty());
            content.setOnAlwaysOnTop(event -> sd.setAlwaysOnTop(!content.isAlwaysOnTop()));
            content.setOnMinimize(event -> sd.setIconified(true));
            content.setOnClose(e -> config.onClose.accept(sd));
            config.configure(content);

            sd.setContent(content);
            sd.showAndWait();
        }
    }

    //================================================================================
    // Config
    //================================================================================
    class DialogConfig<D extends DialogBase> {
        private static Stage primary;
        private Consumer<D> onConfigure = d -> {};
        private String headerText = "";
        private boolean preserveHeader = false;
        private String contentText = "";
        private boolean preserveContent = false;
        private boolean showAlwaysOnTop = true;
        private boolean showMinimize = true;
        private boolean showClose = true;
        private Modality modality = Modality.NONE;
        private Window owner = null;
        private Pane ownerNode = null;
        private boolean centerInOwnerNode = true;
        private boolean scrimOwner = true;
        private double scrimStrength = 0.2;
        private ScrimPriority scrimPriority = ScrimPriority.WINDOW;
        private boolean draggable = true;
        private Consumer<Stage> onClose = Window::hide;
        private EventHandler<WindowEvent> onShown = null;

        public void configure(D content) {
            if (!preserveHeader) content.setHeaderText(headerText);
            if (!preserveContent) content.setContentText(contentText);
            content.setShowAlwaysOnTop(showAlwaysOnTop);
            content.setShowMinimize(showMinimize);
            content.setShowClose(showClose);
            onConfigure.accept(content);
        }

        public void configure(MFXStageDialog sd) {
            try {
                sd.initModality(modality);
                sd.initOwner(owner);
            } catch (Exception ignored) {}
            sd.setOwnerNode(ownerNode);
            sd.setCenterInOwnerNode(centerInOwnerNode);
            sd.setScrimOwner(scrimOwner);
            sd.setScrimStrength(scrimStrength);
            sd.setScrimPriority(scrimPriority);
            sd.setDraggable(draggable);
            sd.setOnShown(onShown);
        }

        public DialogConfig<D> implicitOwner() {
            Stage stage = primary;
            if (primary == null) {
                stage = primary = Window.getWindows().stream()
                    .filter(w -> w instanceof Stage)
                    .map(w -> (Stage) w)
                    .filter(w -> ENBManager.getTitle().equals(w.getTitle()))
                    .findFirst()
                    .orElse(null);
            }
            if (stage == null) return this;
            owner = stage;
            ownerNode = (Pane) stage.getScene().getRoot();
            return this;
        }

        public Consumer<D> getOnConfigure() {
            return onConfigure;
        }

        public DialogConfig<D> setOnConfigure(Consumer<D> onConfigure) {
            this.onConfigure = onConfigure;
            return this;
        }

        public String getHeaderText() {
            return headerText;
        }

        public DialogConfig<D> setHeaderText(String headerText) {
            this.headerText = headerText;
            return this;
        }

        public boolean isPreserveHeader() {
            return preserveHeader;
        }

        public DialogConfig<D> setPreserveHeader(boolean preserveHeader) {
            this.preserveHeader = preserveHeader;
            return this;
        }

        public String getContentText() {
            return contentText;
        }

        public DialogConfig<D> setContentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        public boolean isPreserveContent() {
            return preserveContent;
        }

        public DialogConfig<D> setPreserveContent(boolean preserveContent) {
            this.preserveContent = preserveContent;
            return this;
        }

        public boolean isShowAlwaysOnTop() {
            return showAlwaysOnTop;
        }

        public DialogConfig<D> setShowAlwaysOnTop(boolean showAlwaysOnTop) {
            this.showAlwaysOnTop = showAlwaysOnTop;
            return this;
        }

        public boolean isShowMinimize() {
            return showMinimize;
        }

        public DialogConfig<D> setShowMinimize(boolean showMinimize) {
            this.showMinimize = showMinimize;
            return this;
        }

        public boolean isShowClose() {
            return showClose;
        }

        public DialogConfig<D> setShowClose(boolean showClose) {
            this.showClose = showClose;
            return this;
        }

        public Modality getModality() {
            return modality;
        }

        public DialogConfig<D> setModality(Modality modality) {
            this.modality = modality;
            return this;
        }

        public Window getOwner() {
            return owner;
        }

        public DialogConfig<D> setOwner(Window owner) {
            this.owner = owner;
            return this;
        }

        public Pane getOwnerNode() {
            return ownerNode;
        }

        public DialogConfig<D> setOwnerNode(Pane ownerNode) {
            this.ownerNode = ownerNode;
            return this;
        }

        public boolean isCenterInOwnerNode() {
            return centerInOwnerNode;
        }

        public DialogConfig<D> setCenterInOwnerNode(boolean centerInOwnerNode) {
            this.centerInOwnerNode = centerInOwnerNode;
            return this;
        }

        public boolean isScrimOwner() {
            return scrimOwner;
        }

        public DialogConfig<D> setScrimOwner(boolean scrimOwner) {
            this.scrimOwner = scrimOwner;
            return this;
        }

        public double getScrimStrength() {
            return scrimStrength;
        }

        public DialogConfig<D> setScrimStrength(double scrimStrength) {
            this.scrimStrength = scrimStrength;
            return this;
        }

        public ScrimPriority getScrimPriority() {
            return scrimPriority;
        }

        public DialogConfig<D> setScrimPriority(ScrimPriority scrimPriority) {
            this.scrimPriority = scrimPriority;
            return this;
        }

        public boolean isDraggable() {
            return draggable;
        }

        public DialogConfig<D> setDraggable(boolean draggable) {
            this.draggable = draggable;
            return this;
        }

        public Consumer<Stage> getOnClose() {
            return onClose;
        }

        public DialogConfig<D> setOnClose(Consumer<Stage> onClose) {
            this.onClose = onClose;
            return this;
        }

        public EventHandler<WindowEvent> getOnShown() {
            return onShown;
        }

        public DialogConfig<D> setOnShown(EventHandler<WindowEvent> onShown) {
            this.onShown = onShown;
            return this;
        }
    }
}
