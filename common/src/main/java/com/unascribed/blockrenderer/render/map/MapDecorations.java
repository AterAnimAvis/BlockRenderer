package com.unascribed.blockrenderer.render.map;

import java.util.Locale;

public enum MapDecorations {
    DEFAULT, ALL, NONE;

    public String lowercaseName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static MapDecorations byId(int id) {
        return values()[id % values().length];
    }
}