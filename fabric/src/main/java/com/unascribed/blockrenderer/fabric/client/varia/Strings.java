package com.unascribed.blockrenderer.fabric.client.varia;

import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

public interface Strings {

    DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    static String getNamespace(@Nullable ItemStack stack) {
        if (stack == null) return "";

        ResourceLocation identifier = Registry.ITEM.getKey(stack.getItem());

        return Objects.equals(identifier, new ResourceLocation("air")) ? "" : identifier.getNamespace();
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

    static String getFilename(ItemStack value, int size, boolean addDate, boolean addSize, boolean useIdentifier) {
        String sizeString = addSize ? size + "x" + size + "_" : "";
        String fileName = useIdentifier ? sanitize(Identifiers.get(value.getItem())) : sanitize(value.getDisplayName());

        return (addDate ? dateTime() + "_" : "") + sizeString + fileName;
    }

    static TranslationTextComponent translate(String name, Object... args) {
        return new TranslationTextComponent(name, args);
    }

    static StringTextComponent rawText(String text) {
        return new StringTextComponent(text);
    }

}
