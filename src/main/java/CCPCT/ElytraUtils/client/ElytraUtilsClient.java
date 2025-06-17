package CCPCT.ElytraUtils.client;

import static CCPCT.ElytraUtils.config.ModConfig.load;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import CCPCT.ElytraUtils.util.Packets;

import org.lwjgl.glfw.GLFW;

import CCPCT.ElytraUtils.util.*;
import CCPCT.ElytraUtils.config.configScreen;

public class ElytraUtilsClient implements ClientModInitializer {
    public static KeyBinding swapElytraKey;
    public static KeyBinding configScreenKey;

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
                "Config screen", // translation key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,       // default key
                "Elytra Utils"       // category in controls menu
        ));

        // Register client tick listener
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (swapElytraKey.wasPressed()) {
                // swap totem
                IngameChat.sendChat("Switching totem");
                Packets.swapUseItems(11);
                //Packets.swapItems(9,10);
            }

            if (configScreenKey.wasPressed()) {
                // open config
                MinecraftClient.getInstance().setScreen(configScreen.getConfigScreen(MinecraftClient.getInstance().currentScreen));
            }
        });
    }
}

