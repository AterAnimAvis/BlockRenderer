package com.unascribed.blockrenderer.fabric.client.screens.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unascribed.blockrenderer.fabric.client.render.RenderManager;
import com.unascribed.blockrenderer.fabric.client.render.Requests;
import com.unascribed.blockrenderer.fabric.client.screens.widgets.HoverableTinyButtonWidget;
import com.unascribed.blockrenderer.fabric.client.varia.StringUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import static com.unascribed.blockrenderer.fabric.client.varia.StringUtils.translate;

/*
 * Note: Screen's get initialized in init
 */
@SuppressWarnings("NotNullFieldNotInitialized")
public class EnterNamespaceScreen extends BaseItemScreen {

    private static final TranslatableComponent TITLE = translate("block_renderer.gui.namespace");

    private boolean emptySpec = false;

    private final String prefill;

    private EditBox text;

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
        minecraft.keyboardHandler.setSendRepeatsToGui(true);

        boolean enabled = enabled();

        /* Note: This is the initializer, text can be null! */
        @SuppressWarnings("ConstantConditions")
        String oldText = (text == null ? prefill : text.getValue());

        text = addButton(new EditBox(minecraft.font, width / 2 - 100, height / 6 + 50, 200, 20, translate("block_renderer.gui.namespace")), enabled);
        text.setResponder((value) -> emptySpec = value.trim().isEmpty());
        text.setValue(oldText);
        text.setCanLoseFocus(false);
        setInitialFocus(text);

        if (stack != null) {
            addButton(new HoverableTinyButtonWidget(
                    this,
                    width - 32,
                    height - 32,
                    translate("block_renderer.gui.switch.single"),
                    translate("block_renderer.gui.switch.single.tooltip"),
                    button -> minecraft.setScreen(new EnterSizeScreen(old, stack)))
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
        minecraft.keyboardHandler.setSendRepeatsToGui(false);
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
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        assert minecraft != null;

        super.render(stack, mouseX, mouseY, partialTicks);

        if (!emptySpec) return;

        drawCenteredString(stack, minecraft.font, translate("block_renderer.gui.emptySpec"), width / 2, height / 6 + 30, 0xFF5555);
    }

    @Override
    public void onRender(Button button) {
        assert minecraft != null;

        if (!renderButton.visible) return;

        minecraft.setScreen(old);
        if (minecraft.level == null) return;

        RenderManager.push(Requests.bulk(text.getValue(), round(size), useId.selected(), addSize.selected()));
    }
}
