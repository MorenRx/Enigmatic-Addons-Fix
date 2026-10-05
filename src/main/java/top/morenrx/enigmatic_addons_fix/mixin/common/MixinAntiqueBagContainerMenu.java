package top.morenrx.enigmatic_addons_fix.mixin.common;

import auviotre.enigmatic.addon.contents.gui.AntiqueBagContainerMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import top.morenrx.enigmatic_addons_fix.network.S2CAntiqueBagSyncMessage;

@Mixin(AntiqueBagContainerMenu.class)
public abstract class MixinAntiqueBagContainerMenu extends AbstractContainerMenu {

    protected MixinAntiqueBagContainerMenu(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        if (player instanceof ServerPlayer serverPlayer) {
            S2CAntiqueBagSyncMessage.sync(serverPlayer);
        }
    }
}
