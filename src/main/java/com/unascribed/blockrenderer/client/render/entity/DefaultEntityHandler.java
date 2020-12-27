package com.unascribed.blockrenderer.client.render.entity;

import com.unascribed.blockrenderer.client.render.request.lambda.ImageHandler;
import com.unascribed.blockrenderer.client.varia.Files;
import com.unascribed.blockrenderer.client.varia.Identifiers;
import com.unascribed.blockrenderer.client.varia.StringUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Consumer;

import static com.unascribed.blockrenderer.client.varia.StringUtils.sanitize;

public class DefaultEntityHandler implements Consumer<EntityData>, ImageHandler<EntityData>, Runnable {

    @Nullable
    protected File future = null;
    protected final File folder;
    protected final int size;
    protected final boolean useIdentifier;
    protected final boolean addSize;
    protected final boolean addDate;

    public DefaultEntityHandler(File folder, int size, boolean useIdentifier, boolean addSize, boolean addDate) {
        this.folder = folder;
        this.size = size;
        this.useIdentifier = useIdentifier;
        this.addSize = addSize;
        this.addDate = addDate;
    }

    @Override
    public void accept(EntityData value) {
        Style open = Style.EMPTY.applyFormatting(TextFormatting.GOLD);

        if (future != null)
            open = open.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, future.getAbsolutePath()));

        StringUtils.addMessage(new StringTextComponent("> Finished Rendering " + Identifiers.get(value.entity.getType())).setStyle(open));
    }

    protected String getFilename(EntityData value) {
        String sizeString = addSize ? size + "x" + size + "_" : "";
        String fileName = _getFilename(value.entity, useIdentifier);

        return (addDate ? StringUtils.dateTime() + "_" : "") + sizeString + fileName;
    }

    private String _getFilename(Entity value, boolean useIdentifier) {
        return useIdentifier ? sanitize(Identifiers.get(value.getType())) : sanitize(value.getDisplayName());
    }

    @Override
    public void accept(EntityData value, BufferedImage image) {
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
