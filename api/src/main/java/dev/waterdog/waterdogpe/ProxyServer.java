package dev.waterdog.waterdogpe;

import dev.waterdog.waterdogpe.command.CommandMap;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.ConsoleCommandSender;
import dev.waterdog.waterdogpe.event.EventManager;
import dev.waterdog.waterdogpe.logger.Logger;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfoMap;
import dev.waterdog.waterdogpe.packs.PackManager;
import dev.waterdog.waterdogpe.player.PlayerManager;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import dev.waterdog.waterdogpe.plugin.PluginManager;
import dev.waterdog.waterdogpe.scheduler.WaterdogScheduler;
import dev.waterdog.waterdogpe.security.SecurityManager;
import dev.waterdog.waterdogpe.utils.config.LangConfig;
import dev.waterdog.waterdogpe.utils.config.proxy.NetworkSettings;
import dev.waterdog.waterdogpe.utils.config.proxy.ProxyConfig;
import dev.waterdog.waterdogpe.utils.types.TextContainer;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface ProxyServer {
    static ProxyServer getInstance() {
        return ProxyServerHolder.getInstance();
    }

    static void setInstance(ProxyServer instance) {
        ProxyServerHolder.setInstance(instance);
    }

    class ProxyServerHolder {
        private static ProxyServer instance;
        public static ProxyServer getInstance() { return instance; }
        public static void setInstance(ProxyServer inst) { instance = inst; }
    }

    Path getDataPath();

    Path getPluginPath();

    Logger getLogger();

    VersionInfo getVersionInfo();

    WaterdogScheduler getScheduler();

    PlayerManager getPlayerManager();

    PluginManager getPluginManager();

    EventManager getEventManager();

    PackManager getPackManager();

    CommandMap getCommandMap();

    ConsoleCommandSender getConsoleSender();

    SecurityManager getSecurityManager();

    ProxyConfig getConfiguration();

    NetworkSettings getNetworkSettings();

    LangConfig getLanguageConfig();

    ProxiedPlayer getPlayer(UUID uuid);

    ProxiedPlayer getPlayer(String playerName);

    Map<UUID, ProxiedPlayer> getPlayers();

    ServerInfo getServerInfo(String serverName);

    ServerInfo getServerInfo(String address, int port);

    Collection<ServerInfo> getServers();

    ServerInfoMap getServerInfoMap();

    boolean registerServerInfo(ServerInfo serverInfo);

    ServerInfo removeServerInfo(String serverName);

    boolean isRunning();

    int getCurrentTick();

    void shutdown();

    String translate(TextContainer textContainer);

    boolean dispatchCommand(CommandSender sender, String command);

    dev.waterdog.waterdogpe.network.connection.handler.IForcedHostHandler getForcedHostHandler();

    dev.waterdog.waterdogpe.network.connection.handler.IJoinHandler getJoinHandler();

    dev.waterdog.waterdogpe.network.connection.handler.IReconnectHandler getReconnectHandler();

    io.netty.channel.EventLoopGroup getWorkerEventLoopGroup();

    dev.waterdog.waterdogpe.network.NetworkMetrics getNetworkMetrics();

    dev.waterdog.waterdogpe.network.serverinfo.ServerInfo getForcedHost(String domain);

    boolean handlePlayerCommand(ProxiedPlayer player, String command);
}
