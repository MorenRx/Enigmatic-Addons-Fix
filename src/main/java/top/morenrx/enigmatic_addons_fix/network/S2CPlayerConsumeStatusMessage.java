package top.morenrx.enigmatic_addons_fix.network;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;
import top.morenrx.enigmatic_addons_fix.init.EAFNetwork;

import java.util.function.Supplier;

public record S2CPlayerConsumeStatusMessage(boolean consumedIchorBottle, boolean usedVoidTome) {

    public static void encode(S2CPlayerConsumeStatusMessage msg, FriendlyByteBuf buffer) {
        buffer.writeBoolean(msg.consumedIchorBottle);
        buffer.writeBoolean(msg.usedVoidTome);
    }

    public static S2CPlayerConsumeStatusMessage decode(FriendlyByteBuf buffer) {
        return new S2CPlayerConsumeStatusMessage(buffer.readBoolean(), buffer.readBoolean());
    }

    public static void sync(@Nullable ServerPlayer player) {
        if (player == null) return;
        boolean consumedIchor = SuperpositionHandler.hasPersistentTag(player, "ConsumedIchorBottle");
        boolean usedVoidTome = SuperpositionHandler.hasPersistentTag(player, "UsedVoidTome");
        EAFNetwork.NETWORK_HANDLER.sendToPlayer(new S2CPlayerConsumeStatusMessage(consumedIchor, usedVoidTome), player);
    }

    public static void handle(S2CPlayerConsumeStatusMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientPacketHandler.handlePlayerConsumeStatusSync(msg));
        context.setPacketHandled(true);
    }
}
