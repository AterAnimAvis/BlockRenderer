package com.unascribed.blockrenderer.render.request;

import com.unascribed.blockrenderer.render.ItemStackRenderer;

public class BulkRequest implements IRequest {

    private final int size;
    private final String namespaceSpec;
    private final boolean useId;
    private final boolean addSize;

    public BulkRequest(int size, String namespaceSpec, boolean useId, boolean addSize) {
        this.size = size;
        this.namespaceSpec = namespaceSpec;
        this.useId = useId;
        this.addSize = addSize;
    }

    public void render() {
        ItemStackRenderer.bulkRender(size, namespaceSpec, useId, addSize);
    }

}
