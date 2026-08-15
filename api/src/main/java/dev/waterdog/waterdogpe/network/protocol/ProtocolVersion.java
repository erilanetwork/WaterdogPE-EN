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

package dev.waterdog.waterdogpe.network.protocol;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v1001.Bedrock_v1001;
import org.cloudburstmc.protocol.bedrock.codec.v2168.Bedrock_v2168;
import org.cloudburstmc.protocol.bedrock.codec.v2168.Bedrock_v2168_hotfix4;
import org.cloudburstmc.protocol.bedrock.codec.v898.Bedrock_v898;
import org.cloudburstmc.protocol.bedrock.codec.v924.Bedrock_v924;
import org.cloudburstmc.protocol.bedrock.codec.v944.Bedrock_v944;
import org.cloudburstmc.protocol.bedrock.codec.v975.Bedrock_v975;

@ToString(exclude = {"defaultCodec", "bedrockCodec"})
public enum ProtocolVersion {

    MINECRAFT_PE_1_21_130(898, Bedrock_v898.CODEC),
    MINECRAFT_PE_1_26_0(924, Bedrock_v924.CODEC),
    MINECRAFT_PE_1_26_10(944, Bedrock_v944.CODEC),
    MINECRAFT_PE_1_26_20(975, Bedrock_v975.CODEC),
    MINECRAFT_PE_1_26_30(1001, Bedrock_v1001.CODEC),
    MINECRAFT_PE_1_26_40(2168, Bedrock_v2168.CODEC),
    MINECRAFT_PE_1_26_44(2168, 2169, Bedrock_v2168_hotfix4.CODEC, "26.44"), // this version has not bumped protocol number on client side
    ;

    private static final ProtocolVersion[] VALUES = values();
    private static final Int2ObjectMap<ProtocolVersion> VERSIONS = new Int2ObjectOpenHashMap<>();
    static {
        for (ProtocolVersion version : values()) {
            VERSIONS.putIfAbsent(version.getProtocol(), version);
        }
    }

    @Getter
    private final int protocol;
    @Getter
    private final int protocolInternal;

    @Getter
    private final BedrockCodec defaultCodec;
    @Setter
    private BedrockCodec bedrockCodec;

    ProtocolVersion(int protocol, BedrockCodec codec) {
        this(protocol, protocol, codec);
    }

    ProtocolVersion(int protocol, int protocolInternal, BedrockCodec codec) {
        this.protocol = protocol;
        this.protocolInternal = protocolInternal;
        this.defaultCodec = codec;
    }

    public boolean isBefore(ProtocolVersion version) {
        return this.protocolInternal < version.protocolInternal;
    }

    public boolean isBeforeOrEqual(ProtocolVersion version) {
        return this.protocolInternal <= version.protocolInternal;
    }

    public boolean isAfter(ProtocolVersion version) {
        return this.protocolInternal > version.protocolInternal;
    }

    public boolean isAfterOrEqual(ProtocolVersion version) {
        return this.protocolInternal >= version.protocolInternal;
    }

    public int getRaknetVersion() {
        return this.getCodec().getRaknetProtocolVersion();
    }

    public BedrockCodec getCodec() {
        return this.bedrockCodec == null ? this.defaultCodec : this.bedrockCodec;
    }

    public String getMinecraftVersion() {
        return this.getCodec().getMinecraftVersion();
    }

    public static ProtocolVersion latest() {
        return VALUES[VALUES.length - 1];
    }

    public static ProtocolVersion oldest() {
        return VALUES[0];
    }

    public static ProtocolVersion get(int protocol) {
        return VERSIONS.get(protocol);
    }
}
