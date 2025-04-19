package ca.poum;

import ca.poum.afkplus.AfkPlusIntegration;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

public class IndicatorBossBar {
    private static final Style MISSING_STYLE = Style.EMPTY.withColor(Formatting.YELLOW).withItalic(true);
    private static final Style SLEEPING_STYLE = Style.EMPTY.withColor(Formatting.BLUE).withItalic(true);
    ServerBossBar bossBar;
    int totalPlayerCount;
    int sleepingCount;
    int targetSleepPercentage;
    AfkPlusIntegration afkIntegration;

    public IndicatorBossBar() {
        bossBar = new ServerBossBar(Text.empty(), BossBar.Color.YELLOW, BossBar.Style.PROGRESS);
        totalPlayerCount = 0;
        sleepingCount = 0;
        targetSleepPercentage = 100;
        afkIntegration = AfkPlusIntegration.getInstance(this);
    }

    public void clear() {
        sleepingCount = 0;
        targetSleepPercentage = 100;
        this.updateBar();
    }

    public void registerEvents() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            this.addPlayer(handler.player);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            this.removePlayer(handler.player);
        });

        EntitySleepEvents.START_SLEEPING.register((livingEntity, blockPos) -> {
            if (!(livingEntity instanceof ServerPlayerEntity player)) {
                // Non-player entity sleeping? Ignore
                return;
            }

            this.setTargetSleepPercentage(player.server.getGameRules().getInt(GameRules.PLAYERS_SLEEPING_PERCENTAGE));
            this.addPlayerSleeping();
        });

        EntitySleepEvents.STOP_SLEEPING.register((livingEntity, blockPos) -> {
            if (!(livingEntity instanceof ServerPlayerEntity player)) {
                // Non-player entity sleeping? Ignore
                return;
            }
            this.setTargetSleepPercentage(player.server.getGameRules().getInt(GameRules.PLAYERS_SLEEPING_PERCENTAGE));
            this.removePlayerSleeping();

        });
    }

    private void addPlayer(ServerPlayerEntity player) {
        this.totalPlayerCount++;
        this.bossBar.addPlayer(player);
        this.updateBar();
    }

    private void removePlayer(ServerPlayerEntity player) {
        if (this.totalPlayerCount > 0) {
            this.totalPlayerCount--;
        } else {
            SleepIndicator.LOGGER.warn("Tried decrementing total player count below 0");
        }
        this.bossBar.removePlayer(player);
        this.updateBar();
    }

    public void addPlayerSleeping() {
        this.sleepingCount++;
        this.updateBar();
    }

    public void removePlayerSleeping() {
        if (this.sleepingCount > 0) {
            this.sleepingCount--;
        } else {
            SleepIndicator.LOGGER.warn("Tried decrementing sleeping player count below 0");
        }
        this.updateBar();
    }

    public void setTargetSleepPercentage(int target) {
        this.targetSleepPercentage = target;
        this.updateBar();
    }

    public void updateBar() {
        if (this.sleepingCount == 0) {
            this.bossBar.setVisible(false);
            return;
        }

        int afkCount = this.afkIntegration.getAfkPlayers().size();
        int activePlayerCount = this.totalPlayerCount - afkCount;
        int targetSleepingCount = Math.ceilDiv(activePlayerCount * this.targetSleepPercentage, 100);

        if (this.sleepingCount >= targetSleepingCount) {
            // Enough players sleeping
            this.bossBar.setColor(BossBar.Color.BLUE);
            this.bossBar.setName(Text.literal("Sleeping through the night...").setStyle(SLEEPING_STYLE));
            this.bossBar.setPercent(1f);
            this.bossBar.setVisible(true);
        } else {
            // Missing players
            this.bossBar.setColor(BossBar.Color.YELLOW);
            this.bossBar.setName(Text.literal(String.format("%d out of %d players sleeping", this.sleepingCount, targetSleepingCount)).setStyle(MISSING_STYLE));
            this.bossBar.setPercent((float) this.sleepingCount / targetSleepingCount);
            this.bossBar.setVisible(true);
        }

    }

}
