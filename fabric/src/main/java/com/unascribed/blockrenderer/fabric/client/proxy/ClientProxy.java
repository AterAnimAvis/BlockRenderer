package com.unascribed.blockrenderer.fabric.client.proxy;

import com.unascribed.blockrenderer.fabric.client.init.Keybindings;
import com.unascribed.blockrenderer.fabric.client.render.RenderManager;
import com.unascribed.blockrenderer.fabric.client.render.Requests;
import com.unascribed.blockrenderer.fabric.client.screens.SelectionScreen;
import com.unascribed.blockrenderer.fabric.client.screens.item.EnterSizeScreen;
import com.unascribed.blockrenderer.fabric.client.varia.StringUtils;
import com.unascribed.blockrenderer.fabric.mixin.accessor.IHoveredSlot;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
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
import org.lwjgl.glfw.GLFW;

public class ClientProxy {

    private static boolean down = false;

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
        Screen currentScreen = client.currentScreen;
        boolean isContainerScreen = currentScreen instanceof IHoveredSlot;

        // Intellij Bork-ing wants explicit null check.
        if (currentScreen != null && isContainerScreen) hovered = ((IHoveredSlot) currentScreen).block_renderer$accessor$hoveredSlot();

        if (Screen.hasControlDown()) {
            PlayerEntity player = client.player;

            ItemStack input = hovered != null && hovered.getHasStack() ? hovered.getStack() : null;
            if (input == null && player != null) input = player.getHeldItemMainhand();

            client.displayGuiScreen(new SelectionScreen(client.currentScreen, input));
            return;
        }

        if (!isContainerScreen) {
            PlayerEntity player = client.player;

            if (player != null && !player.getHeldItemMainhand().isEmpty()) {
                renderStack(player.getHeldItemMainhand());
                return;
            }
            StringUtils.addMessage(new TranslationTextComponent("msg.block_renderer.notContainer"));
            return;
        }

        if (hovered == null) {
            StringUtils.addMessage(new TranslationTextComponent("msg.block_renderer.slot.absent"));
            return;
        }

        ItemStack stack = hovered.getStack();

        if (stack.isEmpty()) {
            StringUtils.addMessage(new TranslationTextComponent("msg.block_renderer.slot.empty"));
            return;
        }

        renderStack(stack);
    }

    private static void renderStack(ItemStack stack) {
        Minecraft client = Minecraft.getInstance();

        if (Screen.hasShiftDown()) {
            client.displayGuiScreen(new EnterSizeScreen(client.currentScreen, stack));
            return;
        }

        RenderManager.push(Requests.single(stack, 512, false, false));
    }

    private static boolean isKeyDown() {
        Minecraft client = Minecraft.getInstance();
        Screen currentScreen = client.currentScreen;

        /* Unbound key */
        if (Keybindings.render.isInvalid()) return false;

        /* Has the Keybinding been triggered? */
        if (Keybindings.render.isPressed()) return true;

        /* Not in Screen so we should be ok */
        if (currentScreen == null) return false;

        /* Non Containers seem to behave ok */
        boolean hasSlots = currentScreen instanceof ContainerScreen<?>;
        if (!hasSlots) return false;

        /* TextFieldWidgets */
        if (currentScreen.getListener() instanceof TextFieldWidget) return false;

        /* Recipe Books */
        if (currentScreen instanceof IRecipeShownListener) {
            RecipeBookGui recipeBook = ((IRecipeShownListener) currentScreen).getRecipeGui();
            if (recipeBook.isVisible()) return false;
        }

        /* Actually Check to see if the key is down */
        InputMappings.Input key = KeyBindingHelper.getBoundKeyOf(Keybindings.render);

        if (key.getType() == InputMappings.Type.MOUSE) {
            return GLFW.glfwGetMouseButton(client.getMainWindow().getHandle(), key.getKeyCode()) == GLFW.GLFW_PRESS;
        }

        return InputMappings.isKeyDown(client.getMainWindow().getHandle(), key.getKeyCode());
    }

}
