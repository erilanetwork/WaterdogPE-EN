package dev.waterdog.waterdogpe.utils.config.proxy;

import dev.waterdog.waterdogpe.network.connection.codec.compression.CompressionType;
import dev.waterdog.waterdogpe.utils.config.ServerList;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public interface ProxyConfig {
    String getMotd();
    String getSubMotd();
    String getName();
    List<String> getPriorities();
    InetSocketAddress getBindAddress();
    int getMaxPlayerCount();
    Map<String, String> getForcedHosts();
    NetworkSettings getNetworkSettings();
    boolean isDebug();
    boolean isUpstreamEncryption();
    boolean isOnlineMode();
    List<Integer> getAdditionalPorts();
    String getJoinHandler();
    String getReconnectHandler();
    boolean useLoginExtras();
    boolean useCertificatePayload();
    boolean isReplaceUsernameSpaces();
    boolean enableQuery();
    boolean useFastTransfer();
    boolean injectCommands();
    CompressionType getCompression();
    void setCompression(CompressionType compression);
    int getUpstreamCompression();
    int getDownstreamCompression();
    boolean enableEducationFeatures();
    boolean enableResourcePacks();
    boolean isOverwriteClientPacks();
    boolean isForceServerPacks();
    int getPackCacheSize();
    int getIdleThreads();
    ServerList getServerList();
    List<String> getDefaultPermissions();
    Map<String, List<String>> getPlayerPermissions();
}
