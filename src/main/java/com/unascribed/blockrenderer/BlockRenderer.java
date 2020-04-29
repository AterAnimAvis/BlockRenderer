package com.unascribed.blockrenderer;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Mod(BlockRenderer.MODID)
public class BlockRenderer {
	public static final String MODID = "blockrenderer";
	public static final String NAME = "BlockRenderer";
	public static final String VERSION = "1.0.0";

	public static BlockRenderer inst;
	
	protected KeyBinding bind = new KeyBinding("key.render", GLFW.GLFW_KEY_GRAVE_ACCENT, "key.categories.blockrenderer");
	protected boolean down = false;
	protected static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	protected String pendingBulkRender;
	protected int pendingBulkRenderSize;
	
	protected final Logger log = LogManager.getLogger("BlockRenderer");

	public BlockRenderer() {
		inst = this;

		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onPreInit(FMLClientSetupEvent e) {
		ClientRegistry.registerKeyBinding(bind);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent(priority= EventPriority.HIGHEST)
	public void onFrameStart(TickEvent.RenderTickEvent e) {
		/*
		 * Quick primer: OpenGL is double-buffered. This means, where we draw to is
		 * /not/ on the screen. As such, we are free to do whatever we like before
		 * Minecraft renders, as long as we put everything back the way it was.
		 */
		if (e.phase == TickEvent.Phase.START) {
			if (pendingBulkRender != null) {
				// We *must* call render code in pre-render. If we don't, it won't work right.
				bulkRender(pendingBulkRender, pendingBulkRenderSize);
				pendingBulkRender = null;
			}
			if (bind.isKeyDown()) {
				if (!down) {
					down = true;
					Minecraft mc = Minecraft.getInstance();
					Slot hovered = null;
					Screen currentScreen = mc.currentScreen;
					if (currentScreen instanceof ContainerScreen) {
						hovered = ((ContainerScreen<?>) currentScreen).getSlotUnderMouse();
					}
					if (Screen.hasControlDown()) {
						String modid = null;
						if (hovered != null && hovered.getHasStack()) {
							ResourceLocation identifier = ForgeRegistries.ITEMS.getKey(hovered.getStack().getItem());
							if (identifier != null) modid = identifier.getNamespace();
						}
						mc.displayGuiScreen(new GuiEnterModId(mc.currentScreen, modid));
					} else if (currentScreen instanceof ContainerScreen) {
						if (hovered != null) {
							ItemStack is = hovered.getStack();
							if (!is.isEmpty()) {
								int size = 512;
								if (Screen.hasShiftDown()) {
									size = 16 * (int) Minecraft.getInstance().getMainWindow().getGuiScaleFactor();
								}
								setUpRenderState(size);
								mc.ingameGUI.getChatGUI().printChatMessage(new StringTextComponent(render(is, new File("renders"), true)));
								tearDownRenderState();
							} else {
								mc.ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("msg.slot.empty"));
							}
						} else {
							mc.ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("msg.slot.absent"));
						}
					} else {
						mc.ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("msg.notcontainer"));
					}
				}
			} else {
				down = false;
			}
		}
	}

	private void bulkRender(String modidSpec, int size) {
		Minecraft.getInstance().displayGuiScreen(new IngameMenuScreen(false));
		Set<String> modids = Sets.newHashSet();
		for (String str : modidSpec.split(",")) {
			modids.add(str.trim());
		}
		List<ItemStack> toRender = Lists.newArrayList();
		NonNullList<ItemStack> li = NonNullList.create();
		int rendered = 0;
		for (ResourceLocation resloc : ForgeRegistries.ITEMS.getKeys()) {
			if (resloc != null && modids.contains(resloc.getNamespace()) || modids.contains("*")) {
				li.clear();
				Item i = ForgeRegistries.ITEMS.getValue(resloc);

				if (i == null || i == Items.AIR) continue;

				try {
					i.fillItemGroup(ItemGroup.SEARCH, li);
				} catch (Throwable t) {
					log.warn("Failed to get renderable items for "+resloc, t);
				}
				toRender.addAll(li);
			}
		}
		File folder = new File("renders/"+dateFormat.format(new Date())+"_"+sanitize(modidSpec)+"/");
		long lastUpdate = 0;
		String joined = Joiner.on(", ").join(modids);
		setUpRenderState(size);
		for (ItemStack is : toRender) {
			if (InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), GLFW.GLFW_KEY_ESCAPE))
				break;
			render(is, folder, false);
			rendered++;
			if (Util.milliTime()-lastUpdate > 33) {
				tearDownRenderState();
				renderLoading(I18n.format("gui.rendering", toRender.size(), joined),
						I18n.format("gui.progress", rendered, toRender.size(), (toRender.size()-rendered)),
						is, (float)rendered/toRender.size());
				lastUpdate = Util.milliTime();
				setUpRenderState(size);
			}
		}
		if (rendered >= toRender.size()) {
			renderLoading(I18n.format("gui.rendered", toRender.size(), Joiner.on(", ").join(modids)), "", null, 1);
		} else {
			renderLoading(I18n.format("gui.renderCancelled"),
					I18n.format("gui.progress", rendered, toRender.size(), (toRender.size()-rendered)),
					null, (float)rendered/toRender.size());
		}
		tearDownRenderState();
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {}
	}

	private void renderLoading(String title, String subtitle, ItemStack is, float progress) {
		Minecraft mc = Minecraft.getInstance();
		mc.getFramebuffer().unbindFramebuffer();
		RenderSystem.pushMatrix();
			int displayWidth = mc.getMainWindow().getScaledWidth();
			int displayHeight = mc.getMainWindow().getScaledHeight();

			Rendering.setupOverlayRendering();

			// Draw the dirt background and status text...
			Rendering.drawBackground(displayWidth, displayHeight);
			Rendering.drawCenteredString(mc.fontRenderer, title, displayWidth/2, displayHeight/2-24, -1);
			Rendering.drawRect(displayWidth/2-50, displayHeight/2-1, displayWidth/2+50, displayHeight/2+1, 0xFF001100);
			Rendering.drawRect(displayWidth/2-50, displayHeight/2-1, (displayWidth/2-50)+(int)(progress*100), displayHeight/2+1, 0xFF55FF55);
			RenderSystem.pushMatrix();
				RenderSystem.scalef(0.5f, 0.5f, 1);
				Rendering.drawCenteredString(mc.fontRenderer, subtitle, displayWidth, displayHeight-20, -1);
				// ...and draw the tooltip.
				if (is != null) {
					try {
						List<String> list = getTooltipFromItem(is);
			
						// This code is copied from the tooltip renderer, so we can properly center it.
						FontRenderer font = is.getItem().getFontRenderer(is);
						if (font == null) font = mc.fontRenderer;

						int width = 0;
			
						for (String s : list) {
							int j = font.getStringWidth(s);
			
							if (j > width) {
								width = j;
							}
						}
						// End copied code.
						RenderSystem.translatef((displayWidth-width/2f)-12, displayHeight+30, 0);
						Rendering.drawHoveringText(list, 0, 0, font);
					} catch (Throwable ignored) {}
				}
			RenderSystem.popMatrix();
		RenderSystem.popMatrix();

		mc.getMainWindow().flipFrame();

		/*
		 * While OpenGL itself is double-buffered, Minecraft is actually *triple*-buffered.
		 * This is to allow shaders to work, as shaders are only available in "modern" GL.
		 * Minecraft uses "legacy" GL, so it renders using a separate GL context to this
		 * third buffer, which is then flipped to the back buffer with this call.
		 */
		mc.getFramebuffer().bindFramebuffer(false);
	}

	private String render(ItemStack is, File folder, boolean includeDateInFilename) {
		Minecraft mc = Minecraft.getInstance();
		String filename = (includeDateInFilename ? dateFormat.format(new Date())+"_" : "")+sanitize(is.getDisplayName().getUnformattedComponentText());
		RenderSystem.pushMatrix();
			RenderSystem.clearColor(0, 0, 0, 0);
			RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
			mc.getItemRenderer().renderItemAndEffectIntoGUI(is, 0, 0);
		RenderSystem.popMatrix();
		try {
			/*
			 * We need to flip the image over here, because again, GL Y-zero is
			 * the bottom, so it's "Y-up". Minecraft's Y-zero is the top, so it's
			 * "Y-down". Since readPixels is Y-up, our Y-down render is flipped.
			 * It's easier to do this operation on the resulting image than to
			 * do it with GL transforms. Not faster, just easier.
			 */
			BufferedImage img = createFlipped(readPixels(size, size));
			
			File f = new File(folder, filename+".png");
			int i = 2;
			while (f.exists()) {
				f = new File(folder, filename+"_"+i+".png");
				i++;
			}
			Files.createParentDirs(f);
			f.createNewFile();
			ImageIO.write(img, "PNG", f);
			return I18n.format("msg.render.success", f.getPath());
		} catch (Exception ex) {
			ex.printStackTrace();
			return I18n.format("msg.render.fail");
		}
	}
	
	private int size;
	private float oldZLevel;
	
	private void setUpRenderState(int desiredSize) {
		Minecraft mc = Minecraft.getInstance();
		int displayWidth = mc.getMainWindow().getFramebufferWidth();
		int displayHeight = mc.getMainWindow().getFramebufferHeight();
		/*
		 * As we render to the back-buffer, we need to cap our render size
		 * to be within the window's bounds. If we didn't do this, the results
		 * of our readPixels up ahead would be undefined. And nobody likes
		 * undefined behavior.
		 */
		size = Math.min(Math.min(displayHeight, displayWidth), desiredSize);
		
		// Switches from 3D to 2D
		Rendering.setupOverlayRendering();
		RenderHelper.setupGui3DDiffuseLighting();

		/*
		 * The GUI scale affects us due to the call to setupOverlayRendering
		 * above. As such, we need to counteract this to always get a 512x512
		 * render. We could manually switch to orthogonal mode, but it's just
		 * more convenient to leverage setupOverlayRendering.
		 */
		float scale = size/(16f*(float)mc.getMainWindow().getGuiScaleFactor());
		RenderSystem.translatef(0, 0, -(scale*100));

		RenderSystem.scalef(scale, scale, scale);
		
		oldZLevel = mc.getItemRenderer().zLevel;
		mc.getItemRenderer().zLevel = -50;

		RenderSystem.enableRescaleNormal();
		RenderSystem.enableColorMaterial();
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		RenderSystem.disableAlphaTest();
	}
	
	private void tearDownRenderState() {
		RenderSystem.disableLighting();
		RenderSystem.disableColorMaterial();
		RenderSystem.disableDepthTest();
		RenderSystem.disableBlend();
		
		Minecraft.getInstance().getItemRenderer().zLevel = oldZLevel;
	}
	
	private String sanitize(String str) {
		return str.replaceAll("[^A-Za-z0-9-_ ]", "_");
	}

	public BufferedImage readPixels(int width, int height) {
		int displayHeight = Minecraft.getInstance().getMainWindow().getFramebufferHeight();

		// Allocate a native data array to fit our pixels
		ByteBuffer buf = BufferUtils.createByteBuffer(width * height * 4);
		// And finally read the pixel data from the GPU...
		GL11.glReadPixels(0, displayHeight-height, width, height, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, buf);
		// ...and turn it into a Java object we can do things to.
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = new int[width*height];
		buf.asIntBuffer().get(pixels);
		img.setRGB(0, 0, width, height, pixels, 0, width);
		return img;
	}
	
	private static BufferedImage createFlipped(BufferedImage image) {
		AffineTransform at = new AffineTransform();
		/*
		 * Creates a compound affine transform, instead of just one, as we need
		 * to perform two transformations.
		 * 
		 * The first one is to scale the image to 100% width, and -100% height.
		 * (That's *negative* 100%.)
		 */
		at.concatenate(AffineTransform.getScaleInstance(1, -1));
		/**
		 * We then need to translate the image back up by it's height, as flipping
		 * it over moves it off the bottom of the canvas.
		 */
		at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
		return createTransformed(image, at);
	}
	
	private static BufferedImage createTransformed(BufferedImage image, AffineTransform at) {
		// Create a blank image with the same dimensions as the old one...
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		// ...get it's renderer...
		Graphics2D g = newImage.createGraphics();
		/// ...and draw the old image on top of it with our transform.
		g.transform(at);
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}

	private List<String> getTooltipFromItem(ItemStack p_getTooltipFromItem_1_) {
		Minecraft minecraft = Minecraft.getInstance();

		List<ITextComponent> texts = p_getTooltipFromItem_1_.getTooltip(minecraft.player, ITooltipFlag.TooltipFlags.NORMAL);
		List<String> tooltip = Lists.newArrayList();

		for(ITextComponent itextcomponent : texts) tooltip.add(itextcomponent.getFormattedText());

		return tooltip;
	}

}
