package top.morenrx.enigmatic_addons_fix.mixin.common;

import com.aizistral.enigmaticlegacy.items.IchorBottle;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.morenrx.enigmatic_addons_fix.network.S2CPlayerConsumeStatusMessage;

@Mixin(value = IchorBottle.class, remap = false)
public abstract class MixinIchorBottle {

    @Inject(method = "onConsumed", at = @At("TAIL"))
    private void eaf$onConsumed(Level worldIn, Player player, ItemStack potion, CallbackInfo ci) {
        if (player instanceof ServerPlayer serverPlayer) {
            S2CPlayerConsumeStatusMessage.sync(serverPlayer);
        }
    }
}
