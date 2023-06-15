package io.github.palexdev.enbmanager.frontend.utils;

import io.github.palexdev.enbmanager.backend.games.Game;
import io.github.palexdev.mfxcomponents.controls.MaterialSurface;
import io.github.palexdev.mfxcomponents.window.MFXPlainContent;
import io.github.palexdev.mfxcomponents.window.popups.MFXTooltip;
import io.github.palexdev.mfxcore.utils.fx.RegionUtils;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class UIUtils {

    //================================================================================
    // Constructors
    //================================================================================
    private UIUtils() {}

    //================================================================================
    // Static Methods
    //================================================================================
    // TODO docs
    public static ImgWrapper createIconView() {
        return createIconView(null);
    }

    public static ImgWrapper createIconView(Region surfaceOwner) {
        ImageView iv = new ImageView();
        iv.setFitWidth(48.0);
        iv.setFitHeight(48.0);
        StackPane container = new StackPane();
        if (surfaceOwner == null) surfaceOwner = container;
        MaterialSurface surface = new MaterialSurface(surfaceOwner);
        surface.setManaged(true);
        surface.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        container.getChildren().addAll(iv, surface);
        container.getStyleClass().add("img-wrapper");
        RegionUtils.makeRegionCircular(container);
        return new ImgWrapper(container, iv, surface);
    }

    public static MFXTooltip installTooltip(Node owner, String text) {
        MFXTooltip tooltip = new MFXTooltip(owner); // TODO try using close instead of overriding hide()
        tooltip.setContent(new MFXPlainContent(text));
        tooltip.setInDelay(M3Motion.SHORT2);
        tooltip.setOutDelay(Duration.ZERO);
        return tooltip.install();
    }

    public static FileChooser.ExtensionFilter toFilter(Game game) {
        return new FileChooser.ExtensionFilter(game.getExeName(), game.getExeName());
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    public record ImgWrapper(StackPane container, ImageView view, MaterialSurface surface) {}
}
