package dev.waterdog.waterdogpe.security;

import dev.waterdog.waterdogpe.network.protocol.user.HandshakeEntry;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public interface SecurityManager {
    void blockAddress(InetAddress address, long time, TimeUnit unit);

    void unblockAddress(InetAddress address);

    boolean isAddressBlocked(InetAddress address);

    boolean onConnectionCreated(SocketAddress address);

    boolean onLoginAttempt(SocketAddress address);

    String onLoginFailed(SocketAddress address, HandshakeEntry handshakeEntry, Throwable throwable, String reason);

    void onConnectionError(SocketAddress address, Throwable cause);
}
