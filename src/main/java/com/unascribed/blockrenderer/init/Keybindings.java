package com.unascribed.blockrenderer.init;

import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import static com.unascribed.blockrenderer.Reference.MOD_ID;

public interface Keybindings {

    FabricKeyBinding render = FabricKeyBinding.Builder.create(new Identifier(MOD_ID + ":render"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, "key.categories." + MOD_ID).build();

    static void register() {
        KeyBindingRegistry.INSTANCE.addCategory("key.categories." + MOD_ID);
        KeyBindingRegistry.INSTANCE.register(render);
    }

}
