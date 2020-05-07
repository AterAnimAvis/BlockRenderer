package com.unascribed.blockrenderer.screens;

import com.unascribed.blockrenderer.screens.widgets.HoverableCheckboxWidget;
import com.unascribed.blockrenderer.screens.widgets.HoverableTinyButtonWidget;
import com.unascribed.blockrenderer.screens.widgets.UpdateableSliderWidget;
import com.unascribed.blockrenderer.utils.MathUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

public abstract class BaseScreen extends Screen {

    private static final int MIN_SIZE  = 16;
    private static final int THRESHOLD = 32;
    private static final int MAX_SIZE  = 2048;

    @Nullable
    protected final Screen old;

    protected double size = 512;

    protected UpdateableSliderWidget slider;
    protected ButtonWidget actualSize;
    protected ButtonWidget renderButton;
    protected CheckboxWidget useId;
    protected CheckboxWidget addSize;

    public BaseScreen(Text title, @Nullable Screen old) {
        super(title);
        this.old = old;
    }

    @Override
    public void init() {
        assert client != null;
        boolean enabled = client.world != null;

        addButton(new ButtonWidget(width/2-100, height/6+120, 98, 20, new TranslatableText("gui.cancel"), button -> client.openScreen(old)));

        renderButton = addButton(new ButtonWidget(width/2+2, height/6+120, 98, 20, new TranslatableText("block_renderer.gui.render"), this::onRender), enabled);

        size = MathHelper.clamp(size, MIN_SIZE, MAX_SIZE);

        DoubleOption option = new DoubleOption("block_renderer.gui.renderSize", MIN_SIZE, MAX_SIZE, 1, (settings) -> size, (settings, value) -> size = round(value), this::getSliderDisplay);
        slider = addButton(new UpdateableSliderWidget(client.options, width/2-100, height/6+80, 200, 20, option), enabled);

        actualSize = addButton(new HoverableTinyButtonWidget(this, width/2+104, height/6+80, new TranslatableText("block_renderer.gui.actualSize"), new TranslatableText("block_renderer.gui.actualSize.tooltip"), button -> slider.update((int) client.getWindow().getScaleFactor() * 16)), enabled);
        useId = addButton(new HoverableCheckboxWidget(this, width/2-100, height / 6 + 144, 98, 20, new TranslatableText("block_renderer.gui.useId"), new TranslatableText("block_renderer.gui.useId.tooltip"), false), enabled);
        addSize = addButton(new HoverableCheckboxWidget(this, width/2+2, height / 6 + 144, 98, 20, new TranslatableText("block_renderer.gui.addSize"), new TranslatableText("block_renderer.gui.addSize.tooltip"), false), enabled);
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
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        assert client != null;

        renderBackground(matrices);

        super.render(matrices, mouseX, mouseY, partialTicks);

        drawCenteredString(matrices, client.textRenderer, title.getString(), width/2, height/6, -1);

        for (AbstractButtonWidget widget : buttons)
            if (widget.isHovered())
                widget.renderToolTip(matrices, mouseX, mouseY);

        if (client.world != null) return;

        drawCenteredString(matrices, client.textRenderer, I18n.translate("block_renderer.gui.noWorld"), width/2, height/6+30, 0xFF5555);
    }

    abstract void onRender(AbstractButtonWidget button);

    public MutableText getSliderDisplay(GameOptions settings, DoubleOption option) {
        int px = round(size);
        return option.getDisplayPrefix().append(px + "x" + px);
    }

    protected <T extends AbstractButtonWidget> T addButton(T button, boolean active) {
        addButton(button);

        button.active = active;
        button.visible = active;

        return button;
    }

}
