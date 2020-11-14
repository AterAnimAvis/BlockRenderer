package com.unascribed.blockrenderer.forge.client.proxy;

import com.unascribed.blockrenderer.forge.client.init.Keybindings;
import com.unascribed.blockrenderer.forge.client.render.RenderManager;
import com.unascribed.blockrenderer.forge.client.render.item.ItemRenderer;
import com.unascribed.blockrenderer.forge.client.screens.SelectionScreen;
import com.unascribed.blockrenderer.forge.client.screens.item.EnterSizeScreen;
import com.unascribed.blockrenderer.forge.client.varia.Registries;
import com.unascribed.blockrenderer.forge.client.varia.StringUtils;
import com.unascribed.blockrenderer.forge.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class ClientProxy extends CommonProxy {

    private static boolean down = false;

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(this);

        Registries.clazzLoad();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onFrameStart(TickEvent.RenderTickEvent e) {
        if (e.phase != TickEvent.Phase.START) return;
        
        onFrameStart();
    }

    public static void onFrameStart() {
        RenderManager.onFrameStart();

        if (!isKeyDown()) {
            down = false;
            return;
        }

        if (down) return;
        down = true;

        Minecraft client = Minecraft.getInstance();
        Slot hovered = null;
        Screen currentScreen = client.screen;
        boolean isContainerScreen = currentScreen instanceof ContainerScreen<?>;

        // Intellij Bork-ing wants explicit null check.
        if (currentScreen != null && isContainerScreen) hovered = ((ContainerScreen<?>) currentScreen).getSlotUnderMouse();

        if (Screen.hasControlDown()) {
            PlayerEntity player = client.player;

            ItemStack input = hovered != null && hovered.hasItem() ? hovered.getItem() : null;
            if (input == null && player != null) input = player.getMainHandItem();

            client.setScreen(new SelectionScreen(client.screen, input));
            return;
        }

        if (!isContainerScreen) {
            PlayerEntity player = client.player;

            if (player != null && !player.getMainHandItem().isEmpty()) {
                renderStack(player.getMainHandItem());
                return;
            }
            StringUtils.addMessage(new TranslationTextComponent("msg.block_renderer.notContainer"));
            return;
        }

        if (hovered == null) {
            StringUtils.addMessage(new TranslationTextComponent("msg.block_renderer.slot.absent"));
            return;
        }

        ItemStack stack = hovered.getItem();

        if (stack.isEmpty()) {
            StringUtils.addMessage(new TranslationTextComponent("msg.block_renderer.slot.empty"));
            return;
        }

        renderStack(stack);
    }

    private static void renderStack(ItemStack stack) {
        Minecraft client = Minecraft.getInstance();

        if (Screen.hasShiftDown()) {
            client.setScreen(new EnterSizeScreen(client.screen, stack));
            return;
        }

        RenderManager.push(ItemRenderer.single(stack, 512, false, false));
    }

    private static boolean isKeyDown() {
        Minecraft client = Minecraft.getInstance();
        Screen currentScreen = client.screen;

        /* Unbound key */
        if (Keybindings.render.isUnbound()) return false;

        /* Has the Keybinding been triggered? */
        if (Keybindings.render.isDown()) return true;

        /* Not in Screen so we should be ok */
        if (currentScreen == null) return false;

        /* Non Containers seem to behave ok */
        boolean hasSlots = currentScreen instanceof ContainerScreen<?>;
        if (!hasSlots) return false;

        /* TextFieldWidgets */
        if (currentScreen.getFocused() instanceof TextFieldWidget) return false;

        /* Recipe Books */
        if (currentScreen instanceof IRecipeShownListener) {
            RecipeBookGui recipeBook = ((IRecipeShownListener) currentScreen).getRecipeBookComponent();
            if (recipeBook.isVisible()) return false;
        }

        /* Actually Check to see if the key is down */
        InputMappings.Input key = Keybindings.render.getKey();

        if (key.getType() == InputMappings.Type.MOUSE) {
            return GLFW.glfwGetMouseButton(client.getWindow().getWindow(), key.getValue()) == GLFW.GLFW_PRESS;
        }

        return InputMappings.isKeyDown(client.getWindow().getWindow(), key.getValue());
    }

}
