package com.unascribed.blockrenderer.fabric.client.render.item;

import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;
import com.unascribed.blockrenderer.varia.Files;
import net.minecraft.world.item.ItemStack;

import java.awt.image.BufferedImage;
import java.io.File;

public class DefaultPngItemStackHandler extends BaseItemStackHandler implements ImageHandler<ItemStack> {

    public DefaultPngItemStackHandler(File folder, int size, boolean useIdentifier, boolean addSize, boolean addDate) {
        super(folder, size, useIdentifier, addSize, addDate);
    }

    @Override
    public void accept(ItemStack value, BufferedImage image) {
        Files.IOSupplier<File> provider = () -> {
            File file = Files.getPng(folder, getFilename(value));
            return Files.savePng(file, image);
        };

        future = Files.wrap("Exception whilst saving image", provider);
    }

}
