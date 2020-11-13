package com.unascribed.blockrenderer.varia;

public interface Time {

    int TICKS_IN_A_SECOND = 20;

    long NANOS_IN_A_SECOND = 1_000_000_000L;
    long NANOS_PER_FRAME = NANOS_IN_A_SECOND / TICKS_IN_A_SECOND;

}
