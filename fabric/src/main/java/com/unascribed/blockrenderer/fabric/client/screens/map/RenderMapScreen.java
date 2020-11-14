package com.unascribed.blockrenderer.fabric.client.screens.map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unascribed.blockrenderer.fabric.client.render.RenderManager;
import com.unascribed.blockrenderer.fabric.client.render.item.ItemRenderer;
import com.unascribed.blockrenderer.fabric.client.screens.item.EnterSizeScreen;
import com.unascribed.blockrenderer.render.map.MapDecorations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("NotNullFieldNotInitialized")
public class RenderMapScreen extends EnterSizeScreen {

    private static final TranslatableComponent TITLE = new TranslatableComponent("block_renderer.gui.map");

    @Nullable
    private final MapItemSavedData data;
    protected Button decorationsButton;
    protected MapDecorations decorations = MapDecorations.DEFAULT;

    public RenderMapScreen(@Nullable Screen old, ItemStack stack) {
        super(TITLE, old, stack, false);

        minecraft = Minecraft.getInstance();
        data = minecraft.level != null ? MapItem.getSavedData(stack, minecraft.level) : null;
    }

    @Override
    public void init() {
        super.init();

        decorationsButton = addButton(new Button(width / 2 - 100, height / 6 + 96, 200, 20, new TranslatableComponent("block_renderer.gui.map.decorations." + decorations.lowercaseName()), this::toggleDecorations), enabled());
    }

    private void toggleDecorations(Button button) {
        decorations = MapDecorations.byId(decorations.ordinal() + 1);
        decorationsButton.setMessage(
                new TranslatableComponent("block_renderer.gui.map.decorations." + decorations.lowercaseName())
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
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        assert minecraft != null;

        super.render(stack, mouseX, mouseY, partialTicks);

        if (minecraft.level == null) return;
        if (data != null) return;

        drawCenteredString(stack, minecraft.font, new TranslatableComponent("block_renderer.gui.noMapData"), width / 2, height / 6 + 30, 0xFF5555);
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
