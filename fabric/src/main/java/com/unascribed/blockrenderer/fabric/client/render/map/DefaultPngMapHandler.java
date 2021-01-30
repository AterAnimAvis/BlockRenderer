package com.unascribed.blockrenderer.fabric.client.render.map;

import com.unascribed.blockrenderer.fabric.client.render.item.DefaultPngItemStackHandler;
import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;
import com.unascribed.blockrenderer.varia.rendering.STBWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.MapData;

import java.io.File;
import java.util.function.Consumer;

public class DefaultPngMapHandler implements ImageHandler<MapData>, Consumer<MapData> {

    private final DefaultPngItemStackHandler delegate;
    private final ItemStack stack;

    public DefaultPngMapHandler(ItemStack stack, File folder, int size, boolean useIdentifier, boolean addSize, boolean addDate) {
        this.delegate = new DefaultPngItemStackHandler(folder, size, useIdentifier, addSize, addDate);
        this.stack = stack;
    }

    @Override
    public void accept(MapData value) {
        delegate.accept(stack);
    }

    @Override
    public void accept(MapData value, STBWrapper image) {
        delegate.accept(stack, image);
    }

}
