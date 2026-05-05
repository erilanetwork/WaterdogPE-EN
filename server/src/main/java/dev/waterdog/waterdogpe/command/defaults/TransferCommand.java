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

package dev.waterdog.waterdogpe.command.defaults;

import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.form.SimpleForm;
import dev.waterdog.waterdogpe.form.element.Button;
import dev.waterdog.waterdogpe.form.element.ButtonType;
import dev.waterdog.waterdogpe.form.response.SimpleFormResponse;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

public class TransferCommand extends Command {

    public TransferCommand() {
        super("transfer", CommandSettings.builder()
                .setDescription("waterdog.command.transfer.description")
                .setPermission("waterdog.command.transfer.permission")
                .setUsageMessage("waterdog.command.transfer.usage").build());
    }

    @Override
    public boolean onExecute(CommandSender sender, String alias, String[] args) {
        if (!sender.isPlayer()) {
            sender.sendMessage("§cBu komut sadece oyuncular tarafından kullanılabilir.");
            return true;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        List<ServerInfo> serverList = new ArrayList<>(sender.getProxy().getServers());

        if (serverList.isEmpty()) {
            sender.sendMessage("§cHiç sunucu bulunamadı.");
            return true;
        }

        SimpleForm form = new SimpleForm(
                "Sunucu Değiştir",
                "Birbirinden güzel §bErila§7Network §rsunucularında\noynamaya ne dersin?"
        );

        for (ServerInfo server : serverList) {
            int playerCount = server.getPlayers().size();
            String buttonText = server.getServerName() + "\n" + playerCount + " kişi oynuyor";
            form.addButton(new Button(buttonText, ButtonType.PATH, "textures/items/book_normal"));
        }

        form.setResponseHandler((respondingPlayer, response) -> {
            if (response instanceof SimpleFormResponse simpleResponse) {
                int buttonId = simpleResponse.getClickedButtonId();
                if (buttonId >= 0 && buttonId < serverList.size()) {
                    ServerInfo targetServer = serverList.get(buttonId);

                    if (targetServer.equals(respondingPlayer.getServerInfo())) {
                        respondingPlayer.sendMessage("§cZaten bu sunucudasın!");
                        return;
                    }

                    respondingPlayer.connect(targetServer);
                }
            }
        });

        player.sendForm(form);
        return true;
    }
}
