package com.unascribed.blockrenderer.forge.client.init;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public interface Keybindings {

    KeyBinding render = new KeyBinding("key.block_renderer.render", GLFW.GLFW_KEY_GRAVE_ACCENT, "key.categories.block_renderer");

    static void register() {
        ClientRegistry.registerKeyBinding(render);
    }

}
