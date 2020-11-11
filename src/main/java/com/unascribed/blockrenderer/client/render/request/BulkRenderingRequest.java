package com.unascribed.blockrenderer.client.render.request;

import com.unascribed.blockrenderer.client.render.IRenderer;
import com.unascribed.blockrenderer.client.render.IRequest;
import com.unascribed.blockrenderer.client.render.RenderManager;
import com.unascribed.blockrenderer.client.render.request.lambda.ImageHandler;

import java.util.Collection;

public class BulkRenderingRequest<S, T> implements IRequest {

    private final IRenderer<S, T> renderer;
    private final S parameters;
    private final Collection<T> values;
    private final ImageHandler<T> handler;
    private final Runnable callback;

    public BulkRenderingRequest(IRenderer<S, T> renderer,
                                S parameters,
                                Collection<T> values,
                                ImageHandler<T> handler,
                                Runnable callback) {
        this.renderer = renderer;
        this.parameters = parameters;
        this.values = values;
        this.handler = handler;
        this.callback = callback;
    }

    /**
     * @return true when rendering has been completed
     */
    @Override
    public boolean render() {
        RenderManager.bulk(renderer, handler, parameters, values);
        callback.run();

        return true;
    }

}