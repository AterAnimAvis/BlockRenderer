package com.unascribed.blockrenderer.proxy;

import com.unascribed.blockrenderer.render.request.IRequest;

public abstract class CommonProxy {

    public abstract void init();

    public void render(IRequest request) {
        // NO-OP
    }

}
