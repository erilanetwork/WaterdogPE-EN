package dev.waterdog.waterdogpe.player;

import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.network.connection.client.ClientConnection;
import dev.waterdog.waterdogpe.network.protocol.ProtocolVersion;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.utils.types.Permission;
import dev.waterdog.waterdogpe.utils.types.TextContainer;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.UUID;

public interface ProxiedPlayer extends CommandSender {
    String getName();

    UUID getUniqueId();

    String getXuid();

    InetSocketAddress getAddress();

    long getPing();

    boolean isConnected();

    void disconnect();

    void disconnect(String reason);

    void disconnect(TextContainer reason);

    void sendMessage(String message);

    void sendMessage(TextContainer message);

    void sendPacket(BedrockPacket packet);

    void sendPacketImmediately(BedrockPacket packet);

    ServerInfo getServerInfo();

    void connect(ServerInfo serverInfo);

    ProxyServer getProxy();

    ProtocolVersion getProtocol();

    dev.waterdog.waterdogpe.network.connection.ProxiedConnection getConnection();

    boolean hasPermission(String permission);

    void addPermission(String permission);

    boolean removePermission(String permission);

    Collection<Permission> getPermissions();

    boolean sendToFallback(ServerInfo serverInfo, dev.waterdog.waterdogpe.network.connection.handler.ReconnectReason reason, String message);

    void onDownstreamDisconnected(dev.waterdog.waterdogpe.network.connection.client.ClientConnection connection);

    void onDownstreamTimeout(ServerInfo serverInfo);

    dev.waterdog.waterdogpe.logger.Logger getLogger();
}
