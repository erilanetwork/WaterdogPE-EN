/*
 * Copyright 2022 WaterdogTEAM
 * Licensed under the GNU General Public License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.waterdog.waterdogpe.network.protocol.handler.downstream;

import com.nimbusds.jwt.SignedJWT;
import dev.waterdog.waterdogpe.network.connection.client.ClientConnection;
import dev.waterdog.waterdogpe.network.connection.handler.ReconnectReason;
import dev.waterdog.waterdogpe.network.protocol.handler.TransferCallback;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import lombok.extern.log4j.Log4j2;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.ScoreInfo;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraFadeInstruction;
import org.cloudburstmc.protocol.bedrock.packet.*;
import dev.waterdog.waterdogpe.event.defaults.ServerTransferEvent;
import dev.waterdog.waterdogpe.network.protocol.ProtocolVersion;
import dev.waterdog.waterdogpe.network.protocol.rewrite.types.BlockPalette;
import dev.waterdog.waterdogpe.network.protocol.rewrite.types.RewriteData;
import dev.waterdog.waterdogpe.network.protocol.rewrite.types.StartGameSettings;
import dev.waterdog.waterdogpe.player.WaterdogPlayer;
import dev.waterdog.waterdogpe.network.protocol.Signals;
import dev.waterdog.waterdogpe.utils.types.TranslationContainer;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;
import org.cloudburstmc.protocol.common.PacketSignal;

import javax.crypto.SecretKey;
import java.net.URI;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;
import java.util.Collection;
import java.util.UUID;
import java.awt.Color;

import static dev.waterdog.waterdogpe.network.protocol.user.PlayerRewriteUtils.*;

public class SwitchDownstreamHandler extends AbstractDownstreamHandler {

    /**
     * The fade that covers a fast transfer. The switch is completed once the
     * screen is dark, so FADE_IN_TICKS follows FADE_IN_SECONDS: shortening the
     * fade without shortening the wait would show the switch happening.
     * The hold and the fade out only matter if the camera is never cleared,
     * because completing the transfer clears it.
     */
    private static final float FADE_IN_SECONDS = 0.15f;
    private static final int FADE_IN_TICKS = 3;
    private static final float FADE_HOLD_SECONDS = 0.4f;
    private static final float FADE_OUT_SECONDS = 0.2f;
    private static final dev.waterdog.waterdogpe.logger.Logger logger = dev.waterdog.waterdogpe.ProxyServer.getInstance().getLogger();

    public SwitchDownstreamHandler(WaterdogPlayer player, ClientConnection connection) {
        super(player, connection);
    }

    @Override
    public final PacketSignal handle(ServerToClientHandshakePacket packet) {
        try {
            SignedJWT saltJwt = SignedJWT.parse(packet.getJwt());
            URI x5u = saltJwt.getHeader().getX509CertURL();
            ECPublicKey serverKey = EncryptionUtils.parseKey(x5u.toASCIIString());
            SecretKey key = EncryptionUtils.getSecretKey(
                    this.player.getLoginData().getKeyPair().getPrivate(),
                    serverKey,
                    Base64.getDecoder().decode(saltJwt.getJWTClaimsSet().getStringClaim("salt"))
            );
            this.connection.enableEncryption(key);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to enable encryption", e);
        }

        ClientToServerHandshakePacket clientToServerHandshake = new ClientToServerHandshakePacket();
        this.connection.sendPacket(clientToServerHandshake);
        return Signals.CANCEL;
    }

    @Override
    public final PacketSignal handle(ResourcePacksInfoPacket packet) {
        ResourcePackClientResponsePacket response = new ResourcePackClientResponsePacket();
        response.setStatus(ResourcePackClientResponsePacket.Status.HAVE_ALL_PACKS);
        this.connection.sendPacket(response);
        return Signals.CANCEL;
    }

    @Override
    public final PacketSignal handle(ResourcePackStackPacket packet) {
        ResourcePackClientResponsePacket response = new ResourcePackClientResponsePacket();
        response.setStatus(ResourcePackClientResponsePacket.Status.COMPLETED);
        this.connection.sendPacket(response);
        return Signals.CANCEL;
    }

    @Override
    public PacketSignal handle(PlayStatusPacket packet) {
        PacketSignal signal = this.onPlayStatus(packet, message -> this.player.onTransferFailure(this.connection,
                this.connection.getServerInfo(), ReconnectReason.SERVER_KICK, message), this.connection);
        if (signal == PacketSignal.UNHANDLED) {
            // PLAYER_SPAWN may arrive before phase 2 completes: route it to the transfer callback.
            return super.handle(packet);
        }
        return signal;
    }

    @Override
    public final PacketSignal handle(StartGamePacket packet) {
        RewriteData rewriteData = this.player.getRewriteData();
        rewriteData.setOriginalEntityId(packet.getRuntimeEntityId());
        rewriteData.setGameRules(packet.getGamerules());
        rewriteData.setSpawnPosition(packet.getPlayerPosition());
        rewriteData.setRotation(packet.getRotation());

        rewriteData.setBlockProperties(packet.getBlockProperties());

        if (!this.player.isConnected()) {
            this.connection.disconnect();
            this.player.disconnect("transfer disconnected");
            return Signals.CANCEL;
        }

        // A newer connect() request discards this connection asynchronously, but its START_GAME
        // may already be in flight, drop it before it hijacks the player.
        if (this.player.getPendingConnection() != this.connection) {
            this.connection.disconnect();
            logger.warning("[{}] Aborted server transfer to {} because the connection was discarded!", this.player.getName(), this.connection.getServerInfo().getServerName());
            return Signals.CANCEL;
        }

        // The client locks these settings on its first spawn: a server that disagrees with them can
        // not be joined without a full reconnect, so fail the transfer while it is still recoverable.
        StartGameSettings startGameSettings = rewriteData.getStartGameSettings();
        String incompatibilities = startGameSettings == null ? null : startGameSettings.findIncompatibilities(packet);
        if (incompatibilities != null) {
            logger.warning("[{}] Aborted server transfer to {} due to incompatible StartGame settings: {}", this.player.getName(), this.connection.getServerInfo().getServerName(), incompatibilities);
            this.player.onTransferFailure(this.connection, this.connection.getServerInfo(),
                    ReconnectReason.INCOMPATIBLE, "Incompatible server settings");
            return Signals.CANCEL;
        }

        ClientConnection oldConnection = this.player.getDownstreamConnection();
        TransferCallback transferCallback = new TransferCallback(this.player, this.connection, oldConnection.getServerInfo(), packet.getDimensionId());
        // Downstream connections run on different event loops: two of them can reach START_GAME
        // concurrently, so the transfer slot must be claimed atomically. The loser aborts here.
        if (!rewriteData.trySetTransferCallback(transferCallback)) {
            this.connection.disconnect();
            String serverName = this.connection.getServerInfo().getServerName();
            this.player.sendMessage(new TranslationContainer("waterdog.downstream.connecting", serverName));
            logger.warning("[{}] Aborted server transfer to {} because player is already being transferred!", this.player.getName(), serverName);
            return Signals.CANCEL;
        }
        transferCallback.startTimeout();

        oldConnection.getServerInfo().removeConnection(oldConnection);
        // The server the player is leaving is kept open until the client is
        // handed to this one. Closing it here cuts whatever it was still
        // sending mid batch, which a client that left in a hurry does not
        // survive.
        transferCallback.setSourceConnection(oldConnection);
        this.player.setDownstreamConnection(this.connection);
        this.connection.getServerInfo().addConnection(this.connection);
        this.player.setAcceptPlayStatus(true);

        ServerTransferEvent event = new ServerTransferEvent(this.player, oldConnection.getServerInfo(), this.connection.getServerInfo());
        this.player.getProxy().getEventManager().callEvent(event);

        this.player.getChunkBlobs().clear();

        Long2LongMap entityLinks = this.player.getEntityLinks();
        for (Long2LongMap.Entry entry : entityLinks.long2LongEntrySet()) {
            injectRemoveEntityLink(this.player.getConnection(), entry.getLongKey(), entry.getLongValue());
        }
        entityLinks.clear();

        LongSet bossbars = this.player.getBossbars();
        for (long bossbarId : bossbars) {
            injectRemoveBossbar(this.player.getConnection(), bossbarId);
        }
        bossbars.clear();

        Collection<UUID> playerList = this.player.getPlayers();
        injectRemoveAllPlayers(this.player.getConnection(), playerList);
        playerList.clear();

        LongSet entities = this.player.getEntities();
        for (long entityId : entities) {
            injectRemoveEntity(this.player.getConnection(), entityId);
        }
        entities.clear();

        Long2ObjectMap<ScoreInfo> scoreInfos = this.player.getScoreInfos();
        injectRemoveScoreInfos(this.player.getConnection(), scoreInfos);
        scoreInfos.clear();

        ObjectSet<String> scoreboards = this.player.getScoreboards();
        for (String scoreboard : scoreboards) {
            injectRemoveObjective(this.player.getConnection(), scoreboard);
        }
        scoreboards.clear();

        // ContainerClosePacket can not close the player's own inventory window. If the previous server left it
        // open the client gets stuck and refuses to open any inventory, so force it shut via the SLEEPING flag.
        injectForceCloseInventory(this.player.getConnection(), rewriteData.getEntityId());

        injectRemoveAllEffects(this.player.getConnection(), rewriteData.getEntityId(), this.player.getProtocol());
        injectClearWeather(this.player.getConnection());

        injectGameMode(this.player.getConnection(), packet.getPlayerGameType());
        injectSetDifficulty(this.player.getConnection(), packet.getDifficulty());
        injectGameRules(this.player.getConnection(), packet.getGamerules());

        this.connection.sendPacket(this.player.getLoginData().getChunkRadius());

        // A transferred client never sends this packet again, it only does so on its very first spawn.
        // A downstream that treats it as the end of the spawn sequence therefore waits for it forever
        // and never puts the player into its world, so send it on the client's behalf.
        SetLocalPlayerAsInitializedPacket initialized = new SetLocalPlayerAsInitializedPacket();
        initialized.setRuntimeEntityId(packet.getRuntimeEntityId());
        this.connection.sendPacket(initialized);

        // Client does not accept ChangeDimensionPacket when dimension is same as current dimension.
        // If we transfer between same dimensions we are attempting to do dimension change sequence which uses 2 dim changes
        // After client successfully changes dimension we receive PlayerActionPacket#DIMENSION_CHANGE_SUCCESS and continue in transfer
        int newDimension = determineDimensionId(rewriteData.getDimension(), packet.getDimensionId());

        boolean fastTransfer = event.isTransferScreenAllowed() && newDimension != packet.getDimensionId();
        transferCallback.setFastTransfer(fastTransfer);
        if (fastTransfer) {
            rewriteData.setDimension(packet.getDimensionId());
        } else {
            rewriteData.setDimension(newDimension);
        }

        if (fastTransfer) {
            CameraInstructionPacket cameraPacket = new CameraInstructionPacket();
            CameraFadeInstruction fade = new CameraFadeInstruction(
                    new CameraFadeInstruction.TimeData(FADE_IN_SECONDS, FADE_HOLD_SECONDS, FADE_OUT_SECONDS),
                    new Color(14, 24, 52)
            );
            cameraPacket.setFadeInstruction(fade);
            this.player.getConnection().sendPacketImmediately(cameraPacket);

            this.player.getConnection().setTransferQueueActive(true);

            // The player is wired to nothing until this runs: the server they
            // came from is closed above and the target only takes over when the
            // first phase completes. That gap is kept as short as the fade needs
            // to cover the switch, because a client left with nothing for too
            // long closes the connection on its own.
            this.player.getProxy().getScheduler().scheduleDelayed(() -> {
                if (rewriteData.getTransferCallback() == transferCallback) {
                    transferCallback.onDimChangeSuccess();
                    transferCallback.onDimChangeSuccess();
                }
            }, FADE_IN_TICKS);
        } else if (newDimension == packet.getDimensionId()) {
            // Transfer between different dimensions
            injectPosition(this.player.getConnection(), packet.getPlayerPosition(), packet.getRotation(), rewriteData.getEntityId());
            injectDimensionChange(this.player.getConnection(), newDimension, packet.getPlayerPosition(),
                    rewriteData.getEntityId(), player.getProtocol(), false, this.player.isSubChunkRequestMode());
            transferCallback.onDimChangeSuccess(); // Simulate two dim-change behaviour
        } else {
            injectPosition(this.player.getConnection(), packet.getPlayerPosition(), packet.getRotation(), rewriteData.getEntityId());
            rewriteData.setDimension(packet.getDimensionId());
            transferCallback.onDimChangeSuccess();
            transferCallback.onDimChangeSuccess();
        }
        return Signals.CANCEL;
    }

    @Override
    public PacketSignal handle(DisconnectPacket packet) {
        TransferCallback transferCallback = this.player.getRewriteData().getTransferCallback();
        if (transferCallback != null && transferCallback.getConnection() == this.connection) {
            // Player was already disconnected from old downstream
            transferCallback.onTransferFailed(packet.getKickMessage() == null ? "Sunucu kapandı" : packet.getKickMessage());
            return Signals.CANCEL;
        }

        // Kicked before START_GAME: the previous downstream is still fully functional and the
        // reconnect handler decides where the player goes.
        this.player.onTransferFailure(this.connection, this.connection.getServerInfo(),
                ReconnectReason.SERVER_KICK, packet.getKickMessage());
        return Signals.CANCEL;
    }
}
