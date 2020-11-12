package com.unascribed.blockrenderer.fabric.client.render.item;

import com.unascribed.blockrenderer.fabric.client.varia.StringUtils;
import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;
import com.unascribed.blockrenderer.varia.Files;
import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

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
                .withFormatting(Formatting.GOLD)
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, folder.getAbsolutePath()));

        StringUtils.addMessage(new LiteralText("> Finished Rendering").setStyle(open));
    }

}
