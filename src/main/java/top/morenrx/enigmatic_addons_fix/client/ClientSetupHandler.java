package top.morenrx.enigmatic_addons_fix.client;

import auviotre.enigmatic.addon.contents.objects.bookbag.IAntiqueBagHandler;
import auviotre.enigmatic.addon.registries.EnigmaticAddonItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.morenrx.enigmatic_addons_fix.EnigmaticAddonsFix;
import top.morenrx.enigmatic_addons_fix.helper.AntiqueBagHelper;

@Mod.EventBusSubscriber(modid = EnigmaticAddonsFix.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetupHandler {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(EnigmaticAddonItems.ANTIQUE_BAG, EnigmaticAddonsFix.parse("has_flower"), (stack, level, living, i) -> {
            LivingEntity entity = living != null ? living : Minecraft.getInstance().player;
            return AntiqueBagHelper.getCapability(entity).map(IAntiqueBagHandler::hasFlower).orElse(false) ? 1.0F : 0.0F;
        }));
    }
}
