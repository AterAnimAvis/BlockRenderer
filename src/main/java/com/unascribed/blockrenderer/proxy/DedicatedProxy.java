package com.unascribed.blockrenderer.proxy;

import com.unascribed.blockrenderer.BlockRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

public class DedicatedProxy extends CommonProxy {

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarted);
    }

    public void onServerStarted(FMLServerStartedEvent event) {
        BlockRenderer.LOGGER.error("Running a Client only Mod on a Dedicated Server");
    }

}
