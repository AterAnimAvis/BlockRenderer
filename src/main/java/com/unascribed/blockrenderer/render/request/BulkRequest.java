package com.unascribed.blockrenderer.render.request;

import com.unascribed.blockrenderer.render.ItemStackRenderer;

public class BulkRequest implements IRequest {

    private final int size;
    private final String namespaceSpec;

    public BulkRequest(int size, String namespaceSpec) {
        this.size = size;
        this.namespaceSpec = namespaceSpec;
    }

    public void render() {
        ItemStackRenderer.bulkRender(size, namespaceSpec);
    }

}
