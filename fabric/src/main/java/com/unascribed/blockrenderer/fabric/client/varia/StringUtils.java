package com.unascribed.blockrenderer.fabric.client.varia;

import com.google.common.collect.Sets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
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

        return Registry.ITEM.getId(stack.getItem()).getNamespace();
    }

    static Set<String> getNamespaces(String namespaceSpec) {
        Set<String> namespaces = Sets.newHashSet();

        for (String namespace : namespaceSpec.split(",")) namespaces.add(namespace.trim());

        return namespaces;
    }

    static void addMessage(Text text) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(text);
    }

    static String dateTime() {
        return DATETIME_FORMAT.format(new Date());
    }

    static String sanitize(Text text) {
        return sanitize(text.getString());
    }

    static String sanitize(Identifier identifier) {
        return sanitize(identifier.toString());
    }

    static String sanitize(String str) {
        return str.replaceAll("[^A-Za-z0-9-_ ]", "_");
    }

    static Text asClickable(File file) {
        LiteralText component = new LiteralText(file.getName());

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
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("block_renderer.file.tooltip")))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path))
                        .withUnderline(true)
        );

        return component;
    }

}
