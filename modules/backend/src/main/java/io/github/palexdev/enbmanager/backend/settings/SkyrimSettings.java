package io.github.palexdev.enbmanager.backend.settings;

import io.github.palexdev.enbmanager.backend.settings.base.GameSettings;
import io.inverno.core.annotation.Bean;

@Bean
public class SkyrimSettings extends GameSettings {

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected String node() {
        return root() + "/skyrim";
    }
}
