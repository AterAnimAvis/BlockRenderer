package com.unascribed.blockrenderer.fabric.client.varia;

import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public interface Strings {

    DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    static String getNamespace(@Nullable ItemStack stack) {
        if (stack == null) return "";

        return Registry.ITEM.getKey(stack.getItem()).getNamespace();
    }

    static Set<String> getNamespaces(String namespaceSpec) {
        Set<String> namespaces = Sets.newHashSet();

        for (String namespace : namespaceSpec.split(",")) namespaces.add(namespace.trim());

        return namespaces;
    }

    static void addMessage(Component component) {
        Minecraft.getInstance().gui.getChat().addMessage(component);
    }

    static String dateTime() {
        return DATETIME_FORMAT.format(new Date());
    }

    static String sanitize(Component component) {
        return sanitize(component.getString());
    }

    static String sanitize(ResourceLocation identifier) {
        return sanitize(identifier.toString());
    }

    static String sanitize(String str) {
        return str.trim().replaceAll("[^A-Za-z0-9-_ ]", "_");
    }

    static Component asClickable(File file) {
        TextComponent component = rawText(file.getName());

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
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, translate("block_renderer.file.tooltip")))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path))
                        .withUnderlined(true)
        );

        return component;
    }

    static TranslatableComponent translate(String name, Object... args) {
        return new TranslatableComponent(name, args);
    }

    static TextComponent rawText(String text) {
        return new TextComponent(text);
    }

}
