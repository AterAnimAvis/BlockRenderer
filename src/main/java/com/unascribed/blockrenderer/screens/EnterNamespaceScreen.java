package com.unascribed.blockrenderer.screens;

import com.unascribed.blockrenderer.BlockRenderer;
import com.unascribed.blockrenderer.render.request.item.BulkItemRequest;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import javax.annotation.Nullable;

public class EnterNamespaceScreen extends BaseScreen {

	private static final TranslatableText TITLE = new TranslatableText("block_renderer.gui.namespace");

	private boolean emptySpec = false;

	private final String prefill;

	private TextFieldWidget text;
	
	public EnterNamespaceScreen(@Nullable Screen old, String prefill) {
		super(TITLE, old);
		this.prefill = prefill;
	}
	
	@Override
	public void init() {
		assert client != null;
		client.keyboard.setRepeatEvents(true);

		boolean enabled = client.world != null;

		String oldText = (text == null ? prefill : text.getText());

		text = addButton(new TextFieldWidget(client.textRenderer, width/2-100, height/6+50, 200, 20, new TranslatableText("block_renderer.gui.namespace")), enabled);
		text.setText(oldText);
		text.setSelected(true);
		text.setFocusUnlocked(false);
		setFocused(text);

		super.init();
	}

	@Override
	public void tick() {
		super.tick();
		text.tick();

		emptySpec = text.getText().trim().isEmpty();
		renderButton.visible = !emptySpec;
	}

	@Override
	public void removed() {
		assert client != null;
		client.keyboard.setRepeatEvents(false);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
		assert client != null;

		super.render(matrices, mouseX, mouseY, partialTicks);

		if (!emptySpec) return;

		drawCenteredString(matrices, client.textRenderer, I18n.translate("block_renderer.gui.emptySpec"), width/2, height/6+30, 0xFF5555);
	}

	@Override
	void onRender(AbstractButtonWidget button) {
		assert client != null;
		assert text != null;

		if (!renderButton.visible) return;

		client.openScreen(old);
		if (client.world == null) return;

		BlockRenderer.pendingRequest = new BulkItemRequest(round(size), text.getText(), useId.isChecked(), addSize.isChecked());
	}
}
