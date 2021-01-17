package com.unascribed.blockrenderer.fabric.client.screens.widgets;

import com.unascribed.blockrenderer.varia.Maths;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.OptionSlider;
import net.minecraft.client.settings.SliderPercentageOption;
import org.lwjgl.glfw.GLFW;

public class UpdateableSliderWidget extends OptionSlider {

    private final SliderPercentageOption option;

    public UpdateableSliderWidget(GameSettings settings, int x, int y, int width, int height, SliderPercentageOption option) {
        super(settings, x, y, width, height, option);
        this.option = option;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_W) {
            update(option.get(settings) + 1);
        }

        if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_S) {
            update(option.get(settings) - 1);
        }

        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_A) {
            setSliderValue(sliderValue - (1.0 / (this.width - 8F)));
        }

        if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_D) {
            setSliderValue(sliderValue + (1.0 / (this.width - 8F)));
        }

        return false;
    }

    public void update(double desired) {
        sliderValue = option.normalizeValue(desired);

        /* mcp: applyValue */
        func_230972_a_();

        /* mcp: updateMessage */
        func_230979_b_();
    }

    private void setSliderValue(double desiredRatio) {
        sliderValue = Maths.clamp(desiredRatio, 0.0D, 1.0D);

        /* mcp: applyValue */
        func_230972_a_();

        /* mcp: updateMessage */
        func_230979_b_();
    }

}
