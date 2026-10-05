package top.morenrx.enigmatic_addons_fix.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.morenrx.enigmatic_addons_fix.init.EAFConfig;

public class EAFCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
            Commands.literal("eaf")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("reload")
                    .executes(ctx -> executeReload(ctx.getSource()))
                )
        );

        dispatcher.register(
            Commands.literal("enigmaticaddonsfix")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("reload")
                    .executes(ctx -> executeReload(ctx.getSource()))
                )
        );
    }

    private static int executeReload(CommandSourceStack source) {
        EAFConfig.reload();
        source.sendSuccess(() -> Component.literal("[EnigmaticAddonsFix] ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal("配置已热重载").withStyle(ChatFormatting.GREEN)), true);
        return 1;
    }
}
