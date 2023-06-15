package io.github.palexdev.enbmanager.frontend.components;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;

import java.util.function.UnaryOperator;

public class NumberField extends FloatingField {

    //================================================================================
    // Constructors
    //================================================================================
    public NumberField() {
        this("");
    }

    public NumberField(String text) {
        this(text, "");
    }

    public NumberField(String text, String floatingText) {
        super(text, floatingText);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        TextField field = field();
        UnaryOperator<Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?([1-9][0-9]*)?")) {
                return change;
            } else if ("-".equals(change.getText())) {
                if (change.getControlText().startsWith("-")) {
                    change.setText("");
                    change.setRange(0, 1);
                    change.setCaretPosition(change.getCaretPosition() - 2);
                    change.setAnchor(change.getAnchor() - 2);
                } else {
                    change.setRange(0, 0);
                }
                return change;
            }
            return null;
        };
        field.setTextFormatter(new TextFormatter<>(filter));
    }
}
