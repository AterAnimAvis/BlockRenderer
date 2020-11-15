package com.unascribed.blockrenderer.render;

import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;

import javax.imageio.stream.ImageOutputStream;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IRenderManager {

    <S, T> void render(IRenderer<S, T> renderer, ImageHandler<T> handler, S params, T value, Consumer<T> callback);

    <S, T> void bulk(IRenderer<S, T> renderer, ImageHandler<T> handler, S params, String name, Collection<T> values);

    <S, T> void animated(IAnimatedRenderer<S, T> renderer, Function<T, ImageOutputStream> provider, Consumer<T> callback, S params, int length, boolean loop, T value);

    class Dummy implements IRenderManager {

        @Override
        public <S, T> void render(IRenderer<S, T> renderer, ImageHandler<T> handler, S params, T value, Consumer<T> callback) {

        }

        @Override
        public <S, T> void bulk(IRenderer<S, T> renderer, ImageHandler<T> handler, S params, String name, Collection<T> values) {

        }

        @Override
        public <S, T> void animated(IAnimatedRenderer<S, T> renderer, Function<T, ImageOutputStream> provider, Consumer<T> callback, S params, int length, boolean loop, T value) {

        }

    }

}