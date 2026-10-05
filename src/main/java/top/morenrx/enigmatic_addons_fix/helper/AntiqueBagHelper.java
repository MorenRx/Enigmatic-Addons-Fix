package top.morenrx.enigmatic_addons_fix.helper;

import auviotre.enigmatic.addon.contents.objects.bookbag.AntiqueBagCapability;
import auviotre.enigmatic.addon.contents.objects.bookbag.IAntiqueBagHandler;
import auviotre.enigmatic.addon.handlers.SuperAddonHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AntiqueBagHelper {

    @NotNull
    public static LazyOptional<IAntiqueBagHandler> getCapability(@Nullable LivingEntity entity) {
        if (!(entity instanceof Player player)) return LazyOptional.empty();

        LazyOptional<IAntiqueBagHandler> capability = SuperAddonHandler.getCapability(player, AntiqueBagCapability.INVENTORY);
        return capability != null ? capability : LazyOptional.empty();
    }

    @Nullable
    public static CompoundTag getAntiqueBagTag(@Nullable LivingEntity entity) {
        LazyOptional<IAntiqueBagHandler> capability = getCapability(entity);
        if (!capability.isPresent()) return null;

        IAntiqueBagHandler handler = capability.resolve().orElse(null);
        if (handler == null) return null;

        return handler.writeTag() instanceof CompoundTag compoundTag
                ? compoundTag
                : null;
    }

}
