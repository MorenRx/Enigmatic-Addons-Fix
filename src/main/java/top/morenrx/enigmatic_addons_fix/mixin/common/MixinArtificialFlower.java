package top.morenrx.enigmatic_addons_fix.mixin.common;

import auviotre.enigmatic.addon.contents.items.ArtificialFlower;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.Iterator;
import java.util.Map;

@Mixin(value = ArtificialFlower.class, remap = false)
public abstract class MixinArtificialFlower {

    /**
     * @author MorenRx
     * @reason 修复死亡后失效的问题, 并进行一些优化
     */
    @SubscribeEvent
    @Overwrite
    public void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ArtificialFlower.PLAYER_ATTRIBUTE_MAP.isEmpty()) return;

        Iterator<Map.Entry<ServerPlayer, Map<ImmutableMultimap<Attribute, AttributeModifier>, Integer>>> iterator = ArtificialFlower.PLAYER_ATTRIBUTE_MAP.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<ServerPlayer, Map<ImmutableMultimap<Attribute, AttributeModifier>, Integer>> entry = iterator.next();
            ServerPlayer cachedPlayer = entry.getKey();
            Map<ImmutableMultimap<Attribute, AttributeModifier>, Integer> attributeTickMap = entry.getValue();

            if (attributeTickMap == null || attributeTickMap.isEmpty()) {
                iterator.remove();
                continue;
            }

            ServerPlayer activePlayer = eaf$resolveActivePlayer(cachedPlayer);
            if (activePlayer.isRemoved() || activePlayer.hasDisconnected()) {
                iterator.remove();
                continue;
            }

            eaf$processPlayerAttributes(activePlayer, attributeTickMap);
            if (attributeTickMap.isEmpty()) iterator.remove();
        }
    }

    @Unique
    private static ServerPlayer eaf$resolveActivePlayer(ServerPlayer player) {
        if (player.getServer() == null) return player;

        ServerPlayer onlinePlayer = player.getServer().getPlayerList().getPlayer(player.getUUID());
        if (onlinePlayer != null) return onlinePlayer;

        return player;
    }

    @Unique
    private static void eaf$processPlayerAttributes(ServerPlayer player, Map<ImmutableMultimap<Attribute, AttributeModifier>, Integer> attributeTickMap) {
        Iterator<Map.Entry<ImmutableMultimap<Attribute, AttributeModifier>, Integer>> subIterator = attributeTickMap.entrySet().iterator();

        while (subIterator.hasNext()) {
            Map.Entry<ImmutableMultimap<Attribute, AttributeModifier>, Integer> attributeEntry = subIterator.next();
            ImmutableMultimap<Attribute, AttributeModifier> attributeMap = attributeEntry.getKey();
            int tick = attributeEntry.getValue();

            if (tick <= 1) {
                player.getAttributes().removeAttributeModifiers(attributeMap);
                subIterator.remove();
            } else {
                if (!eaf$hasAllModifiers(player, attributeMap)) {
                    player.getAttributes().addTransientAttributeModifiers(attributeMap);
                }
                attributeEntry.setValue(tick - 1);
            }
        }
    }

    @Unique
    private static boolean eaf$hasAllModifiers(ServerPlayer player, ImmutableMultimap<Attribute, AttributeModifier> attributeMap) {
        for (Map.Entry<Attribute, AttributeModifier> entry : attributeMap.entries()) {
            if (!player.getAttributes().hasModifier(entry.getKey(), entry.getValue().getId())) {
                return false;
            }
        }
        return true;
    }
}
