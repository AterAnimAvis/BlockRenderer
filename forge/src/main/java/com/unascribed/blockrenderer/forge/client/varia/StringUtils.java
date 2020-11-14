package com.unascribed.blockrenderer.forge.client.varia;

import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public interface StringUtils {

    DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    static String getNamespace(@Nullable ItemStack stack) {
        if (stack == null) return "";

        ResourceLocation identifier = ForgeRegistries.ITEMS.getKey(stack.getItem());

        return identifier == null ? "" : identifier.getNamespace();
    }

    static Set<String> getNamespaces(String namespaceSpec) {
        Set<String> namespaces = Sets.newHashSet();

        for (String namespace : namespaceSpec.split(",")) namespaces.add(namespace.trim());

        return namespaces;
    }

    static void addMessage(ITextComponent text) {
        Minecraft.getInstance().gui.getChat().addMessage(text);
    }

    static String dateTime() {
        return DATETIME_FORMAT.format(new Date());
    }

    static String sanitize(ITextComponent text) {
        return sanitize(text.getString());
    }

    static String sanitize(ResourceLocation identifier) {
        return sanitize(identifier.toString());
    }

    static String sanitize(String str) {
        return str.replaceAll("[^A-Za-z0-9-_ ]", "_");
    }

    static ITextComponent asClickable(File file) {
        StringTextComponent component = new StringTextComponent(file.getName());

        String path;

        try {
            path = file.getAbsoluteFile().getCanonicalPath();
        } catch (Exception ignored) {
            try {
                path = file.getCanonicalPath();
            } catch (Exception ignored2) {
                return component;
            }
        }

        component.setStyle(
                component.getStyle()
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("block_renderer.file.tooltip")))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path))
                        .setUnderlined(true)
        );

        return component;
    }

}
