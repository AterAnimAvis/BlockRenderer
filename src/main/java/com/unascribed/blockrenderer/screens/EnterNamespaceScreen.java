package com.unascribed.blockrenderer.screens;

import com.unascribed.blockrenderer.BlockRenderer;
import com.unascribed.blockrenderer.render.request.BulkRequest;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class EnterNamespaceScreen extends BaseScreen {

	private static final TranslationTextComponent TITLE = new TranslationTextComponent("blockrenderer.gui.namespace");

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

		text = addButton(new TextFieldWidget(minecraft.fontRenderer, width/2-100, height/6+50, 200, 20, I18n.format("blockrenderer.gui.namespace")), enabled);
		text.setText(oldText);
		text.setFocused2(true);
		text.setCanLoseFocus(false);
		setFocusedDefault(text);

		super.init();
	}

	@Override
	public void tick() {
		super.tick();
		text.tick();
	}

	@Override
	public void removed() {
		assert minecraft != null;
		minecraft.keyboardListener.enableRepeatEvents(false);
	}

	@Override
	void onRender(Button button) {
		assert minecraft != null;

		minecraft.displayGuiScreen(old);
		if (minecraft.world == null) return;

		BlockRenderer.pendingRequest = new BulkRequest(round(size), text.getText(), useId.isChecked(), addSize.isChecked());
	}
}
