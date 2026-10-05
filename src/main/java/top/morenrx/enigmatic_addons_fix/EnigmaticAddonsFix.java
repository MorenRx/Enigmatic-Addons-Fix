package top.morenrx.enigmatic_addons_fix;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import top.morenrx.enigmatic_addons_fix.command.EAFCommands;
import top.morenrx.enigmatic_addons_fix.init.EAFConfig;
import top.morenrx.enigmatic_addons_fix.init.EAFNetwork;

@Mod(EnigmaticAddonsFix.MODID)
public class EnigmaticAddonsFix {
    public static final String MODID = "enigmatic_addons_fix";
    private static final Logger LOGGER = LogUtils.getLogger();

    public EnigmaticAddonsFix(FMLJavaModLoadingContext context) {
        EAFConfig.init();
        EAFNetwork.init();
        MinecraftForge.EVENT_BUS.register(EAFCommands.class);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static ResourceLocation id(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }

    public static ResourceLocation parse(String location) {
        return new ResourceLocation(location);
    }
}
