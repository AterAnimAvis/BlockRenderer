package com.unascribed.blockrenderer.render.request.item;

import com.unascribed.blockrenderer.render.ItemStackRenderer;
import com.unascribed.blockrenderer.render.request.IRequest;

public class BulkItemRequest implements IRequest {

    private final int size;
    private final String namespaceSpec;
    private final boolean useId;
    private final boolean addSize;

    public BulkItemRequest(int size, String namespaceSpec, boolean useId, boolean addSize) {
        this.size = size;
        this.namespaceSpec = namespaceSpec;
        this.useId = useId;
        this.addSize = addSize;
    }

    public void render() {
        ItemStackRenderer.bulkRender(size, namespaceSpec, useId, addSize);
    }

}
