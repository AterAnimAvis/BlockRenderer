package com.unascribed.blockrenderer.fabric.client.screens.map;

import com.unascribed.blockrenderer.fabric.client.render.RenderManager;
import com.unascribed.blockrenderer.fabric.client.render.item.ItemRenderer;
import com.unascribed.blockrenderer.fabric.client.render.map.MapDecorations;
import com.unascribed.blockrenderer.fabric.client.screens.item.EnterSizeScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("NotNullFieldNotInitialized")
public class RenderMapScreen extends EnterSizeScreen {

    private static final TranslatableText TITLE = new TranslatableText("block_renderer.gui.map");

    @Nullable
    private final MapState data;
    protected ButtonWidget decorationsButton;
    protected MapDecorations decorations = MapDecorations.DEFAULT;

    public RenderMapScreen(@Nullable Screen old, ItemStack stack) {
        super(TITLE, old, stack, false);

        client = MinecraftClient.getInstance();
        data = client.world != null ? FilledMapItem.getMapState(stack, client.world) : null;
    }

    @Override
    public void init() {
        super.init();

        decorationsButton = addButton(new ButtonWidget(width / 2 - 100, height / 6 + 96, 200, 20, new TranslatableText("block_renderer.gui.map.decorations." + decorations.lowercaseName()), this::toggleDecorations), enabled());
    }

    private void toggleDecorations(ButtonWidget button) {
        decorations = MapDecorations.byId(decorations.ordinal() + 1);
        decorationsButton.setMessage(
                new TranslatableText("block_renderer.gui.map.decorations." + decorations.lowercaseName())
        );
    }

    @Override
    protected boolean enabled() {
        return super.enabled() && data != null;
    }

    @Override
    protected int getMinSize() {
        return 128;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        assert client != null;

        super.render(stack, mouseX, mouseY, partialTicks);

        if (client.world == null) return;
        if (data != null) return;

        drawCenteredText(stack, client.textRenderer, new TranslatableText("block_renderer.gui.noMapData"), width / 2, height / 6 + 30, 0xFF5555);
    }

    @Override
    public void onRender(ButtonWidget button) {
        assert client != null;

        client.openScreen(old);
        if (client.world == null) return;
        if (data == null) return;

        //TODO: Map Background Image? "textures/map/map_background.png"
        RenderManager.push(ItemRenderer.single(stack, data, round(size), useId.isChecked(), addSize.isChecked(), decorations));
    }
}
