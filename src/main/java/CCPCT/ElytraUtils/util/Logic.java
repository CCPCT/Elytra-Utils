package CCPCT.ElytraUtils.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;


import java.util.Map;

public class Logic {
    public static void swapElytra() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        int spot;
        ItemStack stack = player.getInventory().armor.get(2);
        if (!stack.isEmpty() && stack.getItem() == Items.ELYTRA && stack.getMaxDamage()-stack.getDamage()>=10){
            //elytra equipped
            spot = getChestplateSpot();
            if (spot == -1){
                Chat.colour("No empty spot!", "red");
                return;
            }
            Chat.send("Swapping to Chestplate!");
        } else {
            //chestplate equipped
            spot = getElytraSpot();
            if (spot == -1){
                Chat.colour("No elytra!","red");
                return;
            }
            Chat.send("Swapping to Elytra!");
        }
        if (spot < 9){
            spot+=36;
        }
        Packets.swapItems(spot,6);
    }

    public static int getElytraSpot() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return -1;

        for (int i = 0; i < player.getInventory().main.size(); i++) {
            ItemStack stack = player.getInventory().main.get(i);
            if (!stack.isEmpty() && stack.getItem() == Items.ELYTRA && stack.getMaxDamage()-stack.getDamage()>=10) {
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
        int bestIndex = 0;
        int bestValue = 0;

        for (int i = 9; i < player.getInventory().main.size(); i++) {
            ItemStack stack = player.getInventory().main.get(i);
            int currentValue = chestplateValues.getOrDefault(player.getInventory().main.get(i).getItem(), 0);
            if (currentValue > bestValue && (stack.getMaxDamage()-stack.getDamage()>=10 || stack.getItem() == Items.AIR)){
                bestValue = currentValue;
                bestIndex = i;
            }
        }
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().main.get(i);
            int currentValue = chestplateValues.getOrDefault(player.getInventory().main.get(i).getItem(), 0);
            if (currentValue > bestValue && (stack.getMaxDamage()-stack.getDamage()>=10 || stack.getItem() == Items.AIR)){
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
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return -1;
        for (int i = 0; i < player.getInventory().main.size(); i++) {
            if (player.getInventory().main.get(i).getItem() == item){
                return i;
            }
        }
        return -1;
    }

    public static ItemStack getItemStack(int slot){
        // input protocol number
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return null;
        if (slot == 45) return player.getInventory().offHand.getFirst();
        else if (slot <= 8) return player.getInventory().armor.get(8-slot);
        else if (slot >= 36) return player.getInventory().main.get(slot-36);
        else return player.getInventory().main.get(slot);
    }

    public static int invToProtocolSlot(int slot,int invType){
        // invType -> 0:main, 1:armour, 2:offHand
        if (invType==2) return 45;
        if (invType==1) return 8-slot;
        if (invType==0){
            if (slot<=8) {
                return slot + 36;
            } else {
                return slot;
            }
        }
        return -1;
    }

    public static void quickFirework(){
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || !player.isGliding()) return;
        Chat.send("Boosting");
        if (player.getMainHandStack().getItem() != Items.FIREWORK_ROCKET) {
            int fireworkSlot = invToProtocolSlot(getItemSpot(Items.FIREWORK_ROCKET), 0);
            Packets.swapUseItems(fireworkSlot);
        } else {
            Packets.useItem();
        }
    }

    public static int getItemCount(Item item){
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return -1;
        PlayerInventory inventory = player.getInventory();
        int count = 0;
        for (int i = 0; i < player.getInventory().main.size(); i++) {
            if (inventory.main.get(i).getItem() == item){
                count += inventory.main.get(i).getCount();
            }
        }
        if (inventory.offHand.getFirst().getItem() == item){
            count += inventory.offHand.getFirst().getCount();
        }
        return count;
    }
}