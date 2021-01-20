package com.unascribed.blockrenderer.render.manager;

import com.unascribed.blockrenderer.render.IAnimatedRenderer;
import com.unascribed.blockrenderer.render.IRenderer;
import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;

import javax.imageio.stream.ImageOutputStream;
import java.io.File;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IRenderManager {

    <S, T> void render(IRenderer<S, T> renderer, ImageHandler<T> handler, S params, T value, Consumer<T> callback);

    <S, T> void bulk(IRenderer<S, T> renderer, ImageHandler<T> handler, String name, S params, Collection<T> values);

    <S, T> void animated(IAnimatedRenderer<S, T> renderer, Function<T, ImageOutputStream> provider, Consumer<T> callback, S params, int length, boolean loop, boolean zip, String zipFile, Consumer<File> zipFileCallback, T value);

}