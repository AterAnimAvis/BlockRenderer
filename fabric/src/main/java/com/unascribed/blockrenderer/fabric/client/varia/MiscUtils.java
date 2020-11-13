package com.unascribed.blockrenderer.fabric.client.varia;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Set;

public interface MiscUtils {

    //TODO: Use this to escape from Animated/Bulk Renders
    static boolean isEscapePressed() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_ESCAPE);
    }

    static List<ItemStack> collectStacks(Set<String> namespaces) {
        List<ItemStack> stacks = Lists.newArrayList();
        NonNullList<ItemStack> list = NonNullList.create();

        for (ResourceLocation identifier : Registry.ITEM.keySet()) {
            if (identifier != null && namespaces.contains(identifier.getNamespace()) || namespaces.contains("*")) {
                list.clear();

                Item item = Registry.ITEM.get(identifier);
                if (item == Items.AIR) continue;

                try {
                    item.fillItemCategory(CreativeModeTab.TAB_SEARCH, list);
                } catch (Throwable t) {
                    Log.error(Markers.SEARCH, "Failed to get render-able items for {}", identifier, t);
                }

                stacks.addAll(list);
            }
        }

        return stacks;
    }

}
