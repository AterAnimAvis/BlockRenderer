package com.unascribed.blockrenderer.forge.client.render.item;

import com.unascribed.blockrenderer.forge.client.varia.Identifiers;
import com.unascribed.blockrenderer.forge.client.varia.Strings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.function.Consumer;

public class BaseItemStackHandler implements Consumer<ItemStack> {

    @Nullable
    protected File future = null;

    protected final File folder;
    protected final int size;
    protected final boolean useIdentifier;
    protected final boolean addSize;
    protected final boolean addDate;

    public BaseItemStackHandler(File folder, int size, boolean useIdentifier, boolean addSize, boolean addDate) {
        this.folder = folder;
        this.size = size;
        this.useIdentifier = useIdentifier;
        this.addSize = addSize;
        this.addDate = addDate;
    }

    @Override
    public void accept(ItemStack value) {
        Style open = Style.EMPTY.applyFormat(TextFormatting.GOLD);

        if (future != null)
            open = open.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, future.getAbsolutePath()));

        Strings.addMessage(Strings.rawText("> Finished Rendering " + Identifiers.get(value.getItem())).setStyle(open));
    }

    protected String getFilename(ItemStack value) {
        String sizeString = addSize ? size + "x" + size + "_" : "";
        String fileName = _getFilename(value, useIdentifier);

        return (addDate ? Strings.dateTime() + "_" : "") + sizeString + fileName;
    }

    private String _getFilename(ItemStack value, boolean useIdentifier) {
        return useIdentifier ? Strings.sanitize(Identifiers.get(value.getItem())) : Strings.sanitize(value.getHoverName());
    }

}
