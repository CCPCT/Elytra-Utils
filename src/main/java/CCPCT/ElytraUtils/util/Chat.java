package CCPCT.ElytraUtils.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import CCPCT.ElytraUtils.config.ModConfig;

public class Chat {
    public static void send(String message) {
        if (ModConfig.get().chatfeedback) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal(message), false);
            }
        }
    }

    public static void colour(String message, String color) {
        if (ModConfig.get().chatfeedback) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal(message).formatted(Formatting.byName(color.toLowerCase())), false);
            }
        }
    }

    public static void debug(String message) {
        if (ModConfig.get().debug) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal(message), false);
            }
        }
    }
}