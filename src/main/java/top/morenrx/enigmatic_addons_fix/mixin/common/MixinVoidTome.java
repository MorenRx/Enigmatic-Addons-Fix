package top.morenrx.enigmatic_addons_fix.mixin.common;

import auviotre.enigmatic.addon.contents.items.VoidTome;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.morenrx.enigmatic_addons_fix.network.S2CPlayerConsumeStatusMessage;

@Mixin(VoidTome.class)
public abstract class MixinVoidTome {

    @Inject(method = "use", at = @At("TAIL"))
    private void eaf$onUse(Level world, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (player instanceof ServerPlayer serverPlayer) {
            S2CPlayerConsumeStatusMessage.sync(serverPlayer);
        }
    }
}
