package CCPCT.ElytraUtils.util;

import CCPCT.ElytraUtils.mixin.PlayerInventoryMixin;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

import java.util.ArrayDeque;
import java.util.Queue;


public class PacketHandler implements ClientModInitializer {

    private static final Queue<Packet> packetsToSend = new ArrayDeque<>();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (packetsToSend.isEmpty()){
                return;
            }
            var packet = packetsToSend.poll();
            if (packet==null || packet.type == null ){
                return;
            }
            sendPacket(packet);
        });
    }


    public static void clearPackets() {
        packetsToSend.clear();
    }

    private static void sendPacket(Packet packet) {
        ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || interactionManager == null) return;
        interactionManager.clickSlot(player.currentScreenHandler.syncId, packet.slot, packet.button, packet.type, player);
    }

    public static boolean isQueueEmpty(){
        return packetsToSend.isEmpty();
    }

    public record Packet(int slot, int button, SlotActionType type) {
        // Helper constructor for a standard slot click
        public static void click(int slot, int button, SlotActionType type) {
            packetsToSend.add(new Packet(slot, button, type));
        }

        public static void clickNow(int slot, int button, SlotActionType type) {
            ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;
            interactionManager.clickSlot(player.currentScreenHandler.syncId, slot, button, type, player);
        }

        // update
        public static void empty() {
            packetsToSend.add(new Packet(0, 0, null));
        }
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
        clickItem(start,false);
        clickItem(end,false);

        //dont need if item in end slot is originally empty
        Chat.debug(endItem.getItem() + "");
        if (endItem.getItem() == Items.AIR) return;
        clickItem(start,false);
    }

    public static void swapUseItems(int start) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        int selectedSlot = ((PlayerInventoryMixin)player.getInventory()).getSelectedSlot();

        if (start<=8) {
            selectHotbarSlot(start);
            useItem();
            selectHotbarSlot(selectedSlot);
        } else {
            ItemStack startItem = Logic.getItemStack(start);
            int end = selectedSlot + 36;
            ItemStack endItem = Logic.getItemStack(end);

            if (startItem==null || endItem == null) return;
            clickItem(start,false);
            clickItem(end,false);
            useItem();
            startItem.decrement(1);
            clickItem(end,false);
            clickItem(start,false);
        }

    }

    public static void clickItem(int slot, boolean delay) {
        if (delay) {
            Packet.click(slot, 0, SlotActionType.PICKUP);
        } else {
            Packet.clickNow(slot, 0, SlotActionType.PICKUP);
        }
    }

    public static void useItem(){
        PlayerEntity player = MinecraftClient.getInstance().player;
        ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;

        if (player == null) return;
        interactionManager.interactItem(player,Hand.MAIN_HAND);

        Chat.debug("used item");
    }

    public static void selectHotbarSlot(int slot) {
        // use protocol number
        if (slot < 0 || slot > 8) return; // validate slot
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player==null || player.getInventory()==null) return;
        ((PlayerInventoryMixin)player.getInventory()).setSelectedSlot(slot);
    }
}