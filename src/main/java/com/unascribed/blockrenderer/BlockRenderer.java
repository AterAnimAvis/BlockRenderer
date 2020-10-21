package com.unascribed.blockrenderer;

import com.unascribed.blockrenderer.client.proxy.ClientProxy;
import com.unascribed.blockrenderer.proxy.CommonProxy;
import com.unascribed.blockrenderer.proxy.DedicatedProxy;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

@Mod(Reference.MOD_ID)
public class BlockRenderer {

    public static final Logger LOGGER = LogManager.getLogger(Reference.NAME);

    public static CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> DedicatedProxy::new);

    public BlockRenderer() {
        proxy.init();

        LOGGER.info("Running Version: " + Reference.VERSION);

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
