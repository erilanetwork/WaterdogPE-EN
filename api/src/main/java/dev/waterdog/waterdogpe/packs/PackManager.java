package dev.waterdog.waterdogpe.packs;

import org.cloudburstmc.protocol.bedrock.packet.ResourcePackChunkDataPacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackChunkRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackDataInfoPacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackStackPacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePacksInfoPacket;

import java.nio.file.Path;

public interface PackManager {
    void loadPacks(Path packsDirectory);

    ResourcePacksInfoPacket getPacksInfoPacket();

    ResourcePackStackPacket getStackPacket();

    ResourcePackDataInfoPacket packInfoFromIdVer(String idVersion);

    ResourcePackChunkDataPacket packChunkDataPacket(String idVersion, ResourcePackChunkRequestPacket from);
}
