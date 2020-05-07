package com.unascribed.blockrenderer.render.request.item;

import com.google.common.base.Joiner;
import com.unascribed.blockrenderer.render.BulkRenderer;
import com.unascribed.blockrenderer.render.impl.ItemStackRenderer;
import com.unascribed.blockrenderer.render.request.IRequest;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Set;

import static com.unascribed.blockrenderer.utils.MiscUtils.collectStacks;
import static com.unascribed.blockrenderer.utils.StringUtils.getNamespaces;

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
        Set<String> namespaces = getNamespaces(namespaceSpec);
        List<ItemStack> renders = collectStacks(namespaces);
        String joined = Joiner.on(", ").join(namespaces);

        BulkRenderer.bulkRender(new ItemStackRenderer(), joined, renders, size, useId, addSize);
    }

}
