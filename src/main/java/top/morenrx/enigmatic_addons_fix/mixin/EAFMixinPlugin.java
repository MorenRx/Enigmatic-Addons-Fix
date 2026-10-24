package top.morenrx.enigmatic_addons_fix.mixin;

import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class EAFMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String simpleName = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
        return switch (simpleName) {
            case "MixinSuperpositionHandler" -> {
                ModFileInfo enigmaticAddonsInfo = FMLLoader.getLoadingModList().getModFileById("enigmaticaddons");
                if (enigmaticAddonsInfo == null) yield false;
                ArtifactVersion currentVersion = new DefaultArtifactVersion(enigmaticAddonsInfo.versionString());
                ArtifactVersion targetVersion = new DefaultArtifactVersion("1.2.7");
                boolean isNewVersion = currentVersion.compareTo(targetVersion) >= 0;
                if (!isNewVersion) yield false;
                // 用于过滤Beta版1.2.7
                Path crystalClass = enigmaticAddonsInfo.getFile().findResource("auviotre/enigmatic/addon/contents/items/ForgerCrystal.class");
                yield Files.exists(crystalClass);
            }
            default -> true;
        };
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
