package ca.poum.afkplus;

import ca.poum.IndicatorBossBar;
import com.sakuraryoko.afkplus.player.AfkPlayerList;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class RealAfkPlusIntegration extends AfkPlusIntegration {
    IndicatorBossBar indicatorBossBar;

    // Force going through Abstract#getInstance
    RealAfkPlusIntegration(IndicatorBossBar indicatorBossBar) {
        this.indicatorBossBar = indicatorBossBar;
    }

    @Override
    public List<ServerPlayerEntity> getAfkPlayers() {
        return AfkPlayerList.getInstance().listAllAfk();
    }
}
