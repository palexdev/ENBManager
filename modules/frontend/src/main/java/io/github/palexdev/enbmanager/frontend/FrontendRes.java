package io.github.palexdev.enbmanager.frontend;

import java.net.URL;

public class FrontendRes {

    //================================================================================
    // Constructors
    //================================================================================
    private FrontendRes() {}

    //================================================================================
    // Static Methods
    //================================================================================
    public static URL get(String name) {
        return FrontendRes.class.getResource(name);
    }

    public static String load(String name) {
        return get(name).toExternalForm();
    }

    public static String loadCss(String name) {
        return load("css/" + name);
    }
}
