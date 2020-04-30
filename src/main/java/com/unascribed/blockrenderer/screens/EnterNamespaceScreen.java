package com.unascribed.blockrenderer.screens;

import com.unascribed.blockrenderer.BlockRenderer;
import com.unascribed.blockrenderer.render.request.BulkRequest;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.TranslatableText;

import javax.annotation.Nullable;

public class EnterNamespaceScreen extends BaseScreen {

	private static final TranslatableText TITLE = new TranslatableText("blockrenderer.gui.namespace");

	private final String prefill;

	private @Nullable TextFieldWidget text;
	
	public EnterNamespaceScreen(@Nullable Screen old, String prefill) {
		super(TITLE, old);
		this.prefill = prefill;
	}
	
	@Override
	public void init() {
		assert client != null;
		client.keyboard.enableRepeatEvents(true);

		boolean enabled = client.world != null;

		String oldText = (text == null ? prefill : text.getText());

		text = addButton(new TextFieldWidget(client.textRenderer, width/2-100, height/6+50, 200, 20, new TranslatableText("blockrenderer.gui.namespace")), enabled);
		text.setText(oldText);
		text.setSelected(true);
		text.setFocusUnlocked(false);
		setFocused(text);

		super.init();
	}

	@Override
	public void tick() {
		if (text != null) text.tick();
	}

	@Override
	public void removed() {
		assert client != null;
		client.keyboard.enableRepeatEvents(false);
	}

	@Override
	void onRender(AbstractButtonWidget button) {
		assert client != null;
		assert text != null;

		client.openScreen(old);
		if (client.world == null) return;

		BlockRenderer.pendingRequest = new BulkRequest(round(size), text.getText());
	}
}
