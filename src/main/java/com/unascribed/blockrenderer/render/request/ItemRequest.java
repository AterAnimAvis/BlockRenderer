package com.unascribed.blockrenderer.render.request;

import com.unascribed.blockrenderer.render.ItemStackRenderer;
import net.minecraft.item.ItemStack;

public class ItemRequest implements IRequest {

    private final int size;
    private final ItemStack stack;

    public ItemRequest(int size, ItemStack stack) {
        this.size = size;
        this.stack = stack;
    }

    public void render() {
        ItemStackRenderer.renderItem(size, stack);
    }

}
