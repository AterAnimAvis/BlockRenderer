package com.unascribed.blockrenderer;

import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.JarVersionLookupHandler;

public interface Reference {

    String MOD_ID = "block_renderer";
    String NAME = "BlockRenderer";

    String VERSION = JarVersionLookupHandler.getSpecificationVersion(Reference.class).orElseGet(() -> System.getenv("MOD_VERSION"));

    boolean isDebug = !FMLEnvironment.production;

}
