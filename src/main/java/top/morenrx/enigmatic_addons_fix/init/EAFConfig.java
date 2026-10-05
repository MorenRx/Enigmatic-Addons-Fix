package top.morenrx.enigmatic_addons_fix.init;

import com.aizistral.omniconfig.Configuration;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

public class EAFConfig {
    private static final String COMMON_CONFIG_NAME = "enigmaticaddonsfix-common";
    private static final String COMMON_CONFIG_VERSION = "1.0.0";
    public static OmniconfigWrapper COMMON_CONFIG;

    public static Omniconfig.EnumParameter<MiningRestrictionMode> despairInsigniaMiningMode;
    public static Omniconfig.EnumParameter<MiningRestrictionMode> adventureCharmMiningMode;
    public static Omniconfig.BooleanParameter disableCursedScrollLossXPOnDeath;
    public static Omniconfig.BooleanParameter disableCursedScrollReduceOrbXP;

    public static void init() {
        COMMON_CONFIG = OmniconfigWrapper.setupBuilder(COMMON_CONFIG_NAME, true, COMMON_CONFIG_VERSION);
        COMMON_CONFIG.pushVersioningPolicy(Configuration.VersioningPolicy.AGGRESSIVE);
        COMMON_CONFIG.pushTerminateNonInvokedKeys(false);

        loadCommon(COMMON_CONFIG);

        COMMON_CONFIG.setReloadable();
        COMMON_CONFIG.build();
    }

    private static void loadCommon(OmniconfigWrapper builder) {
        builder.loadConfigFile();
        builder.forceSynchronized(false);

        builder.pushCategory("MiningRestrictions", "配置项说明:\nALWAYS_ALLOW: 始终允许挖掘\nCROUCH_ALLOW: 下蹲时允许挖掘\nNEVER_ALLOW: 完全不允许挖掘");

        despairInsigniaMiningMode = builder
                .comment("绝望者证章的挖掘限制")
                .nosync()
                .getEnum("DespairInsigniaMiningMode", MiningRestrictionMode.NEVER_ALLOW);

        adventureCharmMiningMode = builder
                .comment("冒险家之证的挖掘限制")
                .nosync()
                .getEnum("AdventureCharmMiningMode", MiningRestrictionMode.CROUCH_ALLOW);

        builder.popCategory();

        builder.pushCategory("CursedXPScroll", "无知诅咒卷轴设置");

        disableCursedScrollLossXPOnDeath = builder
                .comment("禁止死亡时清空无知诅咒卷轴内的经验")
                .nosync()
                .getBoolean("DisableCursedScrollLossXPOnDeath", false);

        disableCursedScrollReduceOrbXP = builder
                .comment("禁止携带无知诅咒卷轴时减少经验的获取量")
                .nosync()
                .getBoolean("DisableCursedScrollReduceOrbXP", false);

        builder.popCategory();
    }

    public static void reload() {
        COMMON_CONFIG.config.load();
        COMMON_CONFIG.invokationMap.values().forEach(param -> param.invoke(COMMON_CONFIG.config));
    }

    public enum MiningRestrictionMode {
        ALWAYS_ALLOW,
        CROUCH_ALLOW,
        NEVER_ALLOW
    }
}
