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

package dev.waterdog.waterdogpe.network.connection.codec.packet;

import dev.waterdog.waterdogpe.network.NetworkMetrics;
import dev.waterdog.waterdogpe.network.protocol.registry.FakeDefinitionRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.cloudburstmc.protocol.bedrock.PacketDirection;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.compat.BedrockCompat;
import org.cloudburstmc.protocol.bedrock.data.EncodingSettings;
import org.cloudburstmc.protocol.bedrock.data.PacketRecipient;
import org.cloudburstmc.protocol.bedrock.netty.BedrockBatchWrapper;
import org.cloudburstmc.protocol.bedrock.netty.BedrockPacketWrapper;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.UnknownPacket;

import java.util.List;

import static java.util.Objects.requireNonNull;

public abstract class BedrockPacketCodec extends MessageToMessageCodec<BedrockBatchWrapper, BedrockBatchWrapper> {
    private static final dev.waterdog.waterdogpe.logger.Logger logger = dev.waterdog.waterdogpe.ProxyServer.getInstance().getLogger();
    public static final String NAME = "bedrock-packet-codec";

    private BedrockCodec codec = BedrockCompat.CODEC;
    private BedrockCodecHelper helper = createDefaultHelper(codec);

    private static BedrockCodecHelper createDefaultHelper(BedrockCodec codec) {
        BedrockCodecHelper helper = codec.createHelper();
        applyFallbackRegistries(helper);
        return helper;
    }

    private static void applyFallbackRegistries(BedrockCodecHelper helper) {
        if (helper.getBlockDefinitions() == null) {
            helper.setBlockDefinitions(FakeDefinitionRegistry.createBlockRegistry());
        }
        if (helper.getItemDefinitions() == null) {
            helper.setItemDefinitions(FakeDefinitionRegistry.createItemRegistry());
        }
    }

    public BedrockCodec getCodec() { return codec; }
    public BedrockCodecHelper getHelper() { return helper; }

    private boolean alwaysDecode;
    private PacketRecipient inboundRecipient;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        PacketDirection direction = ctx.channel().attr(PacketDirection.ATTRIBUTE).get();
        if (direction == PacketDirection.CLIENT_BOUND) {
            this.alwaysDecode = true; // packets from client can be always decoded
        }
        this.inboundRecipient = direction.getInbound();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, BedrockBatchWrapper msg, List<Object> out) {
        if (msg.isModified() || msg.getUncompressed() == null) {
            int passedThrough = 0;
            int encodedPackets = 0;
            for (BedrockPacketWrapper packet : msg.getPackets()) {
                if (this.encode(ctx, packet)) {
                    passedThrough++;
                } else {
                    encodedPackets++;
                }
            }
            this.recordMetrics(ctx, passedThrough, encodedPackets);
        }
        out.add(msg.retain());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, BedrockBatchWrapper msg, List<Object> out) {
        for (BedrockPacketWrapper packet : msg.getPackets()) {
            this.decode(ctx, packet);
        }
        out.add(msg.retain());
    }

    protected final boolean encode(ChannelHandlerContext ctx, BedrockPacketWrapper wrapper) {
        if (wrapper.getPacketBuffer() != null) {
            // We have a pre-encoded packet buffer, just use that.
            return true;
        }

        ByteBuf buf = ctx.alloc().buffer(128);
        try {
            BedrockPacket packet = wrapper.getPacket();
            wrapper.setPacketId(getPacketId(packet));
            this.encodeHeader(buf, wrapper);
            this.codec.tryEncode(helper, buf, packet);
            wrapper.setPacketBuffer(buf.retain());
        } catch (Throwable t) {
            logger.error("Error encoding packet {}", wrapper.getPacket(), t);
        } finally {
            buf.release();
        }
        return false;
    }

    protected final void decode(ChannelHandlerContext ctx, BedrockPacketWrapper wrapper) {
        if (wrapper.getPacketBuffer() == null) {
            throw new IllegalStateException("Packet has no encoded buffer");
        }

        ByteBuf msg = wrapper.getPacketBuffer().retainedSlice();
        try {
            int index = msg.readerIndex();
            this.decodeHeader(msg, wrapper);
            wrapper.setHeaderLength(msg.readerIndex() - index);
            if (this.alwaysDecode) { // Otherwise, we are decoding at other place
                wrapper.setPacket(this.codec.tryDecode(helper, msg, wrapper.getPacketId(), this.inboundRecipient));
            }
        } catch (Throwable t) {
            logger.error("Failed to decode packet", t);
            throw t;
        } finally {
            msg.release();
        }
    }

    public abstract void encodeHeader(ByteBuf buf, BedrockPacketWrapper msg);

    public abstract void decodeHeader(ByteBuf buf, BedrockPacketWrapper msg);

    private void recordMetrics(ChannelHandlerContext ctx, int passedThought, int encodedPackets) {
        NetworkMetrics metrics = ctx.channel().attr(NetworkMetrics.ATTRIBUTE).get();
        if (metrics == null) {
            return;
        }

        PacketDirection direction = ctx.channel().attr(PacketDirection.ATTRIBUTE).get();
        if (passedThought > 0) {
            metrics.passedThroughPackets(passedThought, direction);
        }

        if (encodedPackets > 0) {
            metrics.encodedPackets(passedThought, direction);
        }
    }

    public final int getPacketId(BedrockPacket packet) {
        if (packet instanceof UnknownPacket) {
            return ((UnknownPacket) packet).getPacketId();
        }
        var definition = this.codec.getPacketDefinition(packet.getClass());
        if (definition == null) {
            throw new IllegalArgumentException("Packet " + packet.getClass().getSimpleName() + " is not registered in codec " + this.codec.getMinecraftVersion());
        }
        return definition.getId();
    }

    public final BedrockPacketCodec setCodecHelper(BedrockCodec codec, BedrockCodecHelper helper) {
        this.codec = requireNonNull(codec, "Codec cannot be null");
        this.helper = requireNonNull(helper, "Helper can not be null");
        applyFallbackRegistries(this.helper);

        switch (this.inboundRecipient) {
            case CLIENT -> this.helper.setEncodingSettings(EncodingSettings.UNLIMITED); // we trust downstream
            case SERVER -> this.helper.setEncodingSettings(EncodingSettings.SERVER);
        }
        return this;
    }

    public BedrockPacketCodec setAlwaysDecode(boolean alwaysDecode) {
        this.alwaysDecode = alwaysDecode;
        return this;
    }
}
