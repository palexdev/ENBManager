package io.github.palexdev.enbmanager.frontend.components.dialogs;

import io.github.palexdev.enbmanager.frontend.components.FloatingField;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.MouseEvent;

public class FieldDialog extends DialogBase {
    //================================================================================
    // Properties
    //================================================================================
    private final StringProperty value = new SimpleStringProperty("");

    //================================================================================
    // Constructors
    //================================================================================
    public FieldDialog() {
        getStyleClass().add("field-dialog");
        build();
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void build() {
        FloatingField ff = new FloatingField();
        ff.floatingTextProperty().bind(contentTextProperty());
        ff.field().textProperty().bindBidirectional(value);
        ff.setMaxWidth(Double.MAX_VALUE);
        setContent(ff);

        MFXButton confirm = new MFXButton("OK").filled();
        confirm.setOnAction(e -> getScene().getWindow().hide());
        addActions(confirm);

        closeIcon.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> value.set(""));
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public String getValue() {
        return value.get();
    }

    public StringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }
}
