package com.unascribed.blockrenderer.forge;

import com.unascribed.blockrenderer.InternalAPI;
import com.unascribed.blockrenderer.Reference;
import com.unascribed.blockrenderer.forge.client.init.Keybindings;
import com.unascribed.blockrenderer.forge.client.proxy.ClientProxy;
import com.unascribed.blockrenderer.forge.client.render.RenderManager;
import com.unascribed.blockrenderer.forge.client.varia.Registries;
import com.unascribed.blockrenderer.forge.client.varia.rendering.GL;
import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class BlockRenderer {

    public BlockRenderer() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BlockRenderer::onInitializeClient);
        MinecraftForge.EVENT_BUS.addListener(BlockRenderer::onFrameStart);

        Registries.clazzLoad();

        Log.info(Markers.ROOT, "Running Version: " + Reference.VERSION);

        InternalAPI.setGL(GL.INSTANCE);
        InternalAPI.setRenderManager(RenderManager.INSTANCE);
    }

    private static void onInitializeClient(FMLClientSetupEvent event) {
        Keybindings.register();
    }

    private static void onFrameStart(TickEvent.RenderTickEvent e) {
        if (e.phase != TickEvent.Phase.START) return;

        ClientProxy.onFrameStart();
    }

}
