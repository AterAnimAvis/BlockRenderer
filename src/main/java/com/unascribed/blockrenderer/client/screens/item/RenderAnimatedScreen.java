package com.unascribed.blockrenderer.client.screens.item;

import com.unascribed.blockrenderer.client.render.RenderManager;
import com.unascribed.blockrenderer.client.render.item.ItemRenderer;
import com.unascribed.blockrenderer.client.screens.widgets.HoverableCheckboxWidget;
import com.unascribed.blockrenderer.client.screens.widgets.HoverableTextFieldWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("NotNullFieldNotInitialized")
public class RenderAnimatedScreen extends EnterSizeScreen {

    private static final TranslationTextComponent TITLE = new TranslationTextComponent("block_renderer.gui.renderAnimated");

    private CheckboxButton autoLoop;

    private TextFieldWidget length;

    private boolean isInteger;

    public RenderAnimatedScreen(@Nullable Screen old, ItemStack stack) {
        super(TITLE, old, stack, false);
    }

    @Override
    public void init() {
        assert minecraft != null;
        minecraft.keyboardListener.enableRepeatEvents(true);
        boolean enabled = enabled();

        super.init();

        autoLoop = addButton(new HoverableCheckboxWidget(this, width / 2 + 2, height / 6 + 166, 98, 20, new TranslationTextComponent("block_renderer.gui.loop"), new TranslationTextComponent("block_renderer.gui.loop.tooltip"), false), enabled);

        /* Note: This is the initializer, text can be null! */
        @SuppressWarnings("ConstantConditions")
        String prefill = (length == null ? "20" : length.getText());

        length = addButton(new HoverableTextFieldWidget(this, minecraft.fontRenderer, width / 2 - 100, height / 6 + 74, 200, 20, new TranslationTextComponent("block_renderer.gui.animatedLength"), new TranslationTextComponent("block_renderer.gui.animatedLength.tooltip")), enabled);
        length.setResponder((value) -> {
            isInteger = false;
            try {
                int v = Integer.parseInt(value);
                if (v > 0) isInteger = true;
            } catch (NumberFormatException ignore) {
            }
        });
        length.setText(prefill);
        length.setCanLoseFocus(false);
        setFocusedDefault(length);
    }

    @Override
    public void tick() {
        super.tick();
        length.tick();
        renderButton.visible = isInteger;
    }

    @Override
    public void onClose() {
        assert minecraft != null;
        minecraft.keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (length.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (length.mouseClicked(mouseX, mouseY, button)) return true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onRender(Button button) {
        assert minecraft != null;

        minecraft.displayGuiScreen(old);
        if (minecraft.world == null) return;

        RenderManager.push(ItemRenderer.animated(stack, round(size), useId.isChecked(), addSize.isChecked(), Integer.parseInt(length.getText()), autoLoop.isChecked()));
    }

}
