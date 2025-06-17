package CCPCT.ElytraUtils.client;

import static CCPCT.ElytraUtils.config.ModConfig.load;

import CCPCT.ElytraUtils.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

import CCPCT.ElytraUtils.util.*;
import CCPCT.ElytraUtils.config.configScreen;

import java.util.Objects;

public class ElytraUtilsClient implements ClientModInitializer {
    public static KeyBinding swapElytraKey;
    public static KeyBinding configScreenKey;
    public static KeyBinding endFlightKey;
    public static KeyBinding quickFireworkKey;
    private static boolean lastJumpKeyDown = false;
    private static boolean jumpKeyDown = false;
    private static boolean lastGliding = false;
    private static boolean gliding = false;
    private static boolean lastFirework = false;
    private static boolean firework = false;
    private static boolean alerted = false;

    @Override
    public void onInitializeClient() {

        load();
        // Register the keybinding
        swapElytraKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Swap elytra", // translation key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,       // default key
                "Elytra Utils"       // category in controls menu
        ));

        configScreenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Elytra Utils Config screen", // translation key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,       // default key
                "Elytra Utils"       // category in controls menu
        ));

        endFlightKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "End flight", // translation key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_SPACE,       // default key
                "Elytra Utils"       // category in controls menu
        ));

        quickFireworkKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Quick firework", // translation key
                InputUtil.Type.MOUSE,
                GLFW.GLFW_DONT_CARE,       // default key
                "Elytra Utils"       // category in controls menu
        ));

        // Register client tick listener


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            // swap totem
            if (swapElytraKey.wasPressed()) {
                Logic.swapElytra();
            }
            // open config
            if (configScreenKey.wasPressed()) {
                MinecraftClient.getInstance().setScreen(configScreen.getConfigScreen(MinecraftClient.getInstance().currentScreen));
            }



            gliding = client.player.isGliding();
            jumpKeyDown = client.options.jumpKey.isPressed();
            if (Objects.equals(endFlightKey.getBoundKeyTranslationKey(),"key.keyboard.space")&&(lastGliding && gliding && jumpKeyDown && !lastJumpKeyDown)||endFlightKey.wasPressed()) {
                Chat.send("Ended flight");
                Packets.clickItem(6, ItemStack.EMPTY);
                Packets.clickItem(6, Logic.getItemStack(38));
            }
            lastJumpKeyDown = jumpKeyDown;
            lastGliding = gliding;

            firework = client.options.pickItemKey.isPressed();
            if (ModConfig.get().middleClickQuickFirework) {
                if (firework && !lastFirework) {
                    Logic.quickFirework();
                }
            } else if (quickFireworkKey.wasPressed()){
                Logic.quickFirework();
            }
            lastFirework = firework;

            if (ModConfig.get().durabilityAlert){
                ItemStack item = client.player.getInventory().armor.get(2);
                if (item.getItem()==Items.ELYTRA && item.getMaxDamage()-item.getDamage()<=9){
                    if (ModConfig.get().replaceBreakingElytra&&Logic.getElytraSpot() != -1){
                        Chat.colour("Replacing breaking elytra","yellow");
                        Logic.swapElytra();
                        alerted = true;
                    } else if (!alerted) {
                        Chat.colour("Elytra breaking!", "red");
                        alerted = true;
                    }
                } else {
                    alerted = false;
                }
            }
        });
    }
}

