package io.github.palexdev.enbmanager.backend.utils;

import java.io.File;
import java.util.Comparator;

public class FilesComparator implements Comparator<File> {
    //================================================================================
    // Instance
    //================================================================================
    private static final FilesComparator instance = new FilesComparator();

    public static FilesComparator instance() {
        return instance;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public int compare(File f1, File f2) {
        if (f1.isDirectory() && !f2.isDirectory()) return -1;
        if (!f1.isDirectory() && f2.isDirectory()) return 1;
        return f1.getName().compareTo(f2.getName());
    }
}
