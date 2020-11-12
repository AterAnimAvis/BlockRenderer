package com.unascribed.blockrenderer.render;

import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;

public interface IAnimatedRenderer<S, T> extends IRenderer<S, T> {

    @Override
    default void render(T instance, ImageHandler<T> consumer) {
        render(instance, consumer, 0L);
    }

    void render(T instance, ImageHandler<T> consumer, long nano);

}
