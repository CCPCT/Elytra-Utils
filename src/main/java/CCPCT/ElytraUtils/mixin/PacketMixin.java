package CCPCT.ElytraUtils.mixin;

// FOR DEBUG... DONT ENABLE

import io.netty.channel.ChannelFutureListener;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class PacketMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("PacketLogger");

    private static final ObjectList<String> banned = ObjectList.of("ServerboundClientTickEndPacket","Pos","ClientboundLevelChunkWithLightPacket","PosRot","Rot","ClientboundSoundPacket","ClientboundSetEntityMotionPacket","ClientboundRotateHeadPacket");
    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;Z)V", at = @At("HEAD"))
    private void onSendPacket(Packet<?> packet, @Nullable ChannelFutureListener listener, boolean flush, CallbackInfo ci) {
        String name = packet.getClass().getSimpleName();
        if (banned.contains(name)) {
            return;
        }

        LOGGER.info("[C2S Outbound] {}", packet.getClass().getSimpleName());

        // Example: Log specific packet data or filter out noise
        // if (packet instanceof PlayerMoveC2SPacket movePacket) { ... }
    }
}