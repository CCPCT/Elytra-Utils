package CCPCT.ElytraUtils.mixin;

import CCPCT.ElytraUtils.config.ModConfig;
import CCPCT.ElytraUtils.util.Chat;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class FireworkOnWall {

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void disableFireworkOnWall(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        Chat.debug("firework on wall mixin called");
        if (player != null &&
                player.isFallFlying() &&
                player.getMainHandStack().getItem() == Items.FIREWORK_ROCKET &&
                ModConfig.get().disableFireworkOnWall) {
            Chat.send("Boosted");
            cir.cancel();
        }
    }

    @Inject(method = "interactEntity", at = @At("HEAD"), cancellable = true)
    private void disableFireworkOnEntity(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        Chat.debug("firework on entity mixin called");
        if (player != null &&
                player.isFallFlying() &&
                player.getMainHandStack().getItem() == Items.FIREWORK_ROCKET &&
                ModConfig.get().disableFireworkOnWall) {
            Chat.send("Boosted");
            cir.cancel();
        }
    }
}

