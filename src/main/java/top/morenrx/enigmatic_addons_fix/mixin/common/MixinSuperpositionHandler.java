package top.morenrx.enigmatic_addons_fix.mixin.common;

import auviotre.enigmatic.addon.registries.EnigmaticAddonItems;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SuperpositionHandler.class, remap = false)
public abstract class MixinSuperpositionHandler {

    @Inject(method = "getCurioStack", at = @At("RETURN"), cancellable = true)
    private static void eaf$fixNullCurioStack(LivingEntity entity, Item curio, CallbackInfoReturnable<ItemStack> cir) {
        if (cir.getReturnValue() != null) return;

        if (curio == EnigmaticAddonItems.FORGER_CRYSTAL || curio == EnigmaticAddonItems.BLESS_RING) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}
