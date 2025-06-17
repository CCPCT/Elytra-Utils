package CCPCT.ElytraUtils.client;

import CCPCT.ElytraUtils.util.Chat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.Objects;

import static CCPCT.ElytraUtils.client.ElytraUtilsClient.endFlightKey;


public class SpaceNoFlight implements ClientModInitializer {
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (Objects.equals(endFlightKey.getBoundKeyTranslationKey(), "key.keyboard.space")){
                Chat.send("set to space");
            }
            if (client.player != null && client.options.jumpKey.isPressed()) {
                // Execute your logic here
                System.out.println("Spacebar pressed while in game!");
            }
        });
    }
}
