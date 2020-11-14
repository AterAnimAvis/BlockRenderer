package com.unascribed.blockrenderer.forge.client.varia;

import com.google.common.collect.Lists;
import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Set;

public interface MiscUtils {

    //TODO: Use this to escape from Animated/Bulk Renders
    static boolean isEscapePressed() {
        return InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_ESCAPE);
    }

    static List<ItemStack> collectStacks(Set<String> namespaces) {
        List<ItemStack> stacks = Lists.newArrayList();
        NonNullList<ItemStack> list = NonNullList.create();

        for (ResourceLocation identifier : ForgeRegistries.ITEMS.getKeys()) {
            if (identifier != null && namespaces.contains(identifier.getNamespace()) || namespaces.contains("*")) {
                list.clear();

                Item item = ForgeRegistries.ITEMS.getValue(identifier);
                if (item == null || item == Items.AIR) continue;

                try {
                    item.fillItemCategory(ItemGroup.TAB_SEARCH, list);
                } catch (Throwable t) {
                    Log.error(Markers.SEARCH, "Failed to get render-able items for {}", identifier, t);
                }

                stacks.addAll(list);
            }
        }

        return stacks;
    }

}
