package CCPCT.ElytraUtils.client;

import CCPCT.ElytraUtils.config.ModConfig;
import CCPCT.ElytraUtils.config.configScreen;
import CCPCT.ElytraUtils.util.Chat;
import CCPCT.ElytraUtils.util.Logic;
import CCPCT.ElytraUtils.util.PacketHandler;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

import static CCPCT.ElytraUtils.config.ModConfig.load;

public class ElytraUtilsClient implements ClientModInitializer {
    final public static String MODID = "elytrautils";
    final public static KeyMapping.Category KEYBIND_CAT = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MODID, "keymap"));
    public static KeyMapping swapElytraKey;
    public static KeyMapping configScreenKey;
    public static KeyMapping endFlightKey;
    public static KeyMapping quickFireworkKey;
    private static boolean lastJumpKeyDown = false;
    private static boolean jumpKeyDown = false;
    private static boolean lastGliding = false;
    private static boolean gliding = false;
    private static boolean lastFirework = false;
    private static boolean alerted = false;
    public static boolean lastSwapKeyDown = false;

    @Override
    public void onInitializeClient() {

        load();
        // Register the KeyMapping
        swapElytraKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "elytrautils:key.swap_elytra", // translation key
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,       // default key
                KEYBIND_CAT      // category in controls menu
        ));

        configScreenKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "elytrautils:key.config_menu", // translation key
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_DONT_CARE,       // default key
                KEYBIND_CAT      // category in controls menu
        ));

        endFlightKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "elytrautils:key.end_flight", // translation key
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_SPACE,       // default key
                KEYBIND_CAT      // category in controls menu
        ));

        quickFireworkKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "elytrautils:key.quick_firework", // translation key
                InputConstants.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_MIDDLE,       // default key
                KEYBIND_CAT      // category in controls menu
        ));

        // Register client tick listener


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            // swap totem
            if (swapElytraKey.isDown()) {
                if (!lastSwapKeyDown) {
                    Logic.swapElytra();
                    lastSwapKeyDown = true;
                }
            } else {
                lastSwapKeyDown = false;
            }

            // open config
            if (configScreenKey.isDown()) {
                Minecraft.getInstance().setScreen(configScreen.getConfigScreen(Minecraft.getInstance().screen));
            }


            gliding = client.player.isFallFlying();
            jumpKeyDown = endFlightKey.isDown();
            if (lastGliding && gliding && jumpKeyDown && !lastJumpKeyDown) {
                Chat.send("Ended flight");

                PacketHandler.Packet.empty();
                PacketHandler.clickItem(6, true);
                PacketHandler.clickItem(6, true);

            }
            lastJumpKeyDown = jumpKeyDown;
            lastGliding = gliding;


            if (quickFireworkKey.isDown()) {
                if (!lastFirework) {
                    Logic.quickFirework();
                    lastFirework = true;
                }
            } else {
                lastFirework = false;
            }


            if (ModConfig.get().durabilityAlert) {
                ItemStack item = client.player.getInventory().getItem(38);
                if (item.getItem() == Items.ELYTRA && item.getMaxDamage() - item.getDamageValue() <= 9) {
                    if (ModConfig.get().replaceBreakingElytra && Logic.getElytraSpot() != -1) {
                        Chat.send(Chat.getCode("yellow") + "Replacing breaking elytra");
                        Logic.swapElytra();
                        alerted = true;
                    } else if (!alerted) {
                        Chat.send(Chat.getCode("red") + "Elytra breaking!");
                        alerted = true;
                    }
                } else {
                    alerted = false;
                }
            }
        });

        // special case for armor stands cus its unique
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof ArmorStand &&
                    world.isClientSide() &&
                    player.isFallFlying() &&
                    player.getMainHandItem().getItem() == Items.FIREWORK_ROCKET &&
                    ModConfig.get().disableFireworkOnWall) {
                Chat.send("Boosted from armourstand");
                PacketHandler.useItem();
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS; // allow normal processing
        });

        HudElementRegistry.attachElementBefore(VanillaHudElements.CROSSHAIR, Identifier.fromNamespaceAndPath(MODID, "overlay"),
                ((GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker) -> {
                    Minecraft client = Minecraft.getInstance();
                    if (ModConfig.get().flightOverlay && client.player != null && client.player.isFallFlying()) {
                        int width = client.getWindow().getGuiScaledWidth();
                        int height = client.getWindow().getGuiScaledHeight();
                        int centerX = width / 2;
                        int centerY = height / 2;
                        int holeHeight = height - ModConfig.get().flightOverlayWidth;
                        int holeWidth = width - ModConfig.get().flightOverlayWidth;
                        // Draw top of screen to above the hole
                        guiGraphicsExtractor.fill(0, 0, width, centerY - holeHeight / 2, ModConfig.get().flightOverlayColour);

                        // Draw bottom of screen to below the hole
                        guiGraphicsExtractor.fill(0, centerY + holeHeight / 2, width, height, ModConfig.get().flightOverlayColour);

                        // Draw left of screen to left of hole
                        guiGraphicsExtractor.fill(0, centerY - holeHeight / 2, centerX - holeWidth / 2, centerY + holeHeight / 2, ModConfig.get().flightOverlayColour);

                        // Draw right of hole to end of screen
                        guiGraphicsExtractor.fill(centerX + holeWidth / 2, centerY - holeHeight / 2, width, centerY + holeHeight / 2, ModConfig.get().flightOverlayColour);
                    }
                    if (ModConfig.get().fireworkCount && client.player.isFallFlying()) {
                        int argb = ModConfig.get().fireworkCountColour;
                        guiGraphicsExtractor.text(client.font, String.valueOf(Logic.getItemCount(Items.FIREWORK_ROCKET)), ModConfig.get().fireworkCountx, ModConfig.get().fireworkCounty, argb, false);
                    }
                })
        );
    }
}

