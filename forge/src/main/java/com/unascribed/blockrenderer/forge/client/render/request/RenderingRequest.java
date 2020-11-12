package com.unascribed.blockrenderer.forge.client.render.request;

import com.unascribed.blockrenderer.forge.client.render.RenderManager;
import com.unascribed.blockrenderer.render.IRenderer;
import com.unascribed.blockrenderer.render.IRequest;
import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;

import java.util.function.Consumer;

public class RenderingRequest<S, T> implements IRequest {

    private final IRenderer<S, T> renderer;
    private final S parameters;
    private final T value;
    private final ImageHandler<T> handler;
    private final Consumer<T> callback;

    public RenderingRequest(IRenderer<S, T> renderer,
                            S parameters,
                            T value,
                            ImageHandler<T> handler,
                            Consumer<T> callback) {
        this.renderer = renderer;
        this.parameters = parameters;
        this.value = value;
        this.handler = handler;
        this.callback = callback;
    }

    /**
     * @return true when rendering has been completed
     */
    @Override
    public boolean render() {
        RenderManager.render(renderer, handler, parameters, value, callback);

        return true;
    }

}
