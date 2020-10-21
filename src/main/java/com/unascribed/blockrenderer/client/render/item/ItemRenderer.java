package com.unascribed.blockrenderer.client.render.item;

import com.google.common.base.Joiner;
import com.unascribed.blockrenderer.client.render.IRequest;
import com.unascribed.blockrenderer.client.render.map.DefaultPngMapHandler;
import com.unascribed.blockrenderer.client.render.map.MapDecorations;
import com.unascribed.blockrenderer.client.render.map.MapParameters;
import com.unascribed.blockrenderer.client.render.map.MapRenderer;
import com.unascribed.blockrenderer.client.render.request.AnimatedRenderingRequest;
import com.unascribed.blockrenderer.client.render.request.BulkRenderingRequest;
import com.unascribed.blockrenderer.client.render.request.RenderingRequest;
import com.unascribed.blockrenderer.client.varia.Files;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.MapData;

import java.io.File;
import java.util.List;
import java.util.Set;

import static com.unascribed.blockrenderer.client.varia.MiscUtils.collectStacks;
import static com.unascribed.blockrenderer.client.varia.StringUtils.*;

public class ItemRenderer {

    public static IRequest single(ItemStack stack, int size, boolean useId, boolean addSize) {
        DefaultPngItemStackHandler handler = new DefaultPngItemStackHandler(Files.DEFAULT_FOLDER, size, useId, addSize, true);

        return new RenderingRequest<>(
                new ItemStackRenderer(),
                new ItemStackParameters(size),
                stack,
                handler,
                handler
        );
    }

    public static IRequest single(ItemStack stack, MapData data, int size, boolean useId, boolean addSize, MapDecorations decorations) {
        DefaultPngMapHandler handler = new DefaultPngMapHandler(stack, Files.DEFAULT_FOLDER, size, useId, addSize, true);

        return new RenderingRequest<>(
                new MapRenderer(),
                new MapParameters(size, decorations),
                data,
                handler,
                handler
        );
    }

    public static IRequest bulk(String spec, int size, boolean useId, boolean addSize) {
        Set<String> namespaces = getNamespaces(spec);
        List<ItemStack> renders = collectStacks(namespaces);
        String joined = Joiner.on(", ").join(namespaces);

        String sizeString = addSize ? size + "x" + size + "_" : "";
        File folder = new File(Files.DEFAULT_FOLDER, dateTime() + "_" + sizeString + sanitize(joined) + "/");

        //TODO: Split out into BulkPngItemStackHandler
        DefaultPngItemStackHandler handler = new DefaultPngItemStackHandler(folder, size, useId, false, false);

        return new BulkRenderingRequest<>(
                new ItemStackRenderer(),
                new ItemStackParameters(size),
                renders,
                handler,
                handler
        );
    }

    public static IRequest animated(ItemStack stack, int size, boolean useId, boolean addSize, int length, boolean loop) {
        DefaultGifItemStackHandler handler = new DefaultGifItemStackHandler(Files.DEFAULT_FOLDER, size, useId, addSize, true);

        return new AnimatedRenderingRequest<>(
                new ItemStackRenderer(),
                new ItemStackParameters(size),
                stack,
                length,
                loop,
                handler,
                handler
        );
    }

}
