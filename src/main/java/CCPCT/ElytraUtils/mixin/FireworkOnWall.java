package CCPCT.ElytraUtils.mixin;

import CCPCT.ElytraUtils.config.ModConfig;
import CCPCT.ElytraUtils.util.Chat;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
public class FireworkOnWall {

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void disableWallPlace(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        PlayerEntity player = context.getPlayer();
        if (player != null && player.isGliding() && player.getMainHandStack().getItem() == Items.FIREWORK_ROCKET && ModConfig.get().disableFireworkOnWall) {
            Chat.send("Boosted");
            cir.cancel();
        }
    }
}
