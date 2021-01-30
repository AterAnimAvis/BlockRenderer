package com.unascribed.blockrenderer.fabric.client.render.item;

import com.unascribed.blockrenderer.fabric.client.varia.Strings;
import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;
import com.unascribed.blockrenderer.varia.Files;
import com.unascribed.blockrenderer.varia.rendering.STBWrapper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

import java.io.File;

public class DefaultPngItemStackHandler extends BaseItemStackHandler implements ImageHandler<ItemStack>, Runnable {

    private final int grouped;

    public DefaultPngItemStackHandler(File folder, int size, boolean useIdentifier, boolean addSize, boolean addDate) {
        this(folder, size, useIdentifier, addSize, addDate, 0);
    }

    public DefaultPngItemStackHandler(File folder, int size, boolean useIdentifier, boolean addSize, boolean addDate, int grouped) {
        super(folder, size, useIdentifier, addSize, addDate);
        this.grouped = grouped;
    }

    @Override
    public void accept(ItemStack value, STBWrapper image) {
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

        Strings.addMessage(Strings.rawText("> Finished Rendering").setStyle(open));
    }

    @Override
    protected String getFilename(ItemStack value) {
        String result = super.getFilename(value);

        switch (grouped) {
            case 1:
                ItemGroup group = value.getItem().getGroup();
                if (group == null) return result;
                return Strings.sanitize(group.getPath()) + "/" + result;
            case 2:
                return (value.getItem() instanceof BlockItem ? "blocks" : "items") + "/" + result;
        }

        return result;
    }

}
