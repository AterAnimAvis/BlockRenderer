package com.unascribed.blockrenderer.init;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static com.unascribed.blockrenderer.Reference.MOD_ID;

public interface Keybindings {

    KeyBinding render = new KeyBinding("key." + MOD_ID + ".render", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, "key.categories." + MOD_ID);

    static void register() {
        KeyBindingHelper.registerKeyBinding(render);
    }

}
