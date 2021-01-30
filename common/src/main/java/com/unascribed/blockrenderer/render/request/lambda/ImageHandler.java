package com.unascribed.blockrenderer.render.request.lambda;

import com.unascribed.blockrenderer.varia.rendering.STBWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface ImageHandler<T> extends BiConsumer<T, STBWrapper> {

    @NotNull
    default ImageHandler<T> andThen(@NotNull ImageHandler<? super T> after) {
        Objects.requireNonNull(after);

        return (l, r) -> {
            accept(l, r);
            after.accept(l, r);
        };
    }
}
