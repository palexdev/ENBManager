package io.github.palexdev.enbmanager.backend.games;

import io.github.palexdev.enbmanager.backend.settings.base.GameSettings;

import java.util.Objects;

public abstract class GameBase<S extends GameSettings> implements Game {
    //================================================================================
    // Properties
    //================================================================================
    protected final String name;
    protected final String exeName;
    protected final S settings;

    //================================================================================
    // Constructors
    //================================================================================
    public GameBase(S settings, String name, String exeName) {
        this.name = name;
        this.exeName = exeName;
        this.settings = settings;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getExeName() {
        return exeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameBase<?> other = (GameBase<?>) o;
        return Objects.equals(name, other.name) && Objects.equals(exeName, other.exeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, exeName);
    }
}
