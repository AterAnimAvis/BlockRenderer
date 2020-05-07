package com.unascribed.blockrenderer.init;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import static com.unascribed.blockrenderer.Reference.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Bus.MOD)
public interface Keybindings {

    KeyBinding render = new KeyBinding("key.block_renderer.render", GLFW.GLFW_KEY_GRAVE_ACCENT, "key.categories.block_renderer");

    @SubscribeEvent
    static void register(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(render);
    }

}
