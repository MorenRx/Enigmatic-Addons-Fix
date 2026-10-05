package top.morenrx.enigmatic_addons_fix.mixin.client;


import auviotre.enigmatic.addon.registries.EnigmaticAddonItems;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantOffer.class)
public class MixinMerchantOffer {

    @Inject(method = "increaseUses()V", at = @At("HEAD"), cancellable = true)
    public void increaseUses(CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player != null && SuperpositionHandler.hasCurio(player, EnigmaticAddonItems.AVARICE_RING)) {
            ci.cancel();
        }
    }
}
