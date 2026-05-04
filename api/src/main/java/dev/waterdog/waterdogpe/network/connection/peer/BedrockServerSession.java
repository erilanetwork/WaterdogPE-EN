package dev.waterdog.waterdogpe.network.connection.peer;

import dev.waterdog.waterdogpe.network.connection.ProxiedConnection;

public interface BedrockServerSession extends ProxiedConnection {
    void disconnect(CharSequence reason, boolean hideReason);

    void setTransferQueueActive(boolean enable);

    int getSubClientId();
}
