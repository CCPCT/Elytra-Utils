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
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;


public class Packets implements ClientModInitializer {
    private static ArrayList<Packet<?>> packetsToSend = new ArrayList<>();
    public static void create(Packet<?> packet){
        packetsToSend.add(packet);
    }

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (packetsToSend.isEmpty()){
                return;
            }
            if (packetsToSend.getFirst() == null){
                packetsToSend.removeFirst();
                return;
            }
            ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler == null) {
                return;
            }
            networkHandler.sendPacket(packetsToSend.getFirst());
            Chat.send("sending packet");
            packetsToSend.removeFirst();
        });
    }

    //packet methods
    public static void swapItems(int start, int end) {
        ItemStack startItem = Logic.getItemStack(start);
        if (startItem == null) return;
        ItemStack endItem = Logic.getItemStack(start);
        if (endItem == null) return;

        moveItem(start,ItemStack.EMPTY);
        moveItem(end,startItem);

        //dont need if item in end slot is originally empty
        if (endItem.getItem() == Items.AIR) return;
        moveItem(start,endItem);
    }

    public static void swapUseItems(int start) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        ScreenHandler screenHandler = player.currentScreenHandler;

        swapItems(start,player.getInventory().selectedSlot + 36);

        //use item
        packetsToSend.add(new PlayerInteractItemC2SPacket(
                Hand.MAIN_HAND,
                0,
                player.getYaw(),
                player.getPitch()
        ));

        swapItems(player.getInventory().selectedSlot + 36,start);

    }

    public static void moveItem(int slot, ItemStack holding){
        ScreenHandler screenHandler = MinecraftClient.getInstance().player.currentScreenHandler;
        packetsToSend.add(new ClickSlotC2SPacket(
                screenHandler.syncId,
                screenHandler.getRevision(),
                slot,
                0,
                SlotActionType.PICKUP,
                holding,
                new Int2ObjectOpenHashMap<>()
        ));
    }

}