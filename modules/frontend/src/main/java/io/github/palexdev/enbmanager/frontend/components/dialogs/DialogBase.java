package io.github.palexdev.enbmanager.frontend.components.dialogs;

import io.github.palexdev.enbmanager.frontend.ENBManager;
import io.github.palexdev.enbmanager.frontend.utils.UIUtils;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.mfxcomponents.controls.base.MFXStyleable;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeSolid;
import javafx.css.PseudoClass;

import java.util.List;
import java.util.Map;

/**
 * Base class for all app's dialogs. Makes use of {@link MFXGenericDialog} as a base implementation,
 * but adds some tweaks for the app, and to integrate with MFXComponents.
 */
public class DialogBase extends MFXGenericDialog implements MFXStyleable {
    public enum DialogType {
        INFO, WARN, ERROR
    }

    //================================================================================
    // Properties
    //================================================================================
    protected static final PseudoClass ENABLED = PseudoClass.getPseudoClass("enabled");

    //================================================================================
    // Constructors
    //================================================================================
    public DialogBase() {
        this("", "");
    }

    public DialogBase(String headerText, String contentText) {
        super(headerText, contentText);
        initialize();
    }

    public static DialogBase fatal(String action) {
        DialogBase dialog = error();
        dialog.addActions(Map.entry(
            new MFXButton(action).text(),
            e -> ENBManager.forceShutdown(-1)
        ));
        dialog.setOnClose(e -> ENBManager.forceShutdown(-1));
        return dialog;
    }

    public static DialogBase error() {
        DialogBase dialog = new DialogBase();
        dialog.setHeaderIcon(new MFXFontIcon(FontAwesomeSolid.CIRCLE_EXCLAMATION));
        dialog.getStyleClass().add("error");
        return dialog;
    }

    public static DialogBase warn() {
        DialogBase dialog = new DialogBase();
        dialog.setHeaderIcon(new MFXFontIcon(FontAwesomeSolid.TRIANGLE_EXCLAMATION));
        dialog.getStyleClass().add("warn");
        return dialog;
    }

    public static DialogBase info() {
        DialogBase dialog = new DialogBase();
        dialog.setHeaderIcon(new MFXFontIcon(FontAwesomeSolid.CIRCLE_INFO));
        dialog.getStyleClass().add("info");
        return dialog;
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().setAll(defaultStyleClasses());
        getStylesheets().clear();

        header.getStyleClass().add("window-header");

        closeIcon.setId(null);
        closeIcon.getStyleClass().add("close");
        UIUtils.installTooltip(closeIcon, "Close");

        minimizeIcon.setId(null);
        minimizeIcon.getStyleClass().add("minimize");
        UIUtils.installTooltip(minimizeIcon, "Minimize");

        alwaysOnTopIcon.setId(null);
        alwaysOnTopIcon.getStyleClass().add("aot");
        UIUtils.installTooltip(alwaysOnTopIcon, "Always on top");

        alwaysOnTopProperty().addListener((ob, o, n) -> alwaysOnTopIcon.pseudoClassStateChanged(ENABLED, n));
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public List<String> defaultStyleClasses() {
        return List.of("dialog-base");
    }
}
