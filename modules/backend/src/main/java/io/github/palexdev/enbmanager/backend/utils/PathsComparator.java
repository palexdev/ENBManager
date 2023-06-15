package io.github.palexdev.enbmanager.backend.utils;

import java.nio.file.Path;
import java.util.Comparator;

public class PathsComparator implements Comparator<Path> {
    //================================================================================
    // Instance
    //================================================================================
    private static final PathsComparator instance = new PathsComparator();

    public static PathsComparator instance() {
        return instance;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public int compare(Path p1, Path p2) {
        return FilesComparator.instance().compare(
            p1.toFile(),
            p2.toFile()
        );
    }
}
