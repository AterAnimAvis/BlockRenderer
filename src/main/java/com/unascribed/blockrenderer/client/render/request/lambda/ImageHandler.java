package com.unascribed.blockrenderer.client.render.request.lambda;

import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface ImageHandler<T> extends BiConsumer<T, BufferedImage> {

    @NotNull
    default ImageHandler<T> andThen(@NotNull ImageHandler<? super T> after) {
        Objects.requireNonNull(after);

        return (l, r) -> {
            accept(l, r);
            after.accept(l, r);
        };
    }
}