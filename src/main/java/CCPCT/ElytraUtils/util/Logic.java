package CCPCT.ElytraUtils.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Map;
import java.util.stream.IntStream;

public class Logic {

    public static void swapElytra() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        int spot;
        ItemStack stack = player.getInventory().getItem(38);
        if (!stack.isEmpty() && stack.getItem() == Items.ELYTRA && stack.getMaxDamage()-stack.getDamageValue()>=10){
            //elytra equipped
            spot = getChestplateSpot();
            if (spot == -1){
                Chat.send(Chat.getCode("yellow")+"No empty spot!");
                return;
            }
            Chat.send("Swapping to Chestplate!");
        } else {
            //chestplate equipped
            spot = getElytraSpot();
            if (spot == -1){
                Chat.send(Chat.getCode("yellow")+"No elytra!");
                return;
            }
            Chat.send("Swapping to Elytra!");
        }

        PacketHandler.swapItems(spot,6);
    }

    public static int getElytraSpot() {
        assert Minecraft.getInstance().player != null;
        NonNullList<Slot> slots = Minecraft.getInstance().player.inventoryMenu.slots;

        for (int i = 9; i <= 45; i++) {
            ItemStack stack = slots.get(i).getItem();
            if (!stack.isEmpty() && stack.getItem() == Items.ELYTRA && stack.getMaxDamage()-stack.getDamageValue()>=10) {
                return i;
            }
        }
        return -1;
    }

    public static boolean itemHasEnchantment(ItemStack stack, String ench) {
        return stack.getEnchantments().entrySet().stream().anyMatch(entry -> entry.getKey().is(Identifier.withDefaultNamespace(ench)));
    }

    public static void spearBoost() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        float delta = minecraft.getDeltaTracker().getGameTimeDeltaTicks();
        assert player != null;

        if (player.inventoryMenu.slots.get(6).getItem().getItem() != Items.ELYTRA) {
            Chat.send("Not Equipping elytra");
            return;
        }

        if (player.getAttackStrengthScale(delta) < 1) {
            Chat.send("Still in attack cooldown");
            return;
        }


        if (itemHasEnchantment(player.getMainHandItem(), "lunge")) {
            PacketHandler.clickItem(6);
            PacketHandler.Packet.stall();
            PacketHandler.clickItem(6);
            PacketHandler.Packet.attack();
            PacketHandler.Packet.stall();
            PacketHandler.Packet.fly();
            return;
        }

        int slot = -1;
        for (int i = 0; i < 9; i++) {
            if (player.getSlot(i) == null) continue;
            if (itemHasEnchantment(player.getSlot(i).get(), "lunge")) {
                slot = i;
                break;
            }
        }

        if (slot == -1) {
            Chat.send("No spear :(");
            return;
        }

        int beforeSlot = player.getInventory().getSelectedSlot();
        PacketHandler.clickItem(6);
        PacketHandler.Packet.stall();
        PacketHandler.clickItem(6);
        PacketHandler.Packet.hotbar(slot);
        PacketHandler.Packet.attack();
        PacketHandler.Packet.stall();
        PacketHandler.Packet.fly();
        PacketHandler.Packet.hotbar(beforeSlot);



    }

    final static Map<Item, Integer> chestplateValues = Map.of(
            Items.AIR, 1,
            Items.LEATHER_CHESTPLATE, 2,
            Items.GOLDEN_CHESTPLATE, 3,
            Items.CHAINMAIL_CHESTPLATE, 4,
            Items.COPPER_CHESTPLATE, 5,
            Items.IRON_CHESTPLATE, 6,
            Items.DIAMOND_CHESTPLATE, 7,
            Items.NETHERITE_CHESTPLATE, 8
    );

    public static int getChestplateSpot() {
        assert Minecraft.getInstance().player != null;
        NonNullList<Slot> slots = Minecraft.getInstance().player.inventoryMenu.slots;


        // default assume inventory full without chestplate, use the first item in inventory
        int bestIndex = 0;
        int bestValue = 0;

        for (int i = 9; i <= 45; i++) {
            ItemStack stack = slots.get(i).getItem();
            int currentValue = chestplateValues.getOrDefault(stack.getItem(), 0);
            if (currentValue > bestValue && (stack.getMaxDamage()-stack.getDamageValue()>=10 || stack.getItem() == Items.AIR)){
                bestValue = currentValue;
                bestIndex = i;
            }
        }

        if (bestValue == 0){
            return -1;
        }
        return bestIndex;
    }

    public static int getItemSpot(Item item){
        assert Minecraft.getInstance().player != null;
        NonNullList<Slot> slots = Minecraft.getInstance().player.inventoryMenu.slots;
        for (int i = 9; i <= 44; i++) {
            if (slots.get(i).getItem().getItem() == item){
                return i;
            }
        }
        return -1;
    }

    public static void quickFirework(){
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.isFallFlying()) return;
        int slot = getItemSpot(Items.FIREWORK_ROCKET);
        if (slot == -1) {
            Chat.send(Chat.getCode("yellow")+"No firework in inventory!");
            return;
        }
        Chat.send("Boosting");
        PacketHandler.swapUseFirework(slot);
    }

    public static int getItemCount(Item item){
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return -1;
        Inventory inventory = player.getInventory();
        int count = IntStream.range(0, player.getInventory().getContainerSize()).filter(i -> inventory.getItem(i).getItem() == item).map(i -> inventory.getItem(i).getCount()).sum();
        if (player.getOffhandItem().getItem() == item){
            count += player.getOffhandItem().getCount();
        }
        return count;
    }
}