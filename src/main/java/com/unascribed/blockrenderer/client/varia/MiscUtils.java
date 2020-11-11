package com.unascribed.blockrenderer.client.varia;

import com.google.common.collect.Lists;
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

import static com.unascribed.blockrenderer.BlockRenderer.LOGGER;

public interface MiscUtils {

    static boolean isEscapePressed() {
        return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), GLFW.GLFW_KEY_ESCAPE);
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
                    item.fillItemGroup(ItemGroup.SEARCH, list);
                } catch (Throwable t) {
                    LOGGER.warn("Failed to get render-able items for {}", identifier, t);
                }

                stacks.addAll(list);
            }
        }

        return stacks;
    }

}
