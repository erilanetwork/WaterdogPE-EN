package dev.waterdog.waterdogpe.network.connection.peer;

import dev.waterdog.waterdogpe.network.connection.ProxiedConnection;
import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.netty.BedrockBatchWrapper;
import org.cloudburstmc.protocol.bedrock.netty.BedrockPacketWrapper;

import javax.crypto.SecretKey;
import java.util.function.Consumer;

public interface BedrockServerSession extends ProxiedConnection {
    void disconnect(CharSequence reason, boolean hideReason);

    void disconnect(CharSequence reason);

    void disconnect();

    void setTransferQueueActive(boolean enable);

    void discardTransferQueue();

    int getSubClientId();

    BedrockPeer getPeer();

    void setLogging(boolean logging);

    void setCodec(BedrockCodec codec);

    BedrockCodec getCodec();

    void onPacket(BedrockPacketWrapper packet);

    void onBedrockBatch(BedrockBatchWrapper batch);

    void addDisconnectListener(Consumer<CharSequence> listener);

    void enableEncryption(SecretKey secretKey);

    boolean isSubClient();
}
