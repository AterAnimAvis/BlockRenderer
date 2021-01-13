package com.unascribed.blockrenderer.fabric.client.screens.item;

import com.unascribed.blockrenderer.fabric.client.render.RenderManager;
import com.unascribed.blockrenderer.fabric.client.render.item.ItemRenderer;
import com.unascribed.blockrenderer.fabric.client.screens.widgets.HoverableCheckboxWidget;
import com.unascribed.blockrenderer.fabric.client.screens.widgets.HoverableTextFieldWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("NotNullFieldNotInitialized")
public class RenderAnimatedScreen extends EnterSizeScreen {

    private static final TranslatableText TITLE = new TranslatableText("block_renderer.gui.renderAnimated");

    private CheckboxWidget autoLoop;

    private TextFieldWidget length;

    private boolean isInteger;

    public RenderAnimatedScreen(@Nullable Screen old, ItemStack stack) {
        super(TITLE, old, stack, false);
    }

    @Override
    public void init() {
        assert client != null;
        client.keyboard.setRepeatEvents(true);
        boolean enabled = enabled();

        autoLoop = addButton(new HoverableCheckboxWidget(this, width / 2 + 2, height / 6 + 166, 98, 20, new TranslatableText("block_renderer.gui.loop"), new TranslatableText("block_renderer.gui.loop.tooltip"), false), enabled);

        /* Note: This is the initializer, text can be null! */
        @SuppressWarnings("ConstantConditions")
        String prefill = (length == null ? "20" : length.getText());

        length = addButton(new HoverableTextFieldWidget(this, client.textRenderer, width / 2 - 100, height / 6 + 74, 200, 20, new TranslatableText("block_renderer.gui.animatedLength"), new TranslatableText("block_renderer.gui.animatedLength.tooltip")), enabled);
        length.setChangedListener((value) -> {
            isInteger = false;
            try {
                int v = Integer.parseInt(value);
                if (v > 0) isInteger = true;
            } catch (NumberFormatException ignore) {
            }
        });
        length.setText(prefill);
        addChild(length);
        setInitialFocus(length);

        super.init();
    }

    @Override
    public void tick() {
        super.tick();
        length.tick();
        renderButton.visible = isInteger;
    }

    @Override
    public void onClose() {
        assert client != null;
        client.keyboard.setRepeatEvents(false);
    }

    @Override
    public void onRender(ButtonWidget button) {
        assert client != null;

        client.openScreen(old);
        if (client.world == null) return;

        RenderManager.push(ItemRenderer.animated(stack, round(size), useId.isChecked(), addSize.isChecked(), Integer.parseInt(length.getText()), autoLoop.isChecked()));
    }

}
