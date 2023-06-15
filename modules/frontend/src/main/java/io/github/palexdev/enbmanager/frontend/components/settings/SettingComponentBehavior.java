package io.github.palexdev.enbmanager.frontend.components.settings;

import io.github.palexdev.enbmanager.backend.settings.base.Setting;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class SettingComponentBehavior<T, S extends Setting<T>> extends BehaviorBase<SettingComponent<T, S>> {

    //================================================================================
    // Constructors
    //================================================================================
    public SettingComponentBehavior(SettingComponent<T, S> component) {
        super(component);
    }

    //================================================================================
    // Methods
    //================================================================================
    public void reset(MouseEvent me) {
        // Null events can come from the component's delegate method
        if (me == null || me.getButton() == MouseButton.PRIMARY) {
            SettingComponent<T, S> component = getNode();
            component.setting.set(component.getInitialValue());
        }
    }
}
