package com.unascribed.blockrenderer.forge.client.screens.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.blockrenderer.forge.client.render.RenderManager;
import com.unascribed.blockrenderer.forge.client.render.item.ItemRenderer;
import com.unascribed.blockrenderer.forge.client.screens.widgets.HoverableTinyButtonWidget;
import com.unascribed.blockrenderer.forge.client.varia.StringUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.Nullable;

/*
 * Note: Screen's get initialized in init
 */
@SuppressWarnings("NotNullFieldNotInitialized")
public class EnterNamespaceScreen extends BaseItemScreen {

    private static final TranslationTextComponent TITLE = new TranslationTextComponent("block_renderer.gui.namespace");

    private boolean emptySpec = false;

    private final String prefill;

    private TextFieldWidget text;

    private final @Nullable ItemStack stack;

    public EnterNamespaceScreen(@Nullable Screen old, String prefill) {
        super(TITLE, old);
        this.prefill = prefill;
        this.stack = null;
    }

    public EnterNamespaceScreen(@Nullable Screen old, @Nullable ItemStack stack) {
        super(TITLE, old);
        this.prefill = StringUtils.getNamespace(stack);
        this.stack = stack;
    }

    @Override
    public void init() {
        assert minecraft != null;
        minecraft.keyboardListener.enableRepeatEvents(true);

        boolean enabled = enabled();

        /* Note: This is the initializer, text can be null! */
        @SuppressWarnings("ConstantConditions")
        String oldText = (text == null ? prefill : text.getText());

        text = addButton(new TextFieldWidget(minecraft.fontRenderer, width / 2 - 100, height / 6 + 50, 200, 20, new TranslationTextComponent("block_renderer.gui.namespace")), enabled);
        text.setResponder((value) -> emptySpec = value.trim().isEmpty());
        text.setText(oldText);
        text.setCanLoseFocus(false);
        setFocusedDefault(text);

        if (stack != null) {
            addButton(new HoverableTinyButtonWidget(
                    this,
                    width - 32,
                    height - 32,
                    new TranslationTextComponent("block_renderer.gui.switch.single"),
                    new TranslationTextComponent("block_renderer.gui.switch.single.tooltip"),
                    button -> minecraft.displayGuiScreen(new EnterSizeScreen(old, stack)))
            );
        }

        super.init();

        renderButton.visible = !emptySpec;
    }

    @Override
    public void tick() {
        super.tick();
        text.tick();
        renderButton.visible = !emptySpec;
    }

    @Override
    public void onClose() {
        assert minecraft != null;
        minecraft.keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (text.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (text.mouseClicked(mouseX, mouseY, button)) return true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        assert minecraft != null;

        super.render(stack, mouseX, mouseY, partialTicks);

        if (!emptySpec) return;

        drawCenteredString(stack, minecraft.fontRenderer, new TranslationTextComponent("block_renderer.gui.emptySpec"), width / 2, height / 6 + 30, 0xFF5555);
    }

    @Override
    public void onRender(Button button) {
        assert minecraft != null;

        if (!renderButton.visible) return;

        minecraft.displayGuiScreen(old);
        if (minecraft.world == null) return;

        RenderManager.push(ItemRenderer.bulk(text.getText(), round(size), useId.isChecked(), addSize.isChecked()));
    }
}
