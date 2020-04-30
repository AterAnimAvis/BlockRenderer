package com.unascribed.blockrenderer.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface StringUtils {

    DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    static Set<String> getNamespaces(String namespaceSpec) {
        Set<String> namespaces = Sets.newHashSet();

        for (String namespace : namespaceSpec.split(",")) namespaces.add(namespace.trim());

        return namespaces;
    }

    static void addMessage(String text) {
        Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(new StringTextComponent(text));
    }

    static void addMessage(ITextComponent text) {
        Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(text);
    }

    static String dateTime() {
        return DATETIME_FORMAT.format(new Date());
    }

    static String sanitize(ITextComponent text) {
        return sanitize(text.getUnformattedComponentText());
    }

    static String sanitize(String str) {
        return str.replaceAll("[^A-Za-z0-9-_ ]", "_");
    }

    static List<String> getTooltipFromItem(ItemStack stack) {
        Minecraft minecraft = Minecraft.getInstance();

        List<ITextComponent> texts = stack.getTooltip(minecraft.player, ITooltipFlag.TooltipFlags.NORMAL);
        List<String> tooltip = Lists.newArrayList();

        for(ITextComponent itextcomponent : texts) tooltip.add(itextcomponent.getFormattedText());

        return tooltip;
    }

}
