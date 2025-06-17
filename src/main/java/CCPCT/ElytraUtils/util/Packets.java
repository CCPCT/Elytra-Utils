package CCPCT.ElytraUtils.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.client.network.PendingUpdateManager;

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
            IngameChat.sendChat("sendng");
            packetsToSend.removeFirst();
        });
    }

    //packet methods
    public static void swapItems(int start, int end) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        ScreenHandler screenHandler = player.currentScreenHandler;

        packetsToSend.add(new ClickSlotC2SPacket(
                screenHandler.syncId,
                screenHandler.getRevision(),
                start,
                0,
                SlotActionType.PICKUP,
                ItemStack.EMPTY,
                new Int2ObjectOpenHashMap<>()
        ));

        packetsToSend.add(new ClickSlotC2SPacket(
                screenHandler.syncId,
                screenHandler.getRevision(),
                end,
                0,
                SlotActionType.PICKUP,
                player.getInventory().main.get(start),
                new Int2ObjectOpenHashMap<>()
        ));

        packetsToSend.add(new ClickSlotC2SPacket(
                screenHandler.syncId,
                screenHandler.getRevision(),
                start,
                0,
                SlotActionType.PICKUP,
                player.getInventory().main.get(end),
                new Int2ObjectOpenHashMap<>()
        ));
    }
    public static void swapUseItems(int start) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        ScreenHandler screenHandler = player.currentScreenHandler;


        boolean holdingAir = player.getMainHandStack().getItem() == Items.AIR;
        IngameChat.sendChat(String.valueOf(holdingAir));

        packetsToSend.add(new ClickSlotC2SPacket(
                screenHandler.syncId,
                screenHandler.getRevision(),
                start,
                0,
                SlotActionType.PICKUP,
                ItemStack.EMPTY,
                new Int2ObjectOpenHashMap<>()
        ));

        packetsToSend.add(new ClickSlotC2SPacket(
                screenHandler.syncId,
                screenHandler.getRevision(),
                player.getInventory().selectedSlot + 36,
                0,
                SlotActionType.PICKUP,
                player.getInventory().main.get(start),
                new Int2ObjectOpenHashMap<>()
        ));
        IngameChat.sendChat(String.valueOf(player.getInventory().selectedSlot));

        if (!holdingAir) {
            packetsToSend.add(new ClickSlotC2SPacket(
                screenHandler.syncId,
                screenHandler.getRevision(),
                start,
                0,
                SlotActionType.PICKUP,
                player.getMainHandStack(),
                new Int2ObjectOpenHashMap<>()
            ));
        }

        packetsToSend.add(new PlayerInteractItemC2SPacket(
                Hand.MAIN_HAND,
                0,
                player.getYaw(),
                player.getPitch()
        ));
        IngameChat.sendChat("packet");

    }
}