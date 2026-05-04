package dev.waterdog.waterdogpe.player;

import java.util.Map;
import java.util.UUID;

public interface PlayerManager {
    ProxiedPlayer getPlayer(UUID uuid);

    ProxiedPlayer getPlayer(String playerName);

    int getPlayerCount();

    Map<UUID, ProxiedPlayer> getPlayers();

    boolean registerPlayer(ProxiedPlayer player);

    void removePlayer(ProxiedPlayer player);

    void subscribePermissions(ProxiedPlayer player);
}
