package com.unascribed.blockrenderer.forge.proxy;

import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

public class DedicatedProxy extends CommonProxy {

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarted);
    }

    public void onServerStarted(FMLServerStartedEvent event) {
        Log.error(Markers.ROOT, "Running a Client only Mod on a Dedicated Server");
    }

}
