package com.unascribed.blockrenderer.forge.client.screens.map;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.blockrenderer.forge.client.render.RenderManager;
import com.unascribed.blockrenderer.forge.client.render.item.ItemRenderer;
import com.unascribed.blockrenderer.forge.client.render.map.MapDecorations;
import com.unascribed.blockrenderer.forge.client.screens.item.EnterSizeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.MapData;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("NotNullFieldNotInitialized")
public class RenderMapScreen extends EnterSizeScreen {

    private static final TranslationTextComponent TITLE = new TranslationTextComponent("block_renderer.gui.map");

    @Nullable
    private final MapData data;
    protected Button decorationsButton;
    protected MapDecorations decorations = MapDecorations.DEFAULT;

    public RenderMapScreen(@Nullable Screen old, ItemStack stack) {
        super(TITLE, old, stack, false);

        minecraft = Minecraft.getInstance();
        data = minecraft.level != null ? FilledMapItem.getSavedData(stack, minecraft.level) : null;
    }

    @Override
    public void init() {
        super.init();

        decorationsButton = addButton(new Button(width / 2 - 100, height / 6 + 96, 200, 20, new TranslationTextComponent("block_renderer.gui.map.decorations." + decorations.lowercaseName()), this::toggleDecorations), enabled());
    }

    private void toggleDecorations(Button button) {
        decorations = MapDecorations.byId(decorations.ordinal() + 1);
        decorationsButton.setMessage(
                new TranslationTextComponent("block_renderer.gui.map.decorations." + decorations.lowercaseName())
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
        assert minecraft != null;

        super.render(stack, mouseX, mouseY, partialTicks);

        if (minecraft.level == null) return;
        if (data != null) return;

        drawCenteredString(stack, minecraft.font, new TranslationTextComponent("block_renderer.gui.noMapData"), width / 2, height / 6 + 30, 0xFF5555);
    }

    @Override
    public void onRender(Button button) {
        assert minecraft != null;

        minecraft.setScreen(old);
        if (minecraft.level == null) return;
        if (data == null) return;

        //TODO: Map Background Image? "textures/map/map_background.png"
        RenderManager.push(ItemRenderer.single(stack, data, round(size), useId.selected(), addSize.selected(), decorations));
    }
}
