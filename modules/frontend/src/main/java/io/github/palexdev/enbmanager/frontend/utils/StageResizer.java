package io.github.palexdev.enbmanager.frontend.utils;

import io.github.palexdev.mfxcore.utils.resize.RegionDragResizer;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class StageResizer extends RegionDragResizer {

    //================================================================================
    // Constructors
    //================================================================================
    public StageResizer(Region node, Stage stage) {
        super(node);
        setResizeHandler((n, x, y, w, h) -> resizeStage(stage, w, h));
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void resizeStage(Stage stage, double w, double h) {
        stage.setWidth(w);
        stage.setHeight(h);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void handlePressed(MouseEvent event) {
        node.requestFocus();
        clickedX = event.getSceneX();
        clickedY = event.getSceneY();
        nodeX = nodeX();
        nodeY = nodeY();
        nodeW = nodeW();
        nodeH = nodeH();
        draggedZone = getZoneByEvent(event);
    }

    @Override
    protected void handleDragged(MouseEvent event) {
        if (node.getCursor() == Cursor.MOVE) return;
        super.handleDragged(event);
    }
}
