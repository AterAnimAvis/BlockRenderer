package com.unascribed.blockrenderer.client.impl.js;

import com.unascribed.blockrenderer.client.api.Bounds;
import com.unascribed.blockrenderer.client.api.BoundsProvider;
import com.unascribed.blockrenderer.client.varia.logging.Markers;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.Marker;

import javax.annotation.Nullable;
import java.util.List;

import static com.unascribed.blockrenderer.Reference.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class JavascriptBoundsProvider extends AbstractJavascriptProvider<BoundsProvider> {

    static final JavascriptBoundsProvider INSTANCE = new JavascriptBoundsProvider();

    @Override
    Marker marker() {
        return Markers.BOUNDS;
    }

    @Override
    String type() {
        return "BoundsProvider";
    }

    @Override
    String fileType() {
        return ".bounds.js";
    }

    @Nullable
    @Override
    BoundsProvider cast(NashornScriptEngine engine) {
        return engine.getInterface(BoundsProvider.class);
    }

    @Override
    void removeProviders(List<BoundsProvider> providers) {
        Bounds.remove(providers);
    }

    @Override
    void addProviders(List<BoundsProvider> providers) {
        Bounds.add(providers);
    }

    @SubscribeEvent
    public static void onClientStart(FMLClientSetupEvent event) {
        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener((IResourceManagerReloadListener) manager -> INSTANCE.reload());
        INSTANCE.reload();
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    private static class ForgeEventBus {

        @SubscribeEvent
        public static void addListeners(AddReloadListenerEvent event) {
            event.addListener((IResourceManagerReloadListener) manager -> INSTANCE.reload());
            INSTANCE.reload();
        }

    }

}
