package com.unascribed.blockrenderer.fabric.client.screens.item;


import com.unascribed.blockrenderer.fabric.client.render.RenderManager;
import com.unascribed.blockrenderer.fabric.client.render.item.ItemRenderer;
import com.unascribed.blockrenderer.fabric.client.screens.widgets.HoverableTinyButtonWidget;
import com.unascribed.blockrenderer.fabric.client.varia.StringUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

/*
 * Note: Screen's get initialized in init
 */
@SuppressWarnings("NotNullFieldNotInitialized")
public class EnterNamespaceScreen extends BaseItemScreen {

    private static final TranslatableText TITLE = new TranslatableText("block_renderer.gui.namespace");

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
        assert client != null;
        client.keyboard.setRepeatEvents(true);

        boolean enabled = enabled();

        /* Note: This is the initializer, text can be null! */
        @SuppressWarnings("ConstantConditions")
        String oldText = (text == null ? prefill : text.getText());

        text = addButton(new TextFieldWidget(client.textRenderer, width / 2 - 100, height / 6 + 50, 200, 20, new TranslatableText("block_renderer.gui.namespace")), enabled);
        text.setChangedListener((value) -> emptySpec = value.trim().isEmpty());
        text.setText(oldText);
        text.setFocusUnlocked(false);
        setFocused(text);

        if (stack != null) {
            addButton(new HoverableTinyButtonWidget(
                    this,
                    width - 32,
                    height - 32,
                    new TranslatableText("block_renderer.gui.switch.single"),
                    new TranslatableText("block_renderer.gui.switch.single.tooltip"),
                    button -> client.openScreen(new EnterSizeScreen(old, stack)))
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
        assert client != null;
        client.keyboard.setRepeatEvents(false);
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
        assert client != null;

        super.render(stack, mouseX, mouseY, partialTicks);

        if (!emptySpec) return;

        drawCenteredText(stack, client.textRenderer, new TranslatableText("block_renderer.gui.emptySpec"), width / 2, height / 6 + 30, 0xFF5555);
    }

    @Override
    public void onRender(ButtonWidget button) {
        assert client != null;

        if (!renderButton.visible) return;

        client.openScreen(old);
        if (client.world == null) return;

        RenderManager.push(ItemRenderer.bulk(text.getText(), round(size), useId.isChecked(), addSize.isChecked()));
    }
}
