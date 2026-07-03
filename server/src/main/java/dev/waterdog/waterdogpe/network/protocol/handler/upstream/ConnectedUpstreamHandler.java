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

package dev.waterdog.waterdogpe.network.protocol.handler.upstream;

import dev.waterdog.waterdogpe.form.Form;
import dev.waterdog.waterdogpe.form.SimpleForm;
import dev.waterdog.waterdogpe.form.ModalForm;
import dev.waterdog.waterdogpe.form.CustomForm;
import dev.waterdog.waterdogpe.form.response.SimpleFormResponse;
import dev.waterdog.waterdogpe.form.response.ModalFormResponse;
import dev.waterdog.waterdogpe.form.response.CustomFormResponse;
import dev.waterdog.waterdogpe.network.connection.ProxiedConnection;
import dev.waterdog.waterdogpe.network.connection.client.ClientConnection;
import dev.waterdog.waterdogpe.network.protocol.handler.ProxyPacketHandler;
import dev.waterdog.waterdogpe.network.protocol.rewrite.RewriteMaps;
import lombok.Setter;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.netty.BedrockBatchWrapper;
import org.cloudburstmc.protocol.bedrock.packet.*;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.event.defaults.PlayerChatEvent;
import dev.waterdog.waterdogpe.network.protocol.ProtocolVersion;
import dev.waterdog.waterdogpe.network.protocol.handler.TransferCallback;
import dev.waterdog.waterdogpe.player.WaterdogPlayer;
import dev.waterdog.waterdogpe.network.protocol.Signals;
import org.cloudburstmc.protocol.common.PacketSignal;

import java.util.List;

import static dev.waterdog.waterdogpe.network.protocol.user.PlayerRewriteUtils.injectAirSubChunkResponse;

/**
 * Main handler for handling packets received from upstream.
 */
public class ConnectedUpstreamHandler extends AbstractUpstreamHandler implements ProxyPacketHandler {

    private ClientConnection targetConnection;

    public void setTargetConnection(ClientConnection targetConnection) {
        this.targetConnection = targetConnection;
    }

    public ConnectedUpstreamHandler(WaterdogPlayer player) {
        super(player);
    }

    @Override
    public void sendProxiedBatch(BedrockBatchWrapper batch) {
        if (this.targetConnection != null && this.targetConnection.isConnected()) {
            this.targetConnection.sendPacket(batch.retain());
        }
    }

    @Override
    public final PacketSignal handle(RequestChunkRadiusPacket packet) {
        this.player.getLoginData().setChunkRadius(packet);
        return PacketSignal.UNHANDLED;
    }

    @Override
    public PacketSignal handle(PlayerActionPacket packet) {
        if (packet.getAction() != PlayerActionType.DIMENSION_CHANGE_SUCCESS) {
            return PacketSignal.UNHANDLED;
        }

        TransferCallback transferCallback = this.player.getRewriteData().getTransferCallback();
        if (transferCallback != null && transferCallback.onDimChangeSuccess()) {
            return Signals.CANCEL;
        }
        return PacketSignal.UNHANDLED;
    }

    @Override
    public PacketSignal handle(SubChunkRequestPacket packet) {
        // Only the fake intermediate chunks (phase 1) are answered by us. On the target hop (phase 2) the new
        // server is wired to the client and provides the real chunks, so its sub-chunk requests must pass through.
        TransferCallback callback = this.player.getRewriteData().getTransferCallback();
        if (callback == null || callback.getPhase() != TransferCallback.TransferPhase.PHASE_1) {
            return PacketSignal.UNHANDLED;
        }
        injectAirSubChunkResponse(this.player.getConnection(), packet);
        return Signals.CANCEL;
    }

    @Override
    public final PacketSignal handle(TextPacket packet) {
        PlayerChatEvent event = new PlayerChatEvent(this.player, packet.getMessage());
        ProxyServer.getInstance().getEventManager().callEvent(event);
        packet.setMessage(event.getMessage());
        if (event.isCancelled()) {
            return Signals.CANCEL;
        }
        return PacketSignal.HANDLED;
    }

    @Override
    public final PacketSignal handle(CommandRequestPacket packet) {
        String message = packet.getCommand();
        if (this.player.getProxy().handlePlayerCommand(this.player, message)) {
            return Signals.CANCEL;
        }
        return PacketSignal.UNHANDLED;
    }

    @Override
    public PacketSignal handle(ModalFormResponsePacket packet) {
        Form form = this.player.removePendingForm(packet.getFormId());
        if (form == null) {
            return PacketSignal.UNHANDLED;
        }

        String data = packet.getFormData();
        if (data == null || data.equalsIgnoreCase("null")) {
            return Signals.CANCEL;
        }

        try {
            if (form instanceof SimpleForm simpleForm) {
                int buttonId = Integer.parseInt(data.trim());
                List<dev.waterdog.waterdogpe.form.element.Button> buttons = simpleForm.getButtons();
                if (buttonId >= 0 && buttonId < buttons.size()) {
                    SimpleFormResponse response = new SimpleFormResponse(buttons.get(buttonId), buttonId);
                    form.handleResponse(this.player, response);
                }
            } else if (form instanceof ModalForm) {
                boolean clickedTrue = Boolean.parseBoolean(data.trim());
                ModalFormResponse response = new ModalFormResponse(clickedTrue);
                form.handleResponse(this.player, response);
            } else if (form instanceof CustomForm) {
                com.google.gson.JsonArray jsonArray = com.google.gson.JsonParser.parseString(data).getAsJsonArray();
                java.util.Map<Integer, Object> responses = new java.util.HashMap<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    com.google.gson.JsonElement element = jsonArray.get(i);
                    if (element.isJsonPrimitive()) {
                        com.google.gson.JsonPrimitive prim = element.getAsJsonPrimitive();
                        if (prim.isBoolean()) responses.put(i, prim.getAsBoolean());
                        else if (prim.isNumber()) responses.put(i, prim.getAsDouble());
                        else responses.put(i, prim.getAsString());
                    } else {
                        responses.put(i, element.toString());
                    }
                }
                CustomFormResponse response = new CustomFormResponse(responses);
                form.handleResponse(this.player, response);
            }
        } catch (Exception e) {
            this.player.getLogger().debug("Error handling form response from " + this.player.getName(), e);
        }
        return Signals.CANCEL;
    }

    @Override
    public PacketSignal handle(ClientCacheBlobStatusPacket packet) {
        if (this.player.getProtocol().isBefore(ProtocolVersion.MINECRAFT_PE_1_18_30)) {
            this.player.getChunkBlobs().addAll(packet.getNaks());
        }
        return PacketSignal.UNHANDLED;
    }

    @Override
    public boolean isForceEncode() {
        return false;
    }

    @Override
    public ProxiedConnection getConnection() {
        return this.targetConnection;
    }

    @Override
    public RewriteMaps getRewriteMaps() {
        return this.player.getRewriteMaps();
    }
}
