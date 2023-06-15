package io.github.palexdev.enbmanager.backend.games;

import io.github.palexdev.enbmanager.backend.BackendRes;
import io.github.palexdev.enbmanager.backend.settings.SkyrimSESettings;
import io.inverno.core.annotation.Bean;

import java.io.InputStream;

@Bean
public class SkyrimSE extends GameBase<SkyrimSESettings> {

    //================================================================================
    // Constructors
    //================================================================================
    public SkyrimSE(SkyrimSESettings settings) {
        super(settings, "Skyrim Special Edition", "SkyrimSE.exe");
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public InputStream getIcon() {
        return BackendRes.loadAsset("SkyrimSE.png");
    }

    @Override
    public SkyrimSESettings getSettings() {
        return settings;
    }
}
