package CCPCT.ElytraUtils.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;


public class PacketHandler implements ClientModInitializer {

    public enum PacketType {
        SLOT,
        ATTACK,
        FLY,
        HOTBAR,
        USE,


        STALL,
    }

    private static final ObjectArrayFIFOQueue<Packet> packetsToSend = new ObjectArrayFIFOQueue<>(10);

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            MultiPlayerGameMode gameMode = client.gameMode;
            LocalPlayer player = client.player;
            assert gameMode != null && player != null;

            while (true) {
                if (packetsToSend.isEmpty()){
                    return;
                }
                var packet = packetsToSend.dequeue();
                if (packet==null){
                    return;
                }
                switch (packet.type) {
                    case SLOT -> {
                        gameMode.handleContainerInput(player.containerMenu.containerId, packet.slot, packet.button, packet.input, player);
                    }
                    case ATTACK -> {
                        player.connection.send(new ServerboundPlayerActionPacket(
                                ServerboundPlayerActionPacket.Action.STAB,player.getOnPos(),
                                Direction.fromYRot(player.getYRot(client.getDeltaTracker().getGameTimeDeltaTicks()))));

                        player.connection.send(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
                        player.resetOnlyAttackStrengthTicker();
                    }
                    case FLY -> {
                        if (player.tryToStartFallFlying()) {
                            player.connection.send(new ServerboundPlayerCommandPacket(
                                    player,
                                    ServerboundPlayerCommandPacket.Action.START_FALL_FLYING
                            ));
                        }

                    }
                    case USE -> {
                        useItem();
                    }

                    case HOTBAR -> {
                        player.connection.send(new ServerboundSetCarriedItemPacket(packet.slot));
                        player.getInventory().setSelectedSlot(packet.slot);
                    }

                    default -> {return;}
                }
            }
        });
    }


    public static void clearPackets() {
        packetsToSend.clear();
    }


    public static boolean isQueueEmpty(){
        return packetsToSend.isEmpty();
    }

    public record Packet(PacketType type, int slot, int button, ContainerInput input) {
        // Helper constructor for a standard slot click
        public static void click(int slot, int button, ContainerInput input) {
            packetsToSend.enqueue(new Packet(PacketType.SLOT, slot, button, input));
        }

        public static void attack() {
            packetsToSend.enqueue(new Packet(PacketType.ATTACK, 0, 0, null));
        }

        public static void fly() {
            packetsToSend.enqueue(new Packet(PacketType.FLY, 0, 0, null));
        }

        public static void hotbar(int slot) {
            packetsToSend.enqueue(new Packet(PacketType.HOTBAR, slot, 0, null));
        }

        public static void use() {
            packetsToSend.enqueue(new Packet(PacketType.USE,0,0,null));
        }



        // update
        public static void stall() {
            packetsToSend.enqueue(new Packet(PacketType.STALL, 0, 0, null));
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
        clickItem(start);
        clickItem(end);

        //dont need if item in end slot is originally empty
        clickItem(start);
    }

    public static void swapUseFirework(int start) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null) return;

        int selectedSlot = player.getInventory().getSelectedSlot();

        if (start>=36&&start<=44) {
            Chat.debug(start-36);
            Packet.hotbar(start-36);
            Packet.use();
            Packet.hotbar(selectedSlot);
        } else {
            NonNullList<Slot> slots = Minecraft.getInstance().player.containerMenu.slots;

            ItemStack startItem = slots.get(start).getItem();
            int end = selectedSlot + 36;

            clickItem(start);
            clickItem(end);
            Packet.use();
            startItem.setCount(startItem.getCount()-1);
            clickItem(end);
            clickItem(start);
        }

    }

    public static void clickItem(int slot) {
        Packet.click(slot, 0, ContainerInput.PICKUP);
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