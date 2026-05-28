package CCPCT.ElytraUtils.util;

import CCPCT.ElytraUtils.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.Map;

public class Chat {
    public static final Map<String, String> COLOR_TO_CODE = Map.ofEntries(
            // Core Colors
            Map.entry("black", "§0"),
            Map.entry("dark_blue", "§1"),
            Map.entry("dark_green", "§2"),
            Map.entry("dark_aqua", "§3"),
            Map.entry("dark_red", "§4"),
            Map.entry("dark_purple", "§5"),
            Map.entry("gold", "§6"),
            Map.entry("orange", "§6"), // Alternate name for Gold
            Map.entry("gray", "§8"),
            Map.entry("dark_gray", "§8"),
            Map.entry("blue", "§9"),

            // Bright / Light Colors
            Map.entry("green", "§a"),
            Map.entry("lime", "§a"),
            Map.entry("aqua", "§b"),
            Map.entry("cyan", "§b"),
            Map.entry("red", "§c"),
            Map.entry("light_purple", "§d"),
            Map.entry("pink", "§d"),
            Map.entry("yellow", "§e"),
            Map.entry("white", "§f"),
            Map.entry("light_gray", "§7"),

            // Formats / Modifiers
            Map.entry("obfuscated", "§k"),
            Map.entry("bold", "§l"),
            Map.entry("strikethrough", "§m"),
            Map.entry("underline", "§n"),
            Map.entry("italic", "§o"),
            Map.entry("reset", "§r")
    );

    public static <T> void send(T message) {
        if (ModConfig.get().chatfeedback) {
            Minecraft client = Minecraft.getInstance();
            if (client.player != null) {
                client.player.sendOverlayMessage(Component.literal("§7[Elytra Utils]§r " + message));
            }
        }
    }

    public static <T> void debug(T message) {
        if (ModConfig.get().debug) {
            Minecraft client = Minecraft.getInstance();
            if (client.player != null) {
                client.player.sendSystemMessage(Component.literal("§7[Debug]§r " + message));
            }
        }
    }

    // Helper method to safely pull codes
    public static String getCode(String colorName) {
        return COLOR_TO_CODE.getOrDefault(colorName.toLowerCase().trim(), "§r");
    }

}
