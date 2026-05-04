package dev.waterdog.waterdogpe.network.serverinfo;

import dev.waterdog.waterdogpe.network.connection.client.ClientConnection;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.Set;

public interface ServerInfo {
    String getServerName();

    InetSocketAddress getAddress();

    InetSocketAddress getPublicAddress();

    Set<ProxiedPlayer> getPlayers();

    boolean matchAddress(String address, int port);

    void removeConnection(ClientConnection connection);

    void addConnection(ClientConnection connection);

    io.netty.util.concurrent.Future<ClientConnection> createConnection(ProxiedPlayer player);
}
