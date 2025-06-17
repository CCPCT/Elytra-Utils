package CCPCT.ElytraUtils.mixin;

import CCPCT.ElytraUtils.config.ModConfig;
import CCPCT.ElytraUtils.util.Logic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class RenderOverlay {
    @Mixin(InGameHud.class)
    public static class InGameHudMixin {
        @Inject(method = "render", at = @At("TAIL"))
        private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (ModConfig.get().flightOverlay && client.player != null && client.player.isGliding()) {
                int width = client.getWindow().getScaledWidth();
                int height = client.getWindow().getScaledHeight();
                int centerX = width / 2;
                int centerY = height / 2;
                int holeHeight = height - ModConfig.get().flightOverlayWidth;
                int holeWidth = width - ModConfig.get().flightOverlayWidth;
                // Draw top of screen to above the hole
                context.fill(0, 0, width, centerY - holeHeight / 2, ModConfig.get().flightOverlayColour);

                // Draw bottom of screen to below the hole
                context.fill(0, centerY + holeHeight / 2, width, height, ModConfig.get().flightOverlayColour);

                // Draw left of screen to left of hole
                context.fill(0, centerY - holeHeight / 2, centerX - holeWidth / 2, centerY + holeHeight / 2, ModConfig.get().flightOverlayColour);

                // Draw right of hole to end of screen
                context.fill(centerX + holeWidth / 2, centerY - holeHeight / 2, width, centerY + holeHeight / 2, ModConfig.get().flightOverlayColour);
            }
            if (ModConfig.get().fireworkCount){
                int argb = 0xFF | ModConfig.get().fireworkCountColour;
                context.drawText(client.textRenderer, String.valueOf(Logic.getItemCount(Items.FIREWORK_ROCKET)), ModConfig.get().fireworkCountx, ModConfig.get().fireworkCounty, argb, true);
            }
        }
    }
}


