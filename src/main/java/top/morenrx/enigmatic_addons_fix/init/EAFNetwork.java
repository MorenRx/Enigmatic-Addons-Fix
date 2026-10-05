package top.morenrx.enigmatic_addons_fix.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import top.morenrx.enigmatic_addons_fix.EnigmaticAddonsFix;
import top.morenrx.enigmatic_addons_fix.network.S2CAntiqueBagSyncMessage;
import top.morenrx.enigmatic_addons_fix.network.S2CPlayerConsumeStatusMessage;

public class EAFNetwork {
    public static final EAFNetwork NETWORK_HANDLER = new EAFNetwork();
    private final String protocolVersion = Integer.toString(1);
    private final ResourceLocation channel = EnigmaticAddonsFix.id("main_channel");
    private final SimpleChannel handler = NetworkRegistry.ChannelBuilder
            .named(channel)
            .clientAcceptedVersions(protocolVersion::equals)
            .serverAcceptedVersions(protocolVersion::equals)
            .networkProtocolVersion(() -> protocolVersion)
            .simpleChannel();

    public static void init() {
        NETWORK_HANDLER.register();
    }

    public void register() {
        int id = 0;
        handler.registerMessage(id++, S2CAntiqueBagSyncMessage.class, S2CAntiqueBagSyncMessage::encode, S2CAntiqueBagSyncMessage::decode, S2CAntiqueBagSyncMessage::handle);
        handler.registerMessage(id++, S2CPlayerConsumeStatusMessage.class, S2CPlayerConsumeStatusMessage::encode, S2CPlayerConsumeStatusMessage::decode, S2CPlayerConsumeStatusMessage::handle);
    }

    public <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        handler.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
