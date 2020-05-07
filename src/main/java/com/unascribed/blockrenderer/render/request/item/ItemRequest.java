package com.unascribed.blockrenderer.render.request.item;

import com.unascribed.blockrenderer.render.SingleRenderer;
import com.unascribed.blockrenderer.render.impl.ItemStackRenderer;
import com.unascribed.blockrenderer.render.request.IRequest;
import net.minecraft.item.ItemStack;

public class ItemRequest implements IRequest {

    private final int size;
    private final ItemStack stack;
    private final boolean useId;
    private final boolean addSize;

    public ItemRequest(int size, ItemStack stack, boolean useId, boolean addSize) {
        this.size = size;
        this.stack = stack;
        this.useId = useId;
        this.addSize = addSize;
    }

    public void render() {
        SingleRenderer.render(new ItemStackRenderer(), stack, size, useId, addSize);
    }

}
