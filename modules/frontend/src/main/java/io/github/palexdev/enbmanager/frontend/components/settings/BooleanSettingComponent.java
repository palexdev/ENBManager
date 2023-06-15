package io.github.palexdev.enbmanager.frontend.components.settings;

import io.github.palexdev.enbmanager.backend.settings.base.BooleanSetting;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcomponents.controls.checkbox.MFXCheckBox;
import javafx.geometry.HPos;
import javafx.geometry.VPos;

public class BooleanSettingComponent extends SettingComponent<Boolean, BooleanSetting> {

    //================================================================================
    // Constructors
    //================================================================================
    public BooleanSettingComponent(BooleanSetting setting) {
        super(setting);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected MFXSkinBase<?, ?> buildSkin() {
        return new Skin(this);
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    protected class Skin extends SkinBase {
        private final MFXCheckBox check;

        protected Skin(SettingComponent<Boolean, BooleanSetting> component) {
            super(component);
            check = new MFXCheckBox(getSetting().description());
            check.setSelected(getSetting().get());
            getChildren().add(check);
        }

        @Override
        protected void initBehavior(SettingComponentBehavior<Boolean, BooleanSetting> behavior) {
            super.initBehavior(behavior);
            behavior.register(check.selectedProperty(), (ob, o, n) -> getSetting().set(n));
        }

        @Override
        protected void settingChanged() {
            super.settingChanged();
            Boolean val = getSetting().get();
            check.setSelected(val);
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            super.layoutChildren(x, y, w, h);
            layoutInArea(
                check,
                x, y, w, h, 0,
                HPos.LEFT, VPos.CENTER
            );
        }
    }
}
