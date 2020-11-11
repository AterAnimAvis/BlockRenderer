package com.unascribed.blockrenderer.client.render;

import com.unascribed.blockrenderer.client.render.request.lambda.ImageHandler;

public interface IRenderer<S, T> {

    void setup(S parameters);

    void render(T instance, ImageHandler<T> consumer);

    void teardown();

}