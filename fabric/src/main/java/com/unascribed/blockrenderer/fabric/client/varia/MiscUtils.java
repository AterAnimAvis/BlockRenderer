package com.unascribed.blockrenderer.fabric.client.varia;

import com.google.common.collect.Lists;
import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Set;

public interface MiscUtils {

    static boolean isEscapePressed() {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_ESCAPE);
    }

    static List<ItemStack> collectStacks(Set<String> namespaces) {
        List<ItemStack> stacks = Lists.newArrayList();
        DefaultedList<ItemStack> list = DefaultedList.of();

        for (Identifier identifier : Registry.ITEM.getIds()) {
            if (identifier != null && namespaces.contains(identifier.getNamespace()) || namespaces.contains("*")) {
                list.clear();

                Item item = Registry.ITEM.get(identifier);
                if (item == Items.AIR) continue;

                try {
                    item.appendStacks(ItemGroup.SEARCH, list);
                } catch (Throwable t) {
                    Log.error(Markers.SEARCH, "Failed to get render-able items for {}", identifier, t);
                }

                stacks.addAll(list);
            }
        }

        return stacks;
    }

}
