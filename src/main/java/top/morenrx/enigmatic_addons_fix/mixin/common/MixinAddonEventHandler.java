package top.morenrx.enigmatic_addons_fix.mixin.common;

import auviotre.enigmatic.addon.handlers.AddonEventHandler;
import auviotre.enigmatic.addon.registries.EnigmaticAddonItems;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.morenrx.enigmatic_addons_fix.init.EAFConfig;
import top.morenrx.enigmatic_addons_fix.init.EAFConfig.MiningRestrictionMode;

@Mixin(value = AddonEventHandler.class, remap = false)
public abstract class MixinAddonEventHandler {

    @Redirect(
            method = "miningStuff",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/aizistral/enigmaticlegacy/handlers/SuperpositionHandler;hasCurio(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/Item;)Z"
            )
    )
    private boolean enigmaticAddonsFix$redirectMiningHasCurio(LivingEntity entity, Item item) {
        if (item == EnigmaticAddonItems.ADVENTURE_CHARM || item == EnigmaticAddonItems.DESPAIR_INSIGNIA)
            return false;

        return SuperpositionHandler.hasCurio(entity, item);
    }

    @Inject(method = "miningStuff", at = @At("TAIL"))
    private void enigmaticAddonsFix$applyConfiguredMiningRestrictions(PlayerEvent.BreakSpeed event, CallbackInfo ci) {
        Player player = event.getEntity();
        if (player == null) return;

        if (SuperpositionHandler.hasCurio(player, EnigmaticAddonItems.DESPAIR_INSIGNIA)) {
            MiningRestrictionMode mode = EAFConfig.despairInsigniaMiningMode.getValue();
            if (enigmaticAddonsFix$shouldCancelMining(player, mode)) {
                event.setCanceled(true);
                return;
            }
        }

        if (SuperpositionHandler.hasCurio(player, EnigmaticAddonItems.ADVENTURE_CHARM)) {
            MiningRestrictionMode mode = EAFConfig.adventureCharmMiningMode.getValue();
            if (enigmaticAddonsFix$shouldCancelMining(player, mode)) {
                event.setCanceled(true);
            }
        }
    }

    @Unique
    private static boolean enigmaticAddonsFix$shouldCancelMining(Player player, MiningRestrictionMode mode) {
        return switch (mode) {
            case ALWAYS_ALLOW -> false;
            case CROUCH_ALLOW -> !player.isCrouching();
            case NEVER_ALLOW -> true;
        };
    }

    @Redirect(
            method = "onConfirmedDeath",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/aizistral/enigmaticlegacy/handlers/SuperpositionHandler;hasCurio(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/Item;)Z"
            )
    )
    private boolean enigmaticAddonsFix$redirectHasCurioOnDeath(LivingEntity entity, Item item) {
        if (item == EnigmaticAddonItems.CURSED_XP_SCROLL && EAFConfig.disableCursedScrollLossXPOnDeath.getValue())
            return false;

        return SuperpositionHandler.hasCurio(entity, item);
    }

    @Inject(method = "onXPOrb", at = @At("HEAD"), cancellable = true)
    private void enigmaticAddonsFix$onXPOrb(PlayerXpEvent.PickupXp event, CallbackInfo ci) {
        if (EAFConfig.disableCursedScrollReduceOrbXP.getValue())
            ci.cancel();
    }
}
