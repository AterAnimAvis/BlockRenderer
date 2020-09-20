package com.unascribed.blockrenderer.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.blockrenderer.BlockRenderer;
import com.unascribed.blockrenderer.render.request.item.BulkItemRequest;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class EnterNamespaceScreen extends BaseScreen {

	private static final TranslationTextComponent TITLE = new TranslationTextComponent("block_renderer.gui.namespace");

	private boolean emptySpec = false;

	private final String prefill;

	private TextFieldWidget text;
	
	public EnterNamespaceScreen(@Nullable Screen old, String prefill) {
		super(TITLE, old);
		this.prefill = prefill;
	}
	
	@Override
	public void init() {
		assert minecraft != null;
		minecraft.keyboardListener.enableRepeatEvents(true);

		boolean enabled = minecraft.world != null;

		String oldText = (text == null ? prefill : text.getText());

		text = addButton(new TextFieldWidget(minecraft.fontRenderer, width/2-100, height/6+50, 200, 20, new TranslationTextComponent("block_renderer.gui.namespace")), enabled);
		text.setResponder((value) -> emptySpec = value.trim().isEmpty());
		text.setText(oldText);
		text.setCanLoseFocus(false);
		setFocusedDefault(text);

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
	public boolean mouseClicked(double mouseX, double mouseY, int p_231044_5_) {
		if (text.mouseClicked(mouseX, mouseY, p_231044_5_)) return true;

		return super.mouseClicked(mouseX, mouseY, p_231044_5_);
	}

	@Override
	public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
		assert minecraft != null;

		super.render(stack, mouseX, mouseY, partialTicks);

		if (!emptySpec) return;

		drawCenteredString(stack, minecraft.fontRenderer, new TranslationTextComponent("block_renderer.gui.emptySpec"), width/2, height/6+30, 0xFF5555);
	}

	@Override
	void onRender(Button button) {
		assert minecraft != null;

		if (!renderButton.visible) return;

		minecraft.displayGuiScreen(old);
		if (minecraft.world == null) return;

		BlockRenderer.proxy.render(new BulkItemRequest(round(size), text.getText(), useId.isChecked(), addSize.isChecked()));
	}
}
