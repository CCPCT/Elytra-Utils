package CCPCT.ElytraUtils.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Map;

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
        if (spot < 9){
            spot+=36;
        }
        PacketHandler.swapItems(spot,6);
    }

    public static int getElytraSpot() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return -1;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() == Items.ELYTRA && stack.getMaxDamage()-stack.getDamageValue()>=10) {
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

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return -1;

        // default assume inventory full without chestplate, use the first item in inventory
        int bestIndex = 0;
        int bestValue = 0;

        for (int i = 9; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            int currentValue = chestplateValues.getOrDefault(player.getInventory().getItem(i).getItem(), 0);
            if (currentValue > bestValue && (stack.getMaxDamage()-stack.getDamageValue()>=10 || stack.getItem() == Items.AIR)){
                bestValue = currentValue;
                bestIndex = i;
            }
        }
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            int currentValue = chestplateValues.getOrDefault(player.getInventory().getItem(i).getItem(), 0);
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
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return -1;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).getItem() == item){
                return i;
            }
        }
        return -1;
    }

    public static ItemStack getItemStack(int slot){
        // input protocol number
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return null;
        if (slot == 45) return player.getOffhandItem();
        else if (slot <= 8) return player.getInventory().getItem(8-slot+36);
        else if (slot >= 36) return player.getInventory().getItem(slot-36);
        else return player.getInventory().getItem(slot);
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
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.isFallFlying()) return;
        int slot = getItemSpot(Items.FIREWORK_ROCKET);
        if (slot == -1) {
            Chat.send(Chat.getCode("yellow")+"No firework in inventory!");
            return;
        }
        Chat.send("Boosting");
        PacketHandler.swapUseItems(slot);
    }

    public static int getItemCount(Item item){
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return -1;
        Inventory inventory = player.getInventory();
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (inventory.getItem(i).getItem() == item){
                count += inventory.getItem(i).getCount();
            }
        }
        if (player.getOffhandItem().getItem() == item){
            count += player.getOffhandItem().getCount();
        }
        return count;
    }
}