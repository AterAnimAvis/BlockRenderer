package com.unascribed.blockrenderer.render.request;

import com.unascribed.blockrenderer.render.ItemStackRenderer;
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
        ItemStackRenderer.renderItem(size, stack, useId, addSize);
    }

}
