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

import dev.waterdog.waterdogpe.logger.MainLogger;
import org.cloudburstmc.protocol.bedrock.data.skin.PersonaPieceType;

import java.lang.reflect.Field;
import java.util.Map;

public final class PersonaPieceTypePatch {

    private PersonaPieceTypePatch() {
    }

    @SuppressWarnings("unchecked")
    public static void apply() {
        try {
            Field field = PersonaPieceType.class.getDeclaredField("serializeNames");
            field.setAccessible(true);
            Map<String, PersonaPieceType> names = (Map<String, PersonaPieceType>) field.get(null);

            for (PersonaPieceType type : PersonaPieceType.values()) {
                String serializeName = type.getSerializeName();
                if (serializeName.indexOf('_') < 0) {
                    continue;
                }

                String compact = serializeName.replace("_", "");
                names.putIfAbsent(compact, type);
                names.putIfAbsent("persona_" + compact, type);
            }
        } catch (Exception e) {
            MainLogger.getLogger().warning("Unable to patch PersonaPieceType names", e);
        }
    }
}
