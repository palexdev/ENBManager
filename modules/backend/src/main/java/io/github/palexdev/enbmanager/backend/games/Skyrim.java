package io.github.palexdev.enbmanager.backend.games;

import io.github.palexdev.enbmanager.backend.BackendRes;
import io.github.palexdev.enbmanager.backend.settings.SkyrimSettings;
import io.inverno.core.annotation.Bean;

import java.io.InputStream;

@Bean
public class Skyrim extends GameBase<SkyrimSettings> {

    //================================================================================
    // Constructors
    //================================================================================
    public Skyrim(SkyrimSettings settings) {
        super(settings, "Skyrim", "Skyrim.exe");
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public InputStream getIcon() {
        return BackendRes.loadAsset("Skyrim.png");
    }

    @Override
    public SkyrimSettings getSettings() {
        return settings;
    }
}
