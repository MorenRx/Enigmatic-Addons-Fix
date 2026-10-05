package top.morenrx.enigmatic_addons_fix.mixin.common;

import auviotre.enigmatic.addon.contents.objects.bookbag.AntiqueBagCapability;
import auviotre.enigmatic.addon.registries.EnigmaticAddonItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = AntiqueBagCapability.Wrapper.class, remap = false)
public abstract class MixinAntiqueBagCapabilityWrapper {

    @Shadow
    IItemHandlerModifiable inventory;

    @Shadow
    LivingEntity owner;

    /**
     * @author MorenRx
     * @reason 修改执行顺序
     */
    @Overwrite
    public void tickFlowers() {
        for (int i = 0; i < this.inventory.getSlots(); i++) {
            ItemStack stack = this.inventory.getStackInSlot(i);
            if (stack.is(EnigmaticAddonItems.ARTIFICIAL_FLOWER)) {
                stack.getOrCreateTag().putBoolean("FlowerEnable", false);
                stack.getOrCreateTag().putInt("FlowerBagEnable", i);
                stack.inventoryTick(this.owner.level(), this.owner, i, false);
            }
        }
    }
}
