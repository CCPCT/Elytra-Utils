package CCPCT.ElytraUtils.util;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class Ticks implements ClientModInitializer {
    public static int currentTick = 0;
    public static int get(){return currentTick;}
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            currentTick++;
        });
    }
}