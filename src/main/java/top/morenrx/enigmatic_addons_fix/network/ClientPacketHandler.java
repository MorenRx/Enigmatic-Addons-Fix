package top.morenrx.enigmatic_addons_fix.network;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import top.morenrx.enigmatic_addons_fix.helper.AntiqueBagHelper;

public final class ClientPacketHandler {

    public static void handleAntiqueBagSync(S2CAntiqueBagSyncMessage msg) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        CompoundTag itemsCompoundTag = msg.items();
        if (itemsCompoundTag == null) return;
        AntiqueBagHelper.getCapability(player).ifPresent(handler -> handler.readTag(itemsCompoundTag));
    }

    public static void handlePlayerConsumeStatusSync(S2CPlayerConsumeStatusMessage msg) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (msg.consumedIchorBottle()) {
            SuperpositionHandler.setPersistentBoolean(player, "ConsumedIchorBottle", true);
        } else {
            SuperpositionHandler.removePersistentTag(player, "ConsumedIchorBottle");
        }
        if (msg.usedVoidTome()) {
            SuperpositionHandler.setPersistentBoolean(player, "UsedVoidTome", true);
        } else {
            SuperpositionHandler.removePersistentTag(player, "UsedVoidTome");
        }
    }
}
