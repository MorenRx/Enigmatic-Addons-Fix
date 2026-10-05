package top.morenrx.enigmatic_addons_fix.event;

import auviotre.enigmatic.addon.contents.items.ArtificialFlower;
import auviotre.enigmatic.addon.registries.EnigmaticAddonItems;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.morenrx.enigmatic_addons_fix.EnigmaticAddonsFix;
import top.morenrx.enigmatic_addons_fix.network.S2CAntiqueBagSyncMessage;
import top.morenrx.enigmatic_addons_fix.network.S2CPlayerConsumeStatusMessage;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = EnigmaticAddonsFix.MODID)
public class CommonEventHandler {
    public static final String FLOWER_ENABLE_UUID = "FlowerEnableUUID";

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            S2CAntiqueBagSyncMessage.sync(player);
            S2CPlayerConsumeStatusMessage.sync(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            S2CAntiqueBagSyncMessage.sync(player);
            S2CPlayerConsumeStatusMessage.sync(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            S2CAntiqueBagSyncMessage.sync(player);
            S2CPlayerConsumeStatusMessage.sync(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        Player original = event.getOriginal();
        Player player = event.getEntity();

        if (original.getPersistentData().hasUUID(FLOWER_ENABLE_UUID)) {
            UUID uuid = original.getPersistentData().getUUID(FLOWER_ENABLE_UUID);
            player.getPersistentData().putUUID(FLOWER_ENABLE_UUID, uuid);
        }
        if (original instanceof ServerPlayer originalServerPlayer) {
            ArtificialFlower.PLAYER_ATTRIBUTE_MAP.remove(originalServerPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ArtificialFlower.PLAYER_ATTRIBUTE_MAP.remove(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (!(event.getEntity() instanceof Player player)) return;
        MobEffect effect = event.getEffectInstance().getEffect();
        if (effect == MobEffects.HUNGER || effect == MobEffects.WITHER || effect == MobEffects.POISON) {
            if (SuperpositionHandler.hasCurio(player, EnigmaticAddonItems.REVIVAL_LEAF)) {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
