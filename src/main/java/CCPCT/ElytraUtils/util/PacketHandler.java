package CCPCT.ElytraUtils.util;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
        MultiPlayerGameMode interactionManager = Minecraft.getInstance().gameMode;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || interactionManager == null) return;
        interactionManager.handleContainerInput(player.containerMenu.containerId, packet.slot, packet.button, packet.type, player);
    }

    public static boolean isQueueEmpty(){
        return packetsToSend.isEmpty();
    }

    public record Packet(int slot, int button, ContainerInput type) {
        // Helper constructor for a standard slot click
        public static void click(int slot, int button, ContainerInput type) {
            packetsToSend.add(new Packet(slot, button, type));
        }

        public static void clickNow(int slot, int button, ContainerInput type) {
            MultiPlayerGameMode interactionManager = Minecraft.getInstance().gameMode;
            LocalPlayer player = Minecraft.getInstance().player;
            assert interactionManager != null;
            assert player != null;
            interactionManager.handleContainerInput(player.containerMenu.containerId, slot, button, type, player);
        }

        // update
        public static void empty() {
            packetsToSend.add(new Packet(0, 0, null));
        }
    }

    //packet methods
    public static void swapItems(int start, int end) {
        // use protocal number
        assert Minecraft.getInstance().player != null;
        NonNullList<Slot> slots = Minecraft.getInstance().player.containerMenu.slots;

        ItemStack startItem = slots.get(start).getItem();
        ItemStack endItem = slots.get(end).getItem();

        Chat.debug(start + " to " + end);
        Chat.debug(startItem + " to " + endItem);
        clickItem(start,false);
        clickItem(end,false);

        //dont need if item in end slot is originally empty
        clickItem(start,false);
    }

    public static void swapUseItems(int start) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null) return;

        int selectedSlot = player.getInventory().getSelectedSlot();

        if (start<=8) {
            selectHotbarSlot(start);
            useItem();
            selectHotbarSlot(selectedSlot);
        } else {
            NonNullList<Slot> slots = Minecraft.getInstance().player.containerMenu.slots;

            ItemStack startItem = slots.get(start).getItem();
            int end = selectedSlot + 36;

            clickItem(start,false);
            clickItem(end,false);
            useItem();
            startItem.setCount(startItem.getCount()-1);
            clickItem(end,false);
            clickItem(start,false);
        }

    }

    public static void clickItem(int slot, boolean delay) {
        if (delay) {
            Packet.click(slot, 0, ContainerInput.PICKUP);
        } else {
            Packet.clickNow(slot, 0, ContainerInput.PICKUP);
        }
    }

    public static void useItem(){
        LocalPlayer player = Minecraft.getInstance().player;
        MultiPlayerGameMode interactionManager = Minecraft.getInstance().gameMode;
        assert interactionManager != null;
        assert player != null;

        interactionManager.useItem(player, InteractionHand.MAIN_HAND);

        Chat.debug("used item");
    }

    public static void selectHotbarSlot(int slot) {
        // use protocol number
        if (slot < 0 || slot > 8) return; // validate slot
        LocalPlayer player = Minecraft.getInstance().player;
        assert player != null;
        player.getInventory().setSelectedSlot(slot);
    }
}