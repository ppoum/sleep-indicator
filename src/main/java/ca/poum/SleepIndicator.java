package ca.poum;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SleepIndicator implements ModInitializer {
    public static final String MOD_ID = "sleep-indicator";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        IndicatorBossBar bar = new IndicatorBossBar();
        bar.registerEvents();
        LOGGER.info("Sleeping indicator bar created");

        // Register command to force refreshing the status bar (in case it desyncs somehow)
        this.registerClearCommand(bar);

    }

    private void registerClearCommand(IndicatorBossBar indicatorBossBar) {
        ArgumentBuilder<ServerCommandSource, LiteralArgumentBuilder<ServerCommandSource>> clearSubcommand = CommandManager.literal("clear").executes(context -> {
            indicatorBossBar.clear();
            context.getSource().sendFeedback(() -> Text.literal("Cleared the sleep bar"), false);
            return Command.SINGLE_SUCCESS;
        });


        CommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            commandDispatcher.register(CommandManager.literal("sleepbar").requires(source -> source.hasPermissionLevel(4)).then(clearSubcommand));
        }));
    }

}