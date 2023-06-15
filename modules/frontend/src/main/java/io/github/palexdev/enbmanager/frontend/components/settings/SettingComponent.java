package io.github.palexdev.enbmanager.frontend.components.settings;

import io.github.palexdev.enbmanager.backend.settings.base.Setting;
import io.github.palexdev.mfxcomponents.controls.base.MFXControl;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.prefs.PreferenceChangeListener;

public abstract class SettingComponent<T, S extends Setting<T>> extends MFXControl<SettingComponentBehavior<T, S>> {
    //================================================================================
    // Properties
    //================================================================================
    protected final S setting;
    protected final ReadOnlyObjectWrapper<T> initialValue = new ReadOnlyObjectWrapper<>();
    protected final ReadOnlyBooleanWrapper changed = new ReadOnlyBooleanWrapper();

    //================================================================================
    // Constructors
    //================================================================================
    public SettingComponent(S setting) {
        this.setting = setting;
        setInitialValue(setting.get());
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void initialize() {
        getStyleClass().setAll(defaultStyleClasses());
        setDefaultBehaviorProvider();
    }

    public void reset() {
        getBehavior().reset(null);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public List<String> defaultStyleClasses() {
        return List.of("setting-component");
    }

    @Override
    public Supplier<SettingComponentBehavior<T, S>> defaultBehaviorProvider() {
        return () -> new SettingComponentBehavior<>(this);
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    protected S getSetting() {
        return setting;
    }

    public T getInitialValue() {
        return initialValue.get();
    }

    public ReadOnlyObjectProperty<T> initialValueProperty() {
        return initialValue.getReadOnlyProperty();
    }

    protected void setInitialValue(T initialValue) {
        this.initialValue.set(initialValue);
    }

    public boolean isChanged() {
        return changed.get();
    }

    public ReadOnlyBooleanProperty changedProperty() {
        return changed.getReadOnlyProperty();
    }

    protected void setChanged(boolean changed) {
        this.changed.set(changed);
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    protected abstract class SkinBase extends MFXSkinBase<SettingComponent<T, S>, SettingComponentBehavior<T, S>> {
        protected MFXIconButton resetIcon;
        protected PreferenceChangeListener onSettingChanged;

        protected SkinBase(SettingComponent<T, S> component) {
            super(component);

            onSettingChanged = e -> {
                String evtKey = e.getKey();
                String descKey = getSetting().name();
                if (!Objects.equals(evtKey, descKey)) return;
                settingChanged();
            };
            getSetting().container().onChange(onSettingChanged);

            resetIcon = buildResetIcon();
            getChildren().add(resetIcon);
        }

        @Override
        protected void initBehavior(SettingComponentBehavior<T, S> behavior) {
            behavior.handler(resetIcon, MouseEvent.MOUSE_CLICKED, behavior::reset);
        }

        protected void settingChanged() {
            setChanged(!Objects.equals(getInitialValue(), getSetting().get()));
        }

        protected MFXIconButton buildResetIcon() {
            MFXIconButton icon = new MFXIconButton().filled();
            icon.getStyleClass().add("reset-icon");
            icon.visibleProperty().bind(changedProperty());
            return icon;
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            layoutInArea(
                resetIcon,
                x, y, w, h, 0,
                HPos.RIGHT, VPos.CENTER
            );
        }

        @Override
        public void dispose() {
            getSetting().container().removeOnChange(onSettingChanged);
            onSettingChanged = null;
            super.dispose();
        }
    }
}
