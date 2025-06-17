package CCPCT.ElytraUtils.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import CCPCT.ElytraUtils.config.ModConfig;

public class IngameChat {
    public static void sendChat(String message) {
        if (ModConfig.get().chatfeedback) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal(message), false);
            }
        }
    }

    public static void sendColourChat(String message, String color) {
        if (ModConfig.get().chatfeedback) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal(message).formatted(Formatting.byName(color.toLowerCase())), false);
            }
        }
    }
}