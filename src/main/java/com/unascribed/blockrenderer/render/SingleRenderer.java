package com.unascribed.blockrenderer.render;

import static com.unascribed.blockrenderer.render.IRenderer.DEFAULT_FOLDER;
import static com.unascribed.blockrenderer.utils.StringUtils.addMessage;
import static com.unascribed.blockrenderer.utils.StringUtils.dateTime;

public class SingleRenderer {

    public static <T> void render(IRenderer<T> renderer, T value, int size, boolean useId, boolean addSize) {
        String sizeString = addSize ? size + "x" + size + "_" : "";
        String fileName = renderer.getFilename(value, useId);

        renderer.setup(size);
        renderer.render(value);
        addMessage(renderer.save(value, DEFAULT_FOLDER, dateTime() + "_" + sizeString + fileName));
        renderer.teardown();
    }

}
