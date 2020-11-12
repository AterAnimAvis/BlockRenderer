package com.unascribed.blockrenderer.fabric;

import com.unascribed.blockrenderer.Reference;
import com.unascribed.blockrenderer.fabric.client.init.Keybindings;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockRenderer implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger(Reference.NAME);

    public BlockRenderer() {
        LOGGER.info("Running Version: " + Reference.VERSION);
    }

    @Override
    public void onInitializeClient() {
        Keybindings.register();
    }

}
