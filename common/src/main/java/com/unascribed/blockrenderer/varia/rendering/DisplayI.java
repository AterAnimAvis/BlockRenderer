package com.unascribed.blockrenderer.varia.rendering;

public interface DisplayI<Component> {

    void drawRect(int x1, int y1, int x2, int y2, int color);

    void drawCenteredString(Component component, int x, int y, int color);

    void drawDirtBackground(int scaledWidth, int scaledHeight);

}
