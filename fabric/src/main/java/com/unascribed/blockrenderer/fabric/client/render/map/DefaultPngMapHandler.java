package com.unascribed.blockrenderer.fabric.client.render.map;

import com.unascribed.blockrenderer.fabric.client.render.item.DefaultPngItemStackHandler;
import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Consumer;

public class DefaultPngMapHandler implements ImageHandler<MapState>, Consumer<MapState> {

    private final DefaultPngItemStackHandler delegate;
    private final ItemStack stack;

    public DefaultPngMapHandler(ItemStack stack, File folder, int size, boolean useIdentifier, boolean addSize, boolean addDate) {
        this.delegate = new DefaultPngItemStackHandler(folder, size, useIdentifier, addSize, addDate);
        this.stack = stack;
    }

    @Override
    public void accept(MapState value) {
        delegate.accept(stack);
    }

    @Override
    public void accept(MapState value, BufferedImage image) {
        delegate.accept(stack, image);
    }

}
