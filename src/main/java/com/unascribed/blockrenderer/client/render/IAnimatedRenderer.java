package com.unascribed.blockrenderer.client.render;

import com.unascribed.blockrenderer.client.render.request.lambda.ImageHandler;

public interface IAnimatedRenderer<S, T> extends IRenderer<S, T> {

    default void render(T instance, ImageHandler<T> consumer) {
        render(instance, consumer, 0L);
    }

    void render(T instance, ImageHandler<T> consumer, long nano);

}
