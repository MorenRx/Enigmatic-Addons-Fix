package top.morenrx.enigmatic_addons_fix.mixin.common;

import com.aizistral.enigmaticlegacy.brewing.AbstractBrewingRecipe;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.HashMap;

@Mixin(value = AbstractBrewingRecipe.class, remap = false)
public abstract class MixinAbstractBrewingRecipe {

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/HashMap;containsKey(Ljava/lang/Object;)Z"
            )
    )
    private boolean enigmaticAddonsFix$ensureListAndProceed(HashMap<Object, Object> map, Object key) {
        synchronized (AbstractBrewingRecipe.recipeMap) {
            if (key instanceof ResourceLocation id) {
                AbstractBrewingRecipe.recipeMap.computeIfAbsent(id, k -> new ArrayList<>());
            }
            return true;
        }
    }

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/HashMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private Object enigmaticAddonsFix$synchronizedPut(HashMap<Object, Object> map, Object key, Object value) {
        synchronized (AbstractBrewingRecipe.recipeMap) {
            return map.put(key, value);
        }
    }
}