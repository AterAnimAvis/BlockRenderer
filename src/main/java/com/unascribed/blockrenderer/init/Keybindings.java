package com.unascribed.blockrenderer.init;

import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public interface Keybindings {

    FabricKeyBinding render = FabricKeyBinding.Builder.create(new Identifier("blockrenderer:render"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, "key.categories.blockrenderer").build();

    static void register() {
        KeyBindingRegistry.INSTANCE.addCategory("key.categories.blockrenderer");
        KeyBindingRegistry.INSTANCE.register(render);
    }

}
