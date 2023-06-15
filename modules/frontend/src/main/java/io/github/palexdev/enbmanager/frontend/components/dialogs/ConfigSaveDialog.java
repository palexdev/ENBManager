package io.github.palexdev.enbmanager.frontend.components.dialogs;

import io.github.palexdev.mfxcomponents.controls.checkbox.MFXCheckBox;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.VBox;

public class ConfigSaveDialog extends FieldDialog {
    //================================================================================
    // Properties
    //================================================================================
    private final BooleanProperty deleteOnSave = new SimpleBooleanProperty(false);

    //================================================================================
    // Constructors
    //================================================================================
    public ConfigSaveDialog() {
        getStyleClass().add("config");

        MFXCheckBox dos = new MFXCheckBox("Delete on save");
        deleteOnSave.bind(dos.selectedProperty());
        VBox box = new VBox(getContent(), dos);
        box.getStyleClass().add("box");
        setContent(box);
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public Result getResult() {
        return Result.of(getValue(), isDeleteOnSave());
    }

    public boolean isDeleteOnSave() {
        return deleteOnSave.get();
    }

    public BooleanProperty deleteOnSaveProperty() {
        return deleteOnSave;
    }

    public void setDeleteOnSave(boolean deleteOnSave) {
        this.deleteOnSave.set(deleteOnSave);
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    public record Result(String name, boolean deleteOnSave) {
        public static Result of(String name, boolean deleteOnSave) {
            return new Result(name, deleteOnSave);
        }
    }
}
