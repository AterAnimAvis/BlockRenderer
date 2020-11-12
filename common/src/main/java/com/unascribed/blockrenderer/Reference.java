package com.unascribed.blockrenderer;

import com.unascribed.blockrenderer.varia.VersionUtilities;

import java.util.Objects;

public interface Reference {

    String ID = "block_renderer";
    String NAME = "BlockRenderer";
    String VERSION = VersionUtilities.getSpecificationVersion(Reference.class).orElse("DEVELOPMENT");

    boolean DEVELOPMENT = Objects.equals(VERSION, "DEVELOPMENT");

}
