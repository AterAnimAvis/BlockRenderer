package com.unascribed.blockrenderer.fabric.client.render.item;

import com.unascribed.blockrenderer.varia.Files;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.io.File;
import java.util.function.Function;

public class DefaultGifItemStackHandler extends BaseItemStackHandler implements Function<ItemStack, ImageOutputStream> {

    public DefaultGifItemStackHandler(File folder, int size, boolean useIdentifier, boolean addSize, boolean addDate) {
        super(folder, size, useIdentifier, addSize, addDate);
    }

    @Override
    @Nullable
    public ImageOutputStream apply(ItemStack value) {
        future = Files.wrap("Exception whilst generating gif", () -> Files.getGif(folder, getFilename(value)));
        return Files.wrap("Exception whilst generating gif", () -> new FileImageOutputStream(future));
    }

}
