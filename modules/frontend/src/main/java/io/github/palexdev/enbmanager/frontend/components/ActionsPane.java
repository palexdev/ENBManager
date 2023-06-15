package io.github.palexdev.enbmanager.frontend.components;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Supplier;

public class ActionsPane extends VBox {
    //================================================================================
    // Properties
    //================================================================================
    private final HBox actionsBox;

    //================================================================================
    // Constructors
    //================================================================================
    public ActionsPane(Node target) {
        actionsBox = new HBox();
        actionsBox.getStyleClass().add("actions");
        getChildren().addAll(target, actionsBox);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add("actions-pane");
    }

    public void addActions(Node... actions) {
        actionsBox.getChildren().addAll(actions);
    }

    public void addAction(Node action) {
        actionsBox.getChildren().add(action);
    }

    public void addAction(Supplier<Node> actionSupplier) {
        actionsBox.getChildren().add(actionSupplier.get());
    }

    public List<Node> getActions() {
        return actionsBox.getChildren();
    }
}
