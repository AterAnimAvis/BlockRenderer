package com.unascribed.blockrenderer.forge.client.render.item;

import com.unascribed.blockrenderer.forge.client.varia.Identifiers;
import com.unascribed.blockrenderer.forge.client.varia.Strings;
import com.unascribed.blockrenderer.forge.client.varia.Styles;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.function.Consumer;

import static com.unascribed.blockrenderer.forge.client.varia.Strings.translate;

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
        if (file == null) {
            Strings.addMessage(translate(
                    "msg.block_renderer.render.success.nofile",
                    name,
                    Strings.asClickable(folder)
            ).withStyle(Styles.GOLD));
        } else {
            Strings.addMessage(translate(
                    "msg.block_renderer.render.success",
                    name,
                    Strings.asClickable(folder),
                    Strings.asClickable(file.getAbsoluteFile())
            ).withStyle(Styles.GOLD));
        }
    }

    protected String getFilename(ItemStack value) {
        String sizeString = addSize ? size + "x" + size + "_" : "";
        String fileName = _getFilename(value, useIdentifier);

        return (addDate ? Strings.dateTime() + "_" : "") + sizeString + fileName;
    }

    private String _getFilename(ItemStack value, boolean useIdentifier) {
        return useIdentifier ? Strings.sanitize(Identifiers.get(value.getItem())) : Strings.sanitize(value.getDisplayName());
    }

}
