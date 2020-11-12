package com.unascribed.blockrenderer.varia;

import java.util.Optional;

public class VersionUtilities {

    public static Optional<String> getSpecificationVersion(Class<?> clazz) {
        return Optional.ofNullable(clazz.getPackage().getSpecificationVersion());
    }

}
