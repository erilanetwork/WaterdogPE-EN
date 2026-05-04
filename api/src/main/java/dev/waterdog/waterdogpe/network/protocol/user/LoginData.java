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

package dev.waterdog.waterdogpe.network.protocol.user;

import com.google.gson.JsonObject;
import com.nimbusds.jwt.SignedJWT;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.protocol.ProtocolVersion;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.auth.AuthType;
import org.cloudburstmc.protocol.bedrock.data.auth.CertificateChainPayload;
import org.cloudburstmc.protocol.bedrock.data.auth.TokenPayload;
import org.cloudburstmc.protocol.bedrock.packet.ClientCacheStatusPacket;
import org.cloudburstmc.protocol.bedrock.packet.LoginPacket;
import org.cloudburstmc.protocol.bedrock.packet.RequestChunkRadiusPacket;

import java.net.SocketAddress;
import java.security.KeyPair;
import java.util.Collections;
import java.util.UUID;

/**
 * Holds relevant information passed to the proxy on the first connection (initial) in the LoginPacket.
 */
@Slf4j
@Builder
@Getter
public class LoginData {

    private final String displayName;
    private final UUID uuid;
    private final String xuid;
    private final boolean xboxAuthed;
    private final SocketAddress address;
    private final ProtocolVersion protocol;
    private final String joinHostname;

    @Builder.Default
    private final Platform devicePlatform = Platform.UNKNOWN;
    @Builder.Default
    private final String deviceModel = null;
    @Builder.Default
    private final String deviceId = null;

    private final KeyPair keyPair;
    private final JsonObject clientData;
    private LoginPacket loginPacket;

    @Setter
    @Builder.Default
    private RequestChunkRadiusPacket chunkRadius = PlayerRewriteUtils.defaultChunkRadius;
    @Setter
    @Builder.Default
    private ClientCacheStatusPacket cachePacket = PlayerRewriteUtils.defaultCachePacket;

    private final boolean isChainPayload;

    public String getDisplayName() { return displayName; }
    public UUID getUuid() { return uuid; }
    public String getXuid() { return xuid; }
    public boolean isXboxAuthed() { return xboxAuthed; }
    public SocketAddress getAddress() { return address; }
    public ProtocolVersion getProtocol() { return protocol; }
    public String getJoinHostname() { return joinHostname; }
    public Platform getDevicePlatform() { return devicePlatform; }
    public String getDeviceModel() { return deviceModel; }
    public String getDeviceId() { return deviceId; }
    public KeyPair getKeyPair() { return keyPair; }
    public JsonObject getClientData() { return clientData; }
    public RequestChunkRadiusPacket getChunkRadius() { return chunkRadius; }
    public ClientCacheStatusPacket getCachePacket() { return cachePacket; }
    public boolean isChainPayload() { return isChainPayload; }

    public static LoginDataBuilder builder() {
        return new LoginDataBuilder();
    }

    public static class LoginDataBuilder {
        private String displayName;
        private UUID uuid;
        private String xuid;
        private boolean xboxAuthed;
        private SocketAddress address;
        private ProtocolVersion protocol;
        private String joinHostname;
        private Platform devicePlatform = Platform.UNKNOWN;
        private String deviceModel;
        private String deviceId;
        private KeyPair keyPair;
        private JsonObject clientData;
        private RequestChunkRadiusPacket chunkRadius = PlayerRewriteUtils.defaultChunkRadius;
        private ClientCacheStatusPacket cachePacket = PlayerRewriteUtils.defaultCachePacket;
        private boolean isChainPayload;

        public LoginDataBuilder displayName(String displayName) { this.displayName = displayName; return this; }
        public LoginDataBuilder uuid(UUID uuid) { this.uuid = uuid; return this; }
        public LoginDataBuilder xuid(String xuid) { this.xuid = xuid; return this; }
        public LoginDataBuilder xboxAuthed(boolean xboxAuthed) { this.xboxAuthed = xboxAuthed; return this; }
        public LoginDataBuilder address(SocketAddress address) { this.address = address; return this; }
        public LoginDataBuilder protocol(ProtocolVersion protocol) { this.protocol = protocol; return this; }
        public LoginDataBuilder joinHostname(String joinHostname) { this.joinHostname = joinHostname; return this; }
        public LoginDataBuilder devicePlatform(Platform devicePlatform) { this.devicePlatform = devicePlatform; return this; }
        public LoginDataBuilder deviceModel(String deviceModel) { this.deviceModel = deviceModel; return this; }
        public LoginDataBuilder deviceId(String deviceId) { this.deviceId = deviceId; return this; }
        public LoginDataBuilder keyPair(KeyPair keyPair) { this.keyPair = keyPair; return this; }
        public LoginDataBuilder clientData(JsonObject clientData) { this.clientData = clientData; return this; }
        public LoginDataBuilder chunkRadius(RequestChunkRadiusPacket chunkRadius) { this.chunkRadius = chunkRadius; return this; }
        public LoginDataBuilder cachePacket(ClientCacheStatusPacket cachePacket) { this.cachePacket = cachePacket; return this; }
        public LoginDataBuilder isChainPayload(boolean isChainPayload) { this.isChainPayload = isChainPayload; return this; }

        public LoginData build() {
            return new LoginData(displayName, uuid, xuid, xboxAuthed, address, protocol, joinHostname, devicePlatform, deviceModel, deviceId, keyPair, clientData, chunkRadius, cachePacket, isChainPayload);
        }
    }

    private LoginData(String displayName, UUID uuid, String xuid, boolean xboxAuthed, SocketAddress address, ProtocolVersion protocol, String joinHostname, Platform devicePlatform, String deviceModel, String deviceId, KeyPair keyPair, JsonObject clientData, RequestChunkRadiusPacket chunkRadius, ClientCacheStatusPacket cachePacket, boolean isChainPayload) {
        this.displayName = displayName;
        this.uuid = uuid;
        this.xuid = xuid;
        this.xboxAuthed = xboxAuthed;
        this.address = address;
        this.protocol = protocol;
        this.joinHostname = joinHostname;
        this.devicePlatform = devicePlatform;
        this.deviceModel = deviceModel;
        this.deviceId = deviceId;
        this.keyPair = keyPair;
        this.clientData = clientData;
        this.chunkRadius = chunkRadius;
        this.cachePacket = cachePacket;
        this.isChainPayload = isChainPayload;
    }

    /**
     * Used to construct new login packet using this.clientData and this.extraData signed by this.keyPair.
     * This method should be called everytime client data is changed. Otherwise player will join to downstream using old data.
     *
     * @return new LoginPacket.
     */
    public LoginPacket rebuildLoginPacket() {
        LoginPacket loginPacket = new LoginPacket();
        SignedJWT signedClientData = HandshakeUtils.encodeJWT(this.keyPair, this.clientData);
        loginPacket.setClientJwt(signedClientData.serialize());
        loginPacket.setProtocolVersion(this.protocol.getProtocol());
        if (isChainPayload || ProxyServer.getInstance().getConfiguration().useCertificatePayload()) {
            JsonObject extraData = HandshakeUtils.createChainExtraData(displayName, xuid, uuid);
            SignedJWT signedPayload = HandshakeUtils.createClientDataChain(this.keyPair, extraData);
            loginPacket.setAuthPayload(new CertificateChainPayload(Collections.singletonList(signedPayload.serialize()), AuthType.SELF_SIGNED));
        } else {
            SignedJWT signedPayload = HandshakeUtils.createClientDataToken(this.keyPair, displayName, xuid);
            loginPacket.setAuthPayload(new TokenPayload(signedPayload.serialize(), AuthType.SELF_SIGNED));
        }
        this.loginPacket = loginPacket;
        return loginPacket;
    }

    public LoginPacket getLoginPacket() {
        if (this.loginPacket == null) {
            this.rebuildLoginPacket();
        }
        return this.loginPacket;
    }

}
