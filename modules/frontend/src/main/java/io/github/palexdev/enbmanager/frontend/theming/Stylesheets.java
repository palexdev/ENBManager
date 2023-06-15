package io.github.palexdev.enbmanager.frontend.theming;

import io.github.palexdev.enbmanager.frontend.FrontendRes;
import io.github.palexdev.mfxcomponents.theming.base.Theme;

import java.net.URL;

public enum Stylesheets implements Theme {
    APP_THEME("css/AppTheme.css"),
    ;

    private final String path;

    Stylesheets(String path) {
        this.path = path;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public URL get() {
        if (Helper.isCached(this) && Helper.getCachedTheme(this) != null)
            return Helper.getCachedTheme(this);
        return Helper.cacheTheme(this, FrontendRes.get(path()));
    }
}
