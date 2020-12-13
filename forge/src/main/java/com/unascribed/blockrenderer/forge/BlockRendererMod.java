package com.unascribed.blockrenderer.forge;

import com.unascribed.blockrenderer.Reference;
import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

@Mod(Reference.ID)
public class BlockRendererMod {

    public BlockRendererMod() {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> BlockRenderer::new);
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () ->
                Log.error(Markers.ROOT, "Running a Client Side Mod on a Dedicated Server")
        );

        registerDisplayTest(ModLoadingContext.get());
    }

    /**
     * Ensure that we don't cause the client to show a server as incompatible and vice-versa
     */
    private void registerDisplayTest(ModLoadingContext context) {
        Supplier<String> versionProvider = () -> FMLNetworkConstants.IGNORESERVERONLY;
        BiPredicate<String, Boolean> versionChecker = (version, isNetwork) -> true;
        Pair<Supplier<String>, BiPredicate<String, Boolean>> extension = Pair.of(versionProvider, versionChecker);

        context.registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> extension);
    }

}
