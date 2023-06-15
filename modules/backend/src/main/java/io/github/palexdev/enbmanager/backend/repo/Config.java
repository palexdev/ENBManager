package io.github.palexdev.enbmanager.backend.repo;

import io.github.palexdev.enbmanager.backend.utils.PathsComparator;

import java.nio.file.Path;
import java.util.*;

public record Config(String name, Path path, Set<Path> files) {

    //================================================================================
    // Constructors
    //================================================================================
    public Config(Path path) {
        this(path.getFileName().toString(), path, new TreeSet<>(PathsComparator.instance()));
    }

    public static Config from(Path path) {
        return new Config(path);
    }

    //================================================================================
    // Methods
    //================================================================================
    public Config addFiles(Path... files) {
        Collections.addAll(this.files, files);
        return this;
    }

    public Config addFiles(Collection<? extends Path> files) {
        this.files.addAll(files);
        return this;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return Objects.equals(path, config.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
