package io.github.palexdev.enbmanager.backend;

import java.io.InputStream;
import java.net.URL;

public class BackendRes {

    //================================================================================
    // Constructors
    //================================================================================
    private BackendRes() {}

    //================================================================================
    // Static Methods
    //================================================================================
    public static URL get(String name) {
        return BackendRes.class.getResource(name);
    }

    public static String load(String name) {
        return get(name).toExternalForm();
    }

    public static InputStream loadAsset(String name) {
        return BackendRes.class.getResourceAsStream("assets/" + name);
    }
}
