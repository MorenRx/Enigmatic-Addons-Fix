package top.morenrx.enigmatic_addons_fix.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;
import top.morenrx.enigmatic_addons_fix.helper.AntiqueBagHelper;
import top.morenrx.enigmatic_addons_fix.init.EAFNetwork;

import java.util.function.Supplier;

public record S2CAntiqueBagSyncMessage(CompoundTag items) {

    public static void encode(S2CAntiqueBagSyncMessage msg, FriendlyByteBuf buffer) {
        buffer.writeNbt(msg.items);
    }

    public static S2CAntiqueBagSyncMessage decode(FriendlyByteBuf buffer) {
        CompoundTag items = buffer.readNbt();
        return new S2CAntiqueBagSyncMessage(items != null ? items : new CompoundTag());
    }

    public static void sync(@Nullable ServerPlayer player) {
        if (player == null) return;
        CompoundTag nbt = AntiqueBagHelper.getAntiqueBagTag(player);
        if (nbt == null) return;
        EAFNetwork.NETWORK_HANDLER.sendToPlayer(new S2CAntiqueBagSyncMessage(nbt), player);
    }

    public static void handle(S2CAntiqueBagSyncMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientPacketHandler.handleAntiqueBagSync(msg));
        context.setPacketHandled(true);
    }
}
