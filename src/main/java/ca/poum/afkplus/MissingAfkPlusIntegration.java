package ca.poum.afkplus;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class MissingAfkPlusIntegration extends AfkPlusIntegration {
    // Force going through Abstract#getInstance
    MissingAfkPlusIntegration() {
    }

    @Override
    public List<ServerPlayerEntity> getAfkPlayers() {
        return List.of();
    }
}
