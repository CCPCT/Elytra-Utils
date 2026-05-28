package CCPCT.ElytraUtils.util;

import CCPCT.ElytraUtils.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Chat {
    public static <T> void send(T message) {
        if (ModConfig.get().chatfeedback) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal("§7[Elytra Utils]§r "+String.valueOf(message)), false);
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

    public static <T> void debug(T message) {
        if (ModConfig.get().debug) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal("§7[Debug]§r "+message), false);
            }
        }
    }
}
