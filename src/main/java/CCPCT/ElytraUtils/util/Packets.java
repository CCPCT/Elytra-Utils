package CCPCT.ElytraUtils.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;

import java.util.ArrayList;


import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ComponentChangesHash;
import net.minecraft.screen.sync.ItemStackHash;
import net.minecraft.util.Hand;


public class Packets implements ClientModInitializer {
    private final static ArrayList<Packet<?>> packetsToSend = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (packetsToSend.isEmpty()) return;

            if (packetsToSend.getFirst() == null){
                packetsToSend.removeFirst();
                return;
            }

            sendPacket(packetsToSend.getFirst(),false);

            packetsToSend.removeFirst();
        });
    }

    public static void sendPacket(Packet<?> packet,boolean delay) {
        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (networkHandler == null) return;

        if (delay) packetsToSend.add(packet);
        else networkHandler.sendPacket(packet);
    }

    //packet methods
    public static void swapItems(int start, int end) {
        // use protocal number
        ItemStack startItem = Logic.getItemStack(start);
        if (startItem == null) return;
        ItemStack endItem = Logic.getItemStack(end);
        if (endItem == null) return;

        Chat.debug(start + " to " + end);
        Chat.debug(startItem + " to " + endItem);
        clickItem(start,ItemStack.EMPTY,false);
        clickItem(end,startItem,false);

        //dont need if item in end slot is originally empty
        Chat.debug(endItem.getItem() + "");
        if (endItem.getItem() == Items.AIR) return;
        clickItem(start,endItem,false);
    }

    public static void swapUseItems(int start) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        if (start<=8) {
            selectHotbarSlot(start,false);
            useItem(false);
            selectHotbarSlot(player.getInventory().getSelectedSlot(),false);
        } else {
            ItemStack startItem = Logic.getItemStack(start);
            int end = player.getInventory().getSelectedSlot() + 36;
            ItemStack endItem = Logic.getItemStack(end);

            if (startItem==null || endItem == null) return;
            clickItem(start, ItemStack.EMPTY,false);
            clickItem(end, startItem,false);
            useItem(false);
            startItem.decrement(1);
            clickItem(end, endItem,false);
            clickItem(start, startItem,false);
        }

    }

    public static void clickItem(int slot, ItemStack holding, boolean delay) {
        if (MinecraftClient.getInstance().player == null) return;
        ScreenHandler screenHandler = MinecraftClient.getInstance().player.currentScreenHandler;
        ComponentChangesHash.ComponentHasher hasher = MinecraftClient.getInstance().getNetworkHandler().getComponentHasher();
        sendPacket(new ClickSlotC2SPacket(
                screenHandler.syncId,
                screenHandler.getRevision(),
                (short)slot,
                (byte)0,
                SlotActionType.PICKUP,
                new Int2ObjectOpenHashMap<>(),
                ItemStackHash.fromItemStack(holding,hasher)
        ),delay);
    }

    public static void useItem(boolean delay){
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        sendPacket(new PlayerInteractItemC2SPacket(
                Hand.MAIN_HAND,
                0,
                player.getYaw(),
                player.getPitch()
        ),delay);
        Chat.debug(player.getYaw() + " | " + player.getPitch());
    }

    public static void selectHotbarSlot(int slot, boolean delay) {
        // use protocol number
        if (slot < 0 || slot > 8) return; // validate slot

        sendPacket(new UpdateSelectedSlotC2SPacket(slot),delay);
    }
}