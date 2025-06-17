package CCPCT.ElytraUtils.util;

import CCPCT.ElytraUtils.config.ModConfig;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.Identifier;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Unique;


import java.util.ArrayList;
import java.util.Map;

public class elytraLogic {
    public static boolean overlayactive = false;
    public static boolean totemCountActive = false;
    public static int totemCountValue = 0;

//    public static void swapElytra() {
//        PlayerEntity player = MinecraftClient.getInstance().player;
//        if (player == null) return;
//        int spot;
//        if (player.getInventory().main.get(38).getItem() == Items.ELYTRA){
//            //elytra equipped
//            spot = getChestplateSpot();
//            IngameChat.sendColourChat("Swapping to Chestplate!", "green");
//        } else {
//            //chestplate equipped
//            spot = getElytraSpot();
//            IngameChat.sendColourChat("Swapping to Elytra!", "gray");
//        }
//
//        int spareTotemSlot = getSlotWithSpareTotem();
//        if (spareTotemSlot == -1) {
//            IngameChat.sendColourChat("No totem!", "red");
//            return;
//        }
//        moveTotemToOffhand();
//
//    }

    public static int getElytraSpot() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return -1;

        for (int i = 0; i < player.getInventory().main.size(); i++) {
            ItemStack stack = player.getInventory().main.get(i);
            if (!stack.isEmpty() && stack.getItem() == Items.ELYTRA && stack.getMaxDamage()-stack.getDamage()>=2) {
                return i;
            }
        }
        return -1;
    }

    public static int getChestplateSpot() {
        final Map<Item, Integer> chestplateValues = Map.of(
                Items.AIR, 1,
                Items.LEATHER_CHESTPLATE, 2,
                Items.GOLDEN_CHESTPLATE, 3,
                Items.CHAINMAIL_CHESTPLATE, 4,
                Items.IRON_CHESTPLATE, 5,
                Items.DIAMOND_CHESTPLATE, 6,
                Items.NETHERITE_CHESTPLATE, 7
        );

        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return -1;

        // default assume inventory full without chestplate, use the first item in inventory
        int bestIndex = 9;
        int bestValue = 0;

        for (int i = 0; i < player.getInventory().main.size(); i++) {
            ItemStack stack = player.getInventory().main.get(i);
            int currentValue = chestplateValues.getOrDefault(player.getInventory().main.get(i).getItem(), 0);
            if (currentValue > bestValue && (stack.getMaxDamage()-stack.getDamage()>=10 || stack.getItem() == Items.AIR)){
                bestValue = currentValue;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

//    private static void moveTotemToOffhand() {
//        PlayerEntity player = MinecraftClient.getInstance().player;
//        ScreenHandler screenHandler = player.currentScreenHandler;
//        int fromSlot = getSlotWithSpareTotem();
//        PlayerInventory inventory = player.getInventory();
//        ItemStack totemStack = inventory.getStack(fromSlot).copy();
//
//        if (fromSlot < 9) {
//            // Select Totem Slot
//            packetsToSend.add(new UpdateSelectedSlotC2SPacket(fromSlot));
//
//            // Move Totem To Offhand
//            packetsToSend.add(new PlayerActionC2SPacket(
//                    PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND,
//                    BlockPos.ORIGIN,
//                    Direction.DOWN
//            ));
//
//            // Restore Old Hotbar Slot
//            packetsToSend.add(new UpdateSelectedSlotC2SPacket(inventory.selectedSlot));
//
//            packetsToSend.add(null);
//        } else {
//
//            packetsToSend.add(new ClickSlotC2SPacket(
//                    screenHandler.syncId,
//                    screenHandler.getRevision(),
//                    fromSlot,
//                    0,
//                    SlotActionType.PICKUP,
//                    ItemStack.EMPTY,
//                    new Int2ObjectOpenHashMap<>()
//            ));
//
//            packetsToSend.add(new ClickSlotC2SPacket(
//                    screenHandler.syncId,
//                    screenHandler.getRevision(),
//                    45,
//                    0,
//                    SlotActionType.PICKUP,
//                    totemStack,
//                    new Int2ObjectOpenHashMap<>()
//            ));
//
//            packetsToSend.add(null);
//        }
//    }

    public static void playCustomSound() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.playSound(SoundEvent.of(Identifier.of(ModConfig.get().customSoundName)),ModConfig.get().customSoundVolume,1.0f);
        }
    }
}