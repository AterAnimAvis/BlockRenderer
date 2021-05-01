package com.unascribed.blockrenderer.render;

public interface IRequest {

    boolean render();

    default int priority() {
        return 0;
    }

}
