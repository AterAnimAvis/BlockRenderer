package com.unascribed.blockrenderer.fabric.client.screens.widgets;

import net.minecraft.client.gui.widget.DoubleOptionSliderWidget;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class UpdateableSliderWidget extends DoubleOptionSliderWidget {

    private final DoubleOption option;

    public UpdateableSliderWidget(GameOptions settings, int x, int y, int width, int height, DoubleOption option) {
        super(settings, x, y, width, height, option);
        this.option = option;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_W) {
            update(option.get(options) + 1);
        }

        if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_S) {
            update(option.get(options) - 1);
        }

        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_A) {
            setSliderValue(value - (1.0 / (this.width - 8F)));
        }

        if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_D) {
            setSliderValue(value + (1.0 / (this.width - 8F)));
        }

        return false;
    }

    public void update(double desired) {
        value = option.getRatio(desired);
        applyValue();
        updateMessage();
    }

    private void setSliderValue(double desiredRatio) {
        value = MathHelper.clamp(desiredRatio, 0.0D, 1.0D);
        applyValue();
        updateMessage();
    }

}
