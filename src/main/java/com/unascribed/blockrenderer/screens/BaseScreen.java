package com.unascribed.blockrenderer.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.blockrenderer.screens.widgets.HoverableCheckboxWidget;
import com.unascribed.blockrenderer.screens.widgets.HoverableTinyButtonWidget;
import com.unascribed.blockrenderer.screens.widgets.UpdateableSliderWidget;
import com.unascribed.blockrenderer.screens.widgets.options.ExtendedSliderPercentageOption;
import com.unascribed.blockrenderer.utils.MathUtils;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

public abstract class BaseScreen extends Screen {

    private static final int MIN_SIZE  = 16;
    private static final int THRESHOLD = 32;
    private static final int MAX_SIZE  = 2048;

    protected final Screen old;

    protected double size = 512;

    protected UpdateableSliderWidget slider;
    protected Button actualSize;
    protected Button renderButton;
    protected CheckboxButton useId;
    protected CheckboxButton addSize;

    public BaseScreen(ITextComponent title, @Nullable Screen old) {
        super(title);
        this.old = old;
    }

    @Override
    public void init() {
        assert minecraft != null;
        boolean enabled = minecraft.world != null;

        addButton(new Button(width/2-100, height/6+120, 98, 20, new TranslationTextComponent("gui.cancel"), button -> minecraft.displayGuiScreen(old)));

        renderButton = addButton(new Button(width/2+2, height/6+120, 98, 20, new TranslationTextComponent("block_renderer.gui.render"), this::onRender), enabled);

        size = MathHelper.clamp(size, MIN_SIZE, MAX_SIZE);

        ExtendedSliderPercentageOption option = new ExtendedSliderPercentageOption("block_renderer.gui.renderSize", MIN_SIZE, MAX_SIZE, 1, (settings) -> size, (settings, value) -> size = round(value), this::getSliderDisplay);
        slider = addButton(new UpdateableSliderWidget(minecraft.gameSettings, width/2-100, height/6+80, 200, 20, option), enabled);

        actualSize = addButton(new HoverableTinyButtonWidget(this, width/2+104, height/6+80, new TranslationTextComponent("block_renderer.gui.actualSize"), new TranslationTextComponent("block_renderer.gui.actualSize.tooltip"), button -> slider.update((int) minecraft.getMainWindow().getGuiScaleFactor() * 16)), enabled);
        useId = addButton(new HoverableCheckboxWidget(this, width/2-100, height / 6 + 144, 98, 20, new TranslationTextComponent("block_renderer.gui.useId"), new TranslationTextComponent("block_renderer.gui.useId.tooltip"), false), enabled);
        addSize = addButton(new HoverableCheckboxWidget(this, width/2+2, height / 6 + 144, 98, 20, new TranslationTextComponent("block_renderer.gui.addSize"), new TranslationTextComponent("block_renderer.gui.addSize.tooltip"), false), enabled);
    }

    protected int round(double value) {
        return MathUtils.roundAndClamp(value, MIN_SIZE, MAX_SIZE, THRESHOLD);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER && renderButton.visible) {
            onRender(renderButton);
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        assert minecraft != null;

        renderBackground(stack);

        super.render(stack, mouseX, mouseY, partialTicks);

        drawCenteredString(stack, minecraft.fontRenderer, title, width/2, height/6, -1);

        for (Widget widget : buttons)
            if (widget.isHovered())
                widget.renderToolTip(stack, mouseX, mouseY);

        if (minecraft.world != null) return;

        drawCenteredString(stack, minecraft.fontRenderer, new TranslationTextComponent("block_renderer.gui.noWorld"), width/2, height/6+30, 0xFF5555);
    }

    abstract void onRender(Button button);

    public ITextComponent getSliderDisplay(GameSettings settings, ExtendedSliderPercentageOption option) {
        int px = round(size);
        return option.getDisplayPrefix().copyRaw().appendString(": " + px + "x" + px);
    }

    protected <T extends Widget> T addButton(T button, boolean active) {
        addButton(button);

        button.active = active;
        button.visible = active;

        return button;
    }

}
