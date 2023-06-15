package io.github.palexdev.enbmanager.frontend.components.settings;

import io.github.palexdev.enbmanager.backend.settings.base.Setting;
import io.github.palexdev.enbmanager.frontend.components.FloatingField;
import io.github.palexdev.enbmanager.frontend.components.NumberField;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcore.utils.converters.FunctionalStringConverter;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.util.StringConverter;

import java.util.function.Supplier;

public class FieldSettingComponent<T> extends SettingComponent<T, Setting<T>> {
    private final Supplier<FloatingField> fieldFactory;
    private StringConverter<T> converter;

    //================================================================================
    // Constructors
    //================================================================================
    protected FieldSettingComponent(Setting<T> setting, Supplier<FloatingField> fieldFactory) {
        super(setting);
        this.fieldFactory = fieldFactory;
    }

    public static FieldSettingComponent<String> string(Setting<String> setting) {
        FieldSettingComponent<String> component = new FieldSettingComponent<>(setting, FloatingField::new);
        component.setConverter(FunctionalStringConverter.converter(
            s -> s,
            s -> s
        ));
        return component;
    }

    public static FieldSettingComponent<Double> forDouble(Setting<Double> setting) {
        FieldSettingComponent<Double> component = number(setting);
        component.setConverter(FunctionalStringConverter.converter(
            s -> {
                try {return Double.parseDouble(s);} catch (Exception ex) {return setting.defValue();}
            },
            String::valueOf
        ));
        return component;
    }

    static <N extends Number> FieldSettingComponent<N> number(Setting<N> descriptor) {
        return new FieldSettingComponent<>(descriptor, NumberField::new);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected MFXSkinBase<?, ?> buildSkin() {
        return null;
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public Supplier<FloatingField> getFieldFactory() {
        return fieldFactory;
    }

    public StringConverter<T> getConverter() {
        return converter;
    }

    public void setConverter(StringConverter<T> converter) {
        this.converter = converter;
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    protected class Skin extends SkinBase {
        protected final FloatingField field;

        protected Skin(FieldSettingComponent<T> component) {
            super(component);

            field = component.getFieldFactory().get();
            field.setFloatingText(getSetting().description());
            setText(getSetting().get());

            getChildren().add(field);
        }

        @Override
        protected void initBehavior(SettingComponentBehavior<T, Setting<T>> behavior) {
            super.initBehavior(behavior);
            register(field.focusWithinProperty(), (ob, o, n) -> {
                if (!n) {
                    String text = field.field().getText();
                    boolean avoidEmpty = getSetting().isAvoidEmpty();
                    if (text.isBlank() && avoidEmpty) {
                        getSetting().reset();
                        return;
                    }
                    setSetting(text);
                }
            });
        }

        protected void setText(T val) {
            field.field().setText(getConverter().toString(val));
        }

        protected void setSetting(String val) {
            getSetting().set(getConverter().fromString(val));
        }

        @Override
        protected void settingChanged() {
            super.settingChanged();
            setText(getSetting().get());
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            super.layoutChildren(x, y, w, h);
            layoutInArea(
                field,
                x, y, w, h, 0,
                HPos.LEFT, VPos.CENTER
            );
        }
    }
}
