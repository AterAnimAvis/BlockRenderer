package com.unascribed.blockrenderer.forge.client.render.item;

import com.unascribed.blockrenderer.forge.client.varia.StringUtils;
import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;
import com.unascribed.blockrenderer.varia.Files;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

import java.awt.image.BufferedImage;
import java.io.File;

public class DefaultPngItemStackHandler extends BaseItemStackHandler implements ImageHandler<ItemStack>, Runnable {

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

    @Override
    public void run() {
        Style open = Style.EMPTY
                .applyFormatting(TextFormatting.GOLD)
                .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, folder.getAbsolutePath()));

        StringUtils.addMessage(new StringTextComponent("> Finished Rendering").setStyle(open));
    }

}
