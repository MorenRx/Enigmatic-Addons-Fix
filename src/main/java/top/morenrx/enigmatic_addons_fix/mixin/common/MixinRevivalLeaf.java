package top.morenrx.enigmatic_addons_fix.mixin.common;

import auviotre.enigmatic.addon.contents.items.RevivalLeaf;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.*;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Map;

@Mixin(value = RevivalLeaf.class, remap = false)
public abstract class MixinRevivalLeaf {

    @Unique
    private static final String REVIVAL_GRANTED_FLIGHT = "RevivalLeafGrantedFlight";
    @Unique
    private static final String LAZY_POS_KEY = "RevivalFlightLazyPos";

    @Shadow
    public Map<Player, Integer> flyMap;

    @Shadow
    public static Omniconfig.IntParameter naturalRegenerationSpeed;

    @Final
    @Shadow
    private static List<TagKey<Block>> PLANT_SET;

    /**
     * @author MorenRx
     * @reason 优化植物查找
     */
    @Overwrite
    private boolean hasPlantBy(Player player) {
        double reach = player.getEntityReach() + 1.0;
        double maxDistSq = reach * reach;

        if (player.getPersistentData().contains(LAZY_POS_KEY)) {
            long data = player.getPersistentData().getLong(LAZY_POS_KEY);
            BlockPos lazyPos = BlockPos.of(data);
            if (lazyPos.distToCenterSqr(player.position()) < maxDistSq) {
                BlockState blockState = player.level().getBlockState(lazyPos);
                if (eaf$isPlant(blockState)) return true;
            }
        }

        BlockPos blockPos = player.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(blockPos.offset(-5, -5, -5), blockPos.offset(5, 5, 5))) {
            BlockState blockState = player.level().getBlockState(pos);
            if (!eaf$isPlant(blockState)) continue;

            if (pos.distToCenterSqr(player.position()) < maxDistSq) {
                player.getPersistentData().putLong(LAZY_POS_KEY, pos.asLong());
                return true;
            }
        }
        return false;
    }

    @Unique
    private static boolean eaf$isPlant(BlockState state) {
        if (state.isAir() || state.is(Blocks.WATER)) return false;
        if (PLANT_SET == null) return false;
        for (TagKey<Block> tag : PLANT_SET) {
            if (state.is(tag)) return true;
        }
        return false;
    }

    /**
     * @author MorenRx
     * @reason 修复飞行状态会被复苏之叶取消的问题
     */
    @Overwrite
    protected void handleFlight(Player player) {
        try {
            boolean nearPlant = this.hasPlantBy(player);

            if (nearPlant) {
                // 玩家有可能通过其他方式获得创造飞行, 所以这里需要直接检查是否能飞
                if (!player.getAbilities().mayfly) {
                    // 在这里标记飞行是来自于复苏之叶
                    player.getPersistentData().putBoolean(REVIVAL_GRANTED_FLIGHT, true);
                    player.getAbilities().mayfly = true;
                    player.onUpdateAbilities();
                }
                this.flyMap.put(player, 5);
            } else if (this.flyMap.getOrDefault(player, 0) > 1) {
                this.flyMap.put(player, this.flyMap.get(player) - 1);
            } else if (this.flyMap.getOrDefault(player, 0) == 1) {
                if (player.getPersistentData().getBoolean(REVIVAL_GRANTED_FLIGHT)) {
                    player.getPersistentData().remove(REVIVAL_GRANTED_FLIGHT);
                    if (!player.isCreative() && !player.isSpectator()) {
                        player.getAbilities().mayfly = false;
                        player.getAbilities().flying = false;
                        player.onUpdateAbilities();
                    }
                }
                player.getPersistentData().remove(LAZY_POS_KEY);
                this.flyMap.put(player, 0);
            }
        } catch (Exception exception) {
            player.getPersistentData().remove(REVIVAL_GRANTED_FLIGHT);
            player.getPersistentData().remove(LAZY_POS_KEY);
            this.flyMap.put(player, 0);
        }
    }

    /**
     * @author MorenRx
     * @reason 优化逻辑
     */
    @Overwrite
    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();

        if (entity instanceof Player player) {
            if (!SuperpositionHandler.hasCurio(player, EnigmaticItems.FABULOUS_SCROLL) && !SuperpositionHandler.hasCurio(player, EnigmaticItems.HEAVEN_SCROLL)) {
                boolean isFlying = !player.isSpectator() && !player.isCreative() && player.getAbilities().flying;
                boolean nearPlant = this.hasPlantBy(player);

                if (nearPlant && isFlying) {
                    player.addDeltaMovement(new Vec3(0.0F, -0.012F, 0.0F));
                }

                if (player.level().isClientSide()) {
                    boolean isClientLeafFlight = isFlying && (nearPlant || player.getPersistentData().contains(LAZY_POS_KEY) || this.flyMap.getOrDefault(player, 0) > 0);

                    if (player.tickCount % 12 == 0 && isClientLeafFlight && player.getPersistentData().contains(LAZY_POS_KEY)) {
                        BlockPos lazyPos = BlockPos.of(player.getPersistentData().getLong(LAZY_POS_KEY));
                        player.level().addParticle(ParticleTypes.HAPPY_VILLAGER, player.getRandomX(0.5), player.getY(), player.getRandomZ(0.5), 0, 0, 0);
                        int[] offset = {0, 1};
                        for (int x : offset) {
                            for (int y : offset) {
                                for (int z : offset) {
                                    player.level().addParticle(ParticleTypes.HAPPY_VILLAGER, lazyPos.getX() + x, lazyPos.getY() + y, lazyPos.getZ() + z, 0, 0, 0);
                                }
                            }
                        }
                    }
                } else {
                    this.handleFlight(player);
                }
            }

            if (player.tickCount % 10 == 0) {
                List<LivingEntity> poisonedEntities = player.level().getEntitiesOfClass(
                        LivingEntity.class,
                        player.getBoundingBox().inflate(5.0),
                        living -> living != player && living.isAlive() && living.hasEffect(MobEffects.POISON)
                );
                for (LivingEntity poisoned : poisonedEntities) {
                    poisoned.getPersistentData().putBoolean("RevivingPoisoned", true);
                }
            }

            if (player.tickCount % 10 == 0 && player.level() instanceof ServerLevel world) {
                BlockPos pos = player.blockPosition();
                RandomSource random = player.getRandom();
                for (BlockPos blockPos : BlockPos.betweenClosed(pos.offset(-3, -1, -3), pos.offset(3, 1, 3))) {
                    BlockState state = world.getBlockState(blockPos);
                    if (state.getBlock() instanceof CropBlock cropBlock) {
                        if (cropBlock.getMaxAge() > cropBlock.getAge(state) && random.nextInt(3) == 0) {
                            cropBlock.randomTick(state, world, blockPos, random);
                            Vec3 center = blockPos.getCenter();
                            if (random.nextInt(4) == 0) {
                                world.sendParticles(ParticleTypes.HAPPY_VILLAGER, center.x, center.y, center.z, 1, 0.2, 0.2, 0.2, 0);
                            }
                        }
                    } else if (state.getBlock() instanceof StemBlock stemBlock) {
                        if (random.nextInt(3) == 0) {
                            stemBlock.randomTick(state, world, blockPos, random);
                            Vec3 center = blockPos.getCenter();
                            if (random.nextInt(4) == 0) {
                                world.sendParticles(ParticleTypes.HAPPY_VILLAGER, center.x, center.y, center.z, 1, 0.2, 0.2, 0.2, 0);
                            }
                        }
                    }
                }
            }
        }

        if (entity.tickCount % naturalRegenerationSpeed.getValue() == 0 && entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(Math.max(0.5F, entity.getMaxHealth() / 100.0F));
        }
    }

    /**
     * @author MorenRx
     * @reason 只取消来自复苏之叶的飞行
     */
    @Overwrite
    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        if (!(context.entity() instanceof Player player)) return;

        if (player.getPersistentData().getBoolean(REVIVAL_GRANTED_FLIGHT)) {
            player.getPersistentData().remove(REVIVAL_GRANTED_FLIGHT);
            if (!player.isCreative() && !player.isSpectator()) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
        }
        player.getPersistentData().remove(LAZY_POS_KEY);
        this.flyMap.put(player, 0);
    }
}
