package com.unascribed.blockrenderer.fabric.client.screens.widgets;

import net.minecraft.client.gui.widget.DoubleOptionSliderWidget;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;

public class UpdateableSliderWidget extends DoubleOptionSliderWidget {

    private final DoubleOption option;

    public UpdateableSliderWidget(GameOptions settings, int x, int y, int width, int height, DoubleOption option) {
        super(settings, x, y, width, height, option);
        this.option = option;
    }

    public void update(double desired) {
        value = option.getRatio(desired);
        applyValue();
        updateMessage();
    }

}
