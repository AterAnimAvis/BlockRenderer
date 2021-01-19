package com.unascribed.blockrenderer.render.request;

import com.unascribed.blockrenderer.Interop;
import com.unascribed.blockrenderer.render.IRenderer;
import com.unascribed.blockrenderer.render.IRequest;
import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;

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
        Interop.RENDER_MANAGER.bulk(renderer, handler, parameters, values);
        callback.run();

        return true;
    }

}
