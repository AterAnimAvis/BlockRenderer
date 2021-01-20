package com.unascribed.blockrenderer.varia;

public interface Time {

    int TICKS_IN_A_SECOND = 20;
    int AUTO_LOOP = 30 * TICKS_IN_A_SECOND;
    int MAX_CONSUME = 30 * TICKS_IN_A_SECOND;

    long NANOS_IN_A_SECOND = 1_000_000_000L;
    float NANOS_IN_A_SECOND_F = 1_000_000_000F;
    long NANOS_PER_FRAME = NANOS_IN_A_SECOND / TICKS_IN_A_SECOND;

}
