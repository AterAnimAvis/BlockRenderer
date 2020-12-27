package com.unascribed.blockrenderer.client.proxy;

import com.unascribed.blockrenderer.client.init.Keybindings;
import com.unascribed.blockrenderer.client.render.RenderManager;
import com.unascribed.blockrenderer.client.render.entity.EntityRequest;
import com.unascribed.blockrenderer.client.render.item.ItemRenderer;
import com.unascribed.blockrenderer.client.screens.SelectionScreen;
import com.unascribed.blockrenderer.client.screens.item.EnterSizeScreen;
import com.unascribed.blockrenderer.client.varia.Registries;
import com.unascribed.blockrenderer.proxy.CommonProxy;
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

import static com.unascribed.blockrenderer.client.varia.StringUtils.addMessage;

public class ClientProxy extends CommonProxy {

    private boolean down = false;

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(this);

        Registries.clazzLoad();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onFrameStart(TickEvent.RenderTickEvent e) {
        if (e.phase != TickEvent.Phase.START) return;

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
        boolean isContainerScreen = currentScreen instanceof ContainerScreen<?>;

        // Intellij Bork-ing wants explicit null check.
        if (currentScreen != null && isContainerScreen) hovered = ((ContainerScreen<?>) currentScreen).getSlotUnderMouse();

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

            if (client.pointedEntity != null) {
                RenderManager.push(EntityRequest.single(client.pointedEntity, 512));
                return;
            }

            addMessage(new TranslationTextComponent("msg.block_renderer.notContainer"));
            return;
        }

        if (hovered == null) {
            addMessage(new TranslationTextComponent("msg.block_renderer.slot.absent"));
            return;
        }

        ItemStack stack = hovered.getStack();

        if (stack.isEmpty()) {
            addMessage(new TranslationTextComponent("msg.block_renderer.slot.empty"));
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

        RenderManager.push(ItemRenderer.single(stack, 512, false, false));
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
        InputMappings.Input key = Keybindings.render.getKey();

        if (key.getType() == InputMappings.Type.MOUSE) {
            return GLFW.glfwGetMouseButton(client.getMainWindow().getHandle(), key.getKeyCode()) == GLFW.GLFW_PRESS;
        }

        return InputMappings.isKeyDown(client.getMainWindow().getHandle(), key.getKeyCode());
    }

}
