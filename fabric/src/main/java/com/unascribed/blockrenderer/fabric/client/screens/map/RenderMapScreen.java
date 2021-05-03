package com.unascribed.blockrenderer.fabric.client.screens.map;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.blockrenderer.fabric.client.render.Requests;
import com.unascribed.blockrenderer.fabric.client.render.manager.RenderManager;
import com.unascribed.blockrenderer.fabric.client.screens.item.EnterSizeScreen;
import com.unascribed.blockrenderer.render.map.MapDecorations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.MapData;
import org.jetbrains.annotations.Nullable;

import static com.unascribed.blockrenderer.fabric.client.varia.Strings.translate;

@SuppressWarnings("NotNullFieldNotInitialized")
public class RenderMapScreen extends EnterSizeScreen {

    private static final TranslationTextComponent TITLE = translate("block_renderer.gui.map");

    @Nullable
    private final MapData data;
    protected Button decorationsButton;
    protected MapDecorations decorations = MapDecorations.DEFAULT;

    public RenderMapScreen(@Nullable Screen old, ItemStack stack) {
        super(TITLE, old, stack, false);

        minecraft = Minecraft.getInstance();
        data = minecraft.level != null ? FilledMapItem.getOrCreateSavedData(stack, minecraft.level) : null;
    }

    @Override
    public void init() {
        super.init();

        decorationsButton = addButton(new Button(width / 2 - 100, height / 6 + 96, 200, 20, translate("block_renderer.gui.map.decorations." + decorations.lowercaseName()), this::toggleDecorations), enabled());
    }

    private void toggleDecorations(Button button) {
        decorations = MapDecorations.byId(decorations.ordinal() + 1);
        decorationsButton.setMessage(
                translate("block_renderer.gui.map.decorations." + decorations.lowercaseName())
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

        drawCenteredString(stack, minecraft.font, translate("block_renderer.gui.noMapData"), width / 2, height / 6 + 30, 0xFF5555);
    }

    @Override
    public void onRender(Button button) {
        assert minecraft != null;

        minecraft.setScreen(old);
        if (minecraft.level == null) return;
        if (data == null) return;

        //TODO: Map Background Image? "textures/map/map_background.png"
        RenderManager.push(Requests.single(stack, data, round(size), useId.selected(), addSize.selected(), decorations));
    }
}
