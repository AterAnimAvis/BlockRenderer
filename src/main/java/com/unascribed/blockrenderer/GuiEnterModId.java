package com.unascribed.blockrenderer;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class GuiEnterModId extends GuiScreen {
	private GuiTextField text;
	private GuiScreen old;
	
	public GuiEnterModId(GuiScreen old) {
		this.old = old;
	}
	
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		text = new GuiTextField(0, mc.fontRendererObj, width/2-100, height/6+50, 200, 20);
		buttonList.add(new GuiButton(2, width/2-100, height/6+100, 98, 20, I18n.format("gui.cancel")));
		GuiButton render = new GuiButton(1, width/2+2, height/6+100, 98, 20, I18n.format("gui.render"));
		buttonList.add(render);
		text.setFocused(true);
		text.setCanLoseFocus(false);
		render.enabled = mc.theWorld != null;
		text.setEnabled(mc.theWorld != null);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		drawCenteredString(mc.fontRendererObj, I18n.format("gui.entermodid"), width/2, height/6, -1);
		if (mc.theWorld == null) {
			drawCenteredString(mc.fontRendererObj, I18n.format("gui.noworld"), width/2, height/6+30, 0xFF5555);
		}
		text.drawTextBox();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if (button.id == 1) {
			if (mc.theWorld != null) {
				BlockRenderer.inst.pendingBulkRender = text.getText();
			}
			mc.displayGuiScreen(old);
		} else if (button.id == 2) {
			mc.displayGuiScreen(old);
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		text.updateCursorCounter();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		text.textboxKeyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		text.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}
}