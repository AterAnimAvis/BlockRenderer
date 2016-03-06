package com.unascribed.blockrenderer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

@Mod(modid=BlockRenderer.MODID,name=BlockRenderer.NAME,version=BlockRenderer.VERSION,acceptableRemoteVersions="*",acceptableSaveVersions="*",clientSideOnly=true)
public class BlockRenderer {
	public static final String MODID = "blockrenderer";
	public static final String NAME = "BlockRenderer";
	public static final String VERSION = "0.3.1";
	
	@Instance
	public static BlockRenderer inst;
	
	protected KeyBinding bind;
	protected boolean down = false;
	protected static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	protected String pendingBulkRender;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		bind = new KeyBinding("key.render", Keyboard.KEY_GRAVE, "key.categories.blockrenderer");
		ClientRegistry.registerKeyBinding(bind);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void onFrameStart(RenderTickEvent e) {
		if (e.phase == Phase.START) {
			if (pendingBulkRender != null) {
				bulkRender(pendingBulkRender);
				pendingBulkRender = null;
			}
			int code = bind.getKeyCode();
			if (code > 256) {
				return;
			}
			if (Keyboard.isKeyDown(code)) {
				if (!down) {
					down = true;
					Minecraft mc = Minecraft.getMinecraft();
					if (GuiScreen.isCtrlKeyDown()) {
						mc.displayGuiScreen(new GuiEnterModId(mc.currentScreen));
					} else {
						GuiScreen currentScreen = mc.currentScreen;
						if (currentScreen instanceof GuiContainer) {
							int w = currentScreen.width;
							int h = currentScreen.height;
							final int x = Mouse.getX() * w / mc.displayWidth;
							final int y = h - Mouse.getY() * h / mc.displayHeight - 1;
							Slot s = ((GuiContainer)currentScreen).getSlotAtPosition(x, y);
							if (s != null) {
								ItemStack is = s.getStack();
								if (is != null) {
									mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(render(is, new File("renders"), true)));
								} else {
									mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation("msg.slot.empty"));
								}
							} else {
								mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation("msg.slot.absent"));
							}
						} else {
							mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation("msg.notcontainer"));
						}
					}
				}
			} else {
				down = false;
			}
		}
	}

	private void bulkRender(String modidSpec) {
		Set<String> modids = Sets.newHashSet();
		for (String str : modidSpec.split(",")) {
			modids.add(str.trim());
		}
		List<ItemStack> toRender = Lists.newArrayList();
		List<ItemStack> li = Lists.newArrayList();
		int rendered = 0;
		for (ResourceLocation resloc : Item.itemRegistry.getKeys()) {
			if (resloc != null && modids.contains(resloc.getResourceDomain())) {
				li.clear();
				Item i = Item.itemRegistry.getObject(resloc);
				try {
					i.getSubItems(i, i.getCreativeTab(), li);
				} catch (Throwable t) {}
				toRender.addAll(li);
			}
		}
		toRender.add(new ItemStack(Blocks.chest));
		File folder = new File("renders/"+dateFormat.format(new Date())+"_"+sanitize(modidSpec)+"/");
		long lastUpdate = 0;
		for (ItemStack is : toRender) {
			render(is, folder, false);
			rendered++;
			if (Minecraft.getSystemTime()-lastUpdate > 33) {
				renderLoading(I18n.format("gui.rendering", toRender.size(), Joiner.on(", ").join(modids)), I18n.format("gui.progress", rendered, toRender.size(), (toRender.size()-rendered)), is, (float)rendered/toRender.size());
				lastUpdate = Minecraft.getSystemTime();
			}
		}
		renderLoading(I18n.format("gui.rendered", toRender.size(), Joiner.on(", ").join(modids)), "", null, 1);
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {}
	}

	private void renderLoading(String title, String subtitle, ItemStack is, float progress) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.getFramebuffer().unbindFramebuffer();
		GlStateManager.pushMatrix();
			ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
			mc.entityRenderer.setupOverlayRendering();
			Rendering.drawBackground(res.getScaledWidth(), res.getScaledHeight());
			Rendering.drawCenteredString(mc.fontRendererObj, title, res.getScaledWidth()/2, res.getScaledHeight()/2-24, -1);
			Rendering.drawRect(res.getScaledWidth()/2-50, res.getScaledHeight()/2-1, res.getScaledWidth()/2+50, res.getScaledHeight()/2+1, 0xFF001100);
			Rendering.drawRect(res.getScaledWidth()/2-50, res.getScaledHeight()/2-1, (res.getScaledWidth()/2-50)+(int)(progress*100), res.getScaledHeight()/2+1, 0xFF55FF55);
			GlStateManager.pushMatrix();
				GlStateManager.scale(0.5f, 0.5f, 1);
				Rendering.drawCenteredString(mc.fontRendererObj, subtitle, res.getScaledWidth(), res.getScaledHeight()-20, -1);
				if (is != null) {
					try {
						List<String> list = is.getTooltip(mc.thePlayer, true);
			
						for (int i = 0; i < list.size(); ++i) {
							if (i == 0) {
								list.set(i, is.getRarity().rarityColor + list.get(i));
							} else {
								list.set(i, EnumChatFormatting.GRAY + list.get(i));
							}
						}
			
						FontRenderer font = is.getItem().getFontRenderer(is);
						if (font == null) {
							font = mc.fontRendererObj;
						}
						int width = 0;
			
						for (String s : list) {
							int j = font.getStringWidth(s);
			
							if (j > width) {
								width = j;
							}
						}
						GlStateManager.translate((res.getScaledWidth()-width/2)-12, res.getScaledHeight()+30, 0);
						Rendering.drawHoveringText(list, 0, 0, font);
					} catch (Throwable t) {}
				}
			GlStateManager.popMatrix();
		GlStateManager.popMatrix();
		mc.updateDisplay();
		mc.getFramebuffer().bindFramebuffer(false);
	}

	private String render(ItemStack is, File folder, boolean includeDateInFilename) {
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution res = new ScaledResolution(mc);
		int size;
		if (GuiScreen.isShiftKeyDown()) {
			size = 16*res.getScaleFactor();
		} else {
			size = Math.min(Math.min(mc.displayHeight, mc.displayWidth), 512);
		}
		String filename = (includeDateInFilename ? dateFormat.format(new Date())+"_" : "")+sanitize(is.getDisplayName())+".png";
		GlStateManager.pushMatrix();
			GlStateManager.clearColor(0, 0, 0, 0);
			GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			mc.entityRenderer.setupOverlayRendering();
			RenderHelper.enableGUIStandardItemLighting();
			float scale = size/(16f*res.getScaleFactor());
			GlStateManager.translate(0, 0, -(scale*100));
			
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(0, 0, -50);
			
			GlStateManager.enableDepth();
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.enableRescaleNormal();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			mc.renderItem.renderItemAndEffectIntoGUI(is, 0, 0);
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
			GlStateManager.disableAlpha();
			GlStateManager.disableDepth();
		GlStateManager.popMatrix();
		try {
			BufferedImage img = createFlipped(readPixels(size, size));
			
			File f = new File(folder, filename);
			Files.createParentDirs(f);
			f.createNewFile();
			ImageIO.write(img, "PNG", f);
			return I18n.format("msg.render.success", f.getPath());
		} catch (Exception ex) {
			ex.printStackTrace();
			return I18n.format("msg.render.fail");
		}
	}
	
	private String sanitize(String str) {
		return str.replaceAll("[^A-Za-z0-9-_]", "_");
	}

	public BufferedImage readPixels(int width, int height) throws InterruptedException {
		GL11.glReadBuffer(GL11.GL_BACK);
		ByteBuffer buf = BufferUtils.createByteBuffer(width * height * 4);
		GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, buf);
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = new int[width*height];
		buf.asIntBuffer().get(pixels);
		img.setRGB(0, 0, width, height, pixels, 0, width);
		return img;
	}
	
	private static BufferedImage createFlipped(BufferedImage image) {
		AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getScaleInstance(1, -1));
		at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
		return createTransformed(image, at);
	}
	
	private static BufferedImage createTransformed(BufferedImage image, AffineTransform at) {
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		g.transform(at);
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}

}
