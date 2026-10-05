package top.morenrx.enigmatic_addons_fix.mixin.client;

import auviotre.enigmatic.addon.contents.items.VoidTome;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(VoidTome.class)
public abstract class MixinVoidTomeTooltip {

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void eaf$appendHoverText(ItemStack stack, Level worldIn, List<Component> list, TooltipFlag flagIn, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player != null && SuperpositionHandler.hasPersistentTag(player, "UsedVoidTome")) {
            list.add(Component.translatable("tooltip.enigmatic_addons_fix.void_tome_used").withStyle(ChatFormatting.GRAY));
        }
    }
}
