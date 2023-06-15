import io.inverno.core.annotation.Module;

@Module(sourcePackage = "io.github.palexdev.enbmanager.frontend")
module ENBManager.frontend {
    //***** Modules *****//
    requires ENBManager.backend;

    //***** UI *****//
    requires javafx.controls;

    requires MaterialFX;
    requires mfx.components;
    requires com.sandec.mdfx;
    requires fr.brouillard.oss.cssfx;
    requires org.scenicview.scenicview;

    //***** Inverno *****//
    requires io.inverno.core;
    requires io.inverno.core.annotation;

    //***** Misc *****//
    requires directory.watcher;
    requires java.prefs;
    requires org.slf4j;

    //***** Exports *****//
    // Base
    exports io.github.palexdev.enbmanager.frontend;

    // Components
    exports io.github.palexdev.enbmanager.frontend.components;
    exports io.github.palexdev.enbmanager.frontend.components.dialogs;
    exports io.github.palexdev.enbmanager.frontend.components.misc;
    exports io.github.palexdev.enbmanager.frontend.components.settings;

    // Events
    exports io.github.palexdev.enbmanager.frontend.events;

    // Model
    exports io.github.palexdev.enbmanager.frontend.model;

    // Settings
    exports io.github.palexdev.enbmanager.frontend.settings;

    // Theming
    exports io.github.palexdev.enbmanager.frontend.theming;

    // Utils
    exports io.github.palexdev.enbmanager.frontend.utils;

    // Views
    exports io.github.palexdev.enbmanager.frontend.views;
    exports io.github.palexdev.enbmanager.frontend.views.base;

}