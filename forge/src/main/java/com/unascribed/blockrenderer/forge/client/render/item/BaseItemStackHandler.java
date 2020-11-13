package com.unascribed.blockrenderer.forge.client.render.item;

import com.unascribed.blockrenderer.forge.client.varia.Identifiers;
import com.unascribed.blockrenderer.forge.client.varia.StringUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
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
        report(Identifiers.get(value.getItem()), future);
    }

    protected void report(Object name) {
        report(name, null);
    }

    protected void report(Object name, @Nullable File file) {
        Style gold = Style.EMPTY.applyFormatting(TextFormatting.GOLD);

        if (file == null) {
            StringUtils.addMessage(new TranslationTextComponent(
                    "msg.block_renderer.render.success.nofile",
                    name,
                    StringUtils.asClickable(folder)
            ).setStyle(gold));
        } else {
            StringUtils.addMessage(new TranslationTextComponent(
                    "msg.block_renderer.render.success",
                    name,
                    StringUtils.asClickable(folder),
                    StringUtils.asClickable(file.getAbsoluteFile())
            ).setStyle(gold));
        }
    }

    protected String getFilename(ItemStack value) {
        String sizeString = addSize ? size + "x" + size + "_" : "";
        String fileName = _getFilename(value, useIdentifier);

        return (addDate ? StringUtils.dateTime() + "_" : "") + sizeString + fileName;
    }

    private String _getFilename(ItemStack value, boolean useIdentifier) {
        return useIdentifier ? StringUtils.sanitize(Identifiers.get(value.getItem())) : StringUtils.sanitize(value.getDisplayName());
    }

}
