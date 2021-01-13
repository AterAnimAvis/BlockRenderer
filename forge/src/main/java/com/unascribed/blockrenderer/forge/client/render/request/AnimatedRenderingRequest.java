package com.unascribed.blockrenderer.forge.client.render.request;

import com.unascribed.blockrenderer.forge.client.render.RenderManager;
import com.unascribed.blockrenderer.render.IAnimatedRenderer;
import com.unascribed.blockrenderer.render.IRequest;

import javax.imageio.stream.ImageOutputStream;
import java.io.File;
import java.util.function.Consumer;
import java.util.function.Function;

public class AnimatedRenderingRequest<S, T> implements IRequest {

    private final IAnimatedRenderer<S, T> renderer;
    private final S parameters;
    private final T value;
    private final Function<T, ImageOutputStream> provider;
    private final Consumer<T> callback;
    private final int length;
    private final boolean loop;
    private final boolean zip;
    private final String zipFile;
    private final Consumer<File> zipFileCallback;

    public AnimatedRenderingRequest(IAnimatedRenderer<S, T> renderer,
                                    S parameters,
                                    T value,
                                    int length,
                                    boolean loop,
                                    Function<T, ImageOutputStream> provider,
                                    Consumer<T> callback,
                                    boolean zip,
                                    String zipFile,
                                    Consumer<File> zipFileCallback) {
        this.renderer = renderer;
        this.parameters = parameters;
        this.value = value;
        this.length = length;
        this.loop = loop;
        this.provider = provider;
        this.callback = callback;
        this.zip = zip;
        this.zipFile = zipFile;
        this.zipFileCallback = zipFileCallback;
    }

    /**
     * @return true when rendering has been completed
     */
    @Override
    public boolean render() {
        RenderManager.animated(renderer, provider, callback, parameters, length, loop, zip, zipFile, zipFileCallback, value);
        return true;
    }

}
