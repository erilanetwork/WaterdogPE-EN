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

package dev.waterdog.waterdogpe.network.protocol.rewrite.types;

import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.protocol.handler.TransferCallback;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.data.BlockPropertyData;
import org.cloudburstmc.protocol.bedrock.data.GameRuleData;

import java.util.List;

/**
 * Rewrite data of a present player.
 * Holds both the client-known entityId and the downstream-known clientId.
 * Important when interacting with packets, as different packet targets might want different entityIds.
 */
@Setter
public class RewriteData {

    private long entityId;
    private long originalEntityId;
    private BlockPalette blockPalette;
    private BlockPaletteRewrite blockPaletteRewrite;
    private List<BlockPropertyData> blockProperties;
    private List<GameRuleData<?>> gameRules;
    private int dimension = 0;
    private volatile TransferCallback transferCallback;
    private Vector3f spawnPosition;
    private Vector2f rotation;
    private boolean immobileFlag;
    private String proxyName;
    private BedrockCodecHelper codecHelper;

    public long getEntityId() { return entityId; }
    public void setEntityId(long entityId) { this.entityId = entityId; }
    public long getOriginalEntityId() { return originalEntityId; }
    public void setOriginalEntityId(long originalEntityId) { this.originalEntityId = originalEntityId; }
    public BlockPalette getBlockPalette() { return blockPalette; }
    public void setBlockPalette(BlockPalette blockPalette) { this.blockPalette = blockPalette; }
    public BlockPaletteRewrite getBlockPaletteRewrite() { return blockPaletteRewrite; }
    public void setBlockPaletteRewrite(BlockPaletteRewrite blockPaletteRewrite) { this.blockPaletteRewrite = blockPaletteRewrite; }
    public List<BlockPropertyData> getBlockProperties() { return blockProperties; }
    public void setBlockProperties(List<BlockPropertyData> blockProperties) { this.blockProperties = blockProperties; }
    public List<GameRuleData<?>> getGameRules() { return gameRules; }
    public void setGameRules(List<GameRuleData<?>> gameRules) { this.gameRules = gameRules; }
    public int getDimension() { return dimension; }
    public void setDimension(int dimension) { this.dimension = dimension; }
    public TransferCallback getTransferCallback() { return transferCallback; }
    public synchronized void setTransferCallback(TransferCallback transferCallback) { this.transferCallback = transferCallback; }

    /**
     * Atomically claims the transfer slot. Downstream connections run on different event loops,
     * so two of them can reach START_GAME concurrently and only one may win.
     *
     * @return false if another transfer is still in progress.
     */
    public synchronized boolean trySetTransferCallback(TransferCallback callback) {
        if (this.transferCallback != null && this.transferCallback.getPhase() != TransferCallback.TransferPhase.RESET) {
            return false;
        }
        this.transferCallback = callback;
        return true;
    }

    /**
     * Clears the transfer slot only if it is still owned by the given callback.
     */
    public synchronized void clearTransferCallback(TransferCallback callback) {
        if (this.transferCallback == callback) {
            this.transferCallback = null;
        }
    }
    public Vector3f getSpawnPosition() { return spawnPosition; }
    public void setSpawnPosition(Vector3f spawnPosition) { this.spawnPosition = spawnPosition; }
    public Vector2f getRotation() { return rotation; }
    public void setRotation(Vector2f rotation) { this.rotation = rotation; }
    public void setImmobileFlag(boolean immobileFlag) { this.immobileFlag = immobileFlag; }
    public String getProxyName() { return proxyName; }
    public void setProxyName(String proxyName) { this.proxyName = proxyName; }
    public BedrockCodecHelper getCodecHelper() { return codecHelper; }
    public void setCodecHelper(BedrockCodecHelper codecHelper) { this.codecHelper = codecHelper; }

    public RewriteData() {
        this.proxyName = ProxyServer.getInstance().getConfiguration().getName();
    }

    /**
     * Atomically claims the transfer slot. Downstream connections run on different event loops,
     * so two of them can reach START_GAME concurrently and only one may win.
     *
     * @return false if another transfer is still in progress.
     */
    public synchronized boolean trySetTransferCallback(TransferCallback callback) {
        if (this.transferCallback != null && this.transferCallback.getPhase() != TransferCallback.TransferPhase.RESET) {
            return false;
        }
        this.transferCallback = callback;
        return true;
    }

    /**
     * Clears the transfer slot only if it is still owned by the given callback.
     */
    public synchronized void clearTransferCallback(TransferCallback callback) {
        if (this.transferCallback == callback) {
            this.transferCallback = null;
        }
    }

    public synchronized void setTransferCallback(TransferCallback callback) {
        this.transferCallback = callback;
    }

    public boolean hasImmobileFlag() {
        return this.immobileFlag;
    }

}
