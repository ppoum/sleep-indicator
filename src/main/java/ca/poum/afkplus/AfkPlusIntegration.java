package ca.poum.afkplus;

import ca.poum.IndicatorBossBar;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public abstract class AfkPlusIntegration {
    public static AfkPlusIntegration getInstance(IndicatorBossBar indicatorBossBar) {
        if (FabricLoader.getInstance().isModLoaded("afkplus")) {
            return new RealAfkPlusIntegration(indicatorBossBar);
        } else {
            return new MissingAfkPlusIntegration();
        }
    }

    public abstract List<ServerPlayerEntity> getAfkPlayers();
}

