import io.inverno.core.annotation.Module;

@Module(sourcePackage = "io.github.palexdev.enbmanager.backend")
module ENBManager.backend {
    //***** Inverno *****//
    requires io.inverno.core.annotation;
    requires io.inverno.core;

    //***** Misc *****//
    requires java.prefs;
    requires org.slf4j;

    //***** Exports *****//
    // Base
    exports io.github.palexdev.enbmanager.backend;

    // Events
    exports io.github.palexdev.enbmanager.backend.events;
    exports io.github.palexdev.enbmanager.backend.events.bus;

    // Functional
    exports io.github.palexdev.enbmanager.backend.fp;

    // Games
    exports io.github.palexdev.enbmanager.backend.games;

    // Repo
    exports io.github.palexdev.enbmanager.backend.repo;

    // Settings
    exports io.github.palexdev.enbmanager.backend.settings;
    exports io.github.palexdev.enbmanager.backend.settings.base;

    // Utils
    exports io.github.palexdev.enbmanager.backend.utils;
}