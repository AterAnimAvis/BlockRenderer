package com.unascribed.blockrenderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Static versions of Gui and GuiScreen utility methods.
 *
 */
public class Rendering {
	private static class DummyScreen extends Screen {

		protected DummyScreen() {
			super(new StringTextComponent("BlockRenderer Dummy Screen"));
		}

		@Override
		public List<String> getTooltipFromItem(ItemStack stack) {
			return super.getTooltipFromItem(stack);
		}
	}

	private static final DummyScreen GUI = new DummyScreen();
	
	
	public static void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
		GUI.drawCenteredString(fontRendererIn, text, x, y, color);
	}
	
	public static void drawRect(int left, int top, int right, int bottom, int color) {
		AbstractGui.fill(left, top, right, bottom, color);
	}
	
	public static void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font) {
		GUI.renderTooltip(textLines, x, y, font);
	}
	
	public static void drawBackground(int width, int height) {
		GUI.init(Minecraft.getInstance(), width, height);
		GUI.renderDirtBackground(0);
	}

	protected static void setupOverlayRendering() {
		Minecraft mc = Minecraft.getInstance();
		MainWindow mainwindow = mc.getMainWindow();
		double scaleFactor = mainwindow.getGuiScaleFactor();

		RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
		RenderSystem.matrixMode(GL11.GL_PROJECTION);
		RenderSystem.loadIdentity();
		RenderSystem.ortho(0.0D, (double)mainwindow.getFramebufferWidth() / scaleFactor, (double)mainwindow.getFramebufferHeight() / scaleFactor, 0.0D, 1000.0D, 3000.0D);
		RenderSystem.matrixMode(GL11.GL_MODELVIEW);
		RenderSystem.loadIdentity();
		RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
	}
	
	
	private Rendering() {}
}
