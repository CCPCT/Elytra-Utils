package CCPCT.ElytraUtils.mixin;

import CCPCT.ElytraUtils.client.ElytraUtilsClient;
import CCPCT.ElytraUtils.config.ModConfig;
import CCPCT.ElytraUtils.util.Chat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class FireworkOnWall {

    @Final
    @Shadow
    private Minecraft minecraft;

    @Inject(method = "performUseItemOn", at = @At("HEAD"), cancellable = true)
    private void disableFireworkOnWall(LocalPlayer player, InteractionHand hand, BlockHitResult blockHit, CallbackInfoReturnable<InteractionResult> cir) {
        Chat.debug("firework on wall mixin called");
        if (ignoreWall(minecraft.player)) {
            Chat.send("Boosted");
            cir.cancel();
        }
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void disableFireworkOnEntity(Player player, Entity entity, EntityHitResult hitResult, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Chat.debug("firework on entity mixin called");
        if (ignoreWall(minecraft.player)) {
            Chat.send("Boosted");
            cir.cancel();
        }
    }

    @Inject(method = "handlePickItemFromBlock", at = @At("HEAD"), cancellable = true)
    private void disableFireworkOnPickBlock(BlockPos pos, boolean includeData, CallbackInfo ci) {
        Chat.debug("firework on pick block called");
        if (ignoreWall(minecraft.player) && ElytraUtilsClient.quickFireworkKey.same(minecraft.options.keyPickItem)) {
            Chat.send("Boosted");
            ci.cancel();
        }
    }

    @Inject(method = "handlePickItemFromEntity", at = @At("HEAD"), cancellable = true)
    private void disableFireworkOnPickEntity(Entity entity, boolean includeData, CallbackInfo ci) {
        Chat.debug("firework on pick entity called");
        if (ignoreWall(minecraft.player) && ElytraUtilsClient.quickFireworkKey.same(minecraft.options.keyPickItem)) {
            Chat.send("Boosted");
            ci.cancel();
        }
    }

    @Unique
    boolean ignoreWall(LocalPlayer player) {
        return player != null &&
                player.isFallFlying() &&
                player.getMainHandItem().getItem() == Items.FIREWORK_ROCKET &&
                ModConfig.get().disableFireworkOnWall;
    }


}

