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

import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.form.CustomForm;
import dev.waterdog.waterdogpe.form.ModalForm;
import dev.waterdog.waterdogpe.form.SimpleForm;
import dev.waterdog.waterdogpe.form.element.Button;
import dev.waterdog.waterdogpe.form.element.ButtonType;
import dev.waterdog.waterdogpe.form.element.Input;
import dev.waterdog.waterdogpe.form.response.CustomFormResponse;
import dev.waterdog.waterdogpe.form.response.ModalFormResponse;
import dev.waterdog.waterdogpe.form.response.SimpleFormResponse;
import dev.waterdog.waterdogpe.network.serverinfo.BedrockServerInfo;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ServerManagementCommand extends Command {

    public ServerManagementCommand() {
        super("server", CommandSettings.builder()
                .setDescription("waterdog.command.servermanagement.description")
                .setPermission("waterdog.command.servermanagement.permission")
                .setUsageMessage("/server")
                .setAliases("syonetim", "servermanage")
                .build());
    }

    @Override
    public boolean onExecute(CommandSender sender, String alias, String[] args) {
        if (!sender.isPlayer()) {
            sender.sendMessage("§cBu komut sadece oyuncular tarafından kullanılabilir.");
            return true;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        showMainMenu(player);
        return true;
    }

    private void showMainMenu(ProxiedPlayer player) {
        SimpleForm form = new SimpleForm("Sunucu Yönetim Paneli", "");

        form.addButton(new Button("Sunucu Listesi", ButtonType.PATH, "textures/items/book_normal"));
        form.addButton(new Button("Sunucu Ekle", ButtonType.PATH, "textures/items/emerald"));
        form.addButton(new Button("Sunucu Kaldır", ButtonType.PATH, "textures/blocks/barrier"));

        form.setResponseHandler((respondingPlayer, response) -> {
            if (response instanceof SimpleFormResponse simpleResponse) {
                switch (simpleResponse.getClickedButtonId()) {
                    case 0 -> showServerList(respondingPlayer);
                    case 1 -> showAddServerForm(respondingPlayer);
                    case 2 -> showRemoveServerForm(respondingPlayer);
                }
            }
        });

        player.sendForm(form);
    }

    private void showServerList(ProxiedPlayer player) {
        List<ServerInfo> servers = new ArrayList<>(player.getProxy().getServers());

        if (servers.isEmpty()) {
            player.sendMessage("§cHiç kayıtlı sunucu bulunamadı.");
            showMainMenu(player);
            return;
        }

        SimpleForm form = new SimpleForm(
                "Sunucu Listesi",
                "Toplam §7" + servers.size() + " adet §rsunucu kayıtlı."
        );

        for (ServerInfo server : servers) {
            int playerCount = server.getPlayers().size();
            String buttonText =  server.getServerName() + "\n§e" + playerCount + " oyuncu";
            form.addButton(new Button(buttonText, ButtonType.PATH, "textures/items/book_normal"));
        }

        form.addButton(new Button("§cGeri", ButtonType.PATH, "textures/ui/back_button_pressed"));

        form.setResponseHandler((respondingPlayer, response) -> {
            if (response instanceof SimpleFormResponse simpleResponse) {
                int buttonId = simpleResponse.getClickedButtonId();
                if (buttonId == servers.size()) {
                    showMainMenu(respondingPlayer);
                } else if (buttonId >= 0 && buttonId < servers.size()) {
                    ServerInfo server = servers.get(buttonId);
                    showServerDetail(respondingPlayer, server);
                }
            }
        });

        player.sendForm(form);
    }

    private void showServerDetail(ProxiedPlayer player, ServerInfo server) {
        StringBuilder content = new StringBuilder();
        content.append("§7Adres: §f").append(server.getAddress().getHostString()).append("\n");
        content.append("§7Port: §f").append(server.getAddress().getPort()).append("\n");
        content.append("§7Oyuncu Sayısı: §f").append(server.getPlayers().size()).append("\n\n");

        if (!server.getPlayers().isEmpty()) {
            content.append("§7Oyuncular:\n");
            for (ProxiedPlayer p : server.getPlayers()) {
                content.append(p.getName()).append("§7 - §f").append(p.getPing()).append(" ms").append("\n");
            }
        }

        SimpleForm form = new SimpleForm("Sunucu Detayı", content.toString());
        form.addButton(new Button("§cGeri", ButtonType.PATH, "textures/ui/back_button_pressed"));

        form.setResponseHandler((respondingPlayer, response) -> {
            if (response instanceof SimpleFormResponse) {
                showServerList(respondingPlayer);
            }
        });

        player.sendForm(form);
    }

    private void showAddServerForm(ProxiedPlayer player) {
        CustomForm form = new CustomForm("Sunucu Ekle");

        form.addElement(new Input("Sunucu Adı:", "ÖRN: lobby1"));
        form.addElement(new Input("IP Adresi:", "ÖRN: 127.0.0.1"));
        form.addElement(new Input("Port:", "ÖRN: 19132"));

        form.setResponseHandler((respondingPlayer, response) -> {
            if (response instanceof CustomFormResponse customResponse) {
                try {
                    String serverName = customResponse.getInputResponse(0);
                    String address = customResponse.getInputResponse(1);
                    String portStr = customResponse.getInputResponse(2);

                    if (serverName == null || serverName.trim().isEmpty()) {
                        respondingPlayer.sendMessage("§cSunucu adı boş olamaz!");
                        return;
                    }

                    if (address == null || address.trim().isEmpty()) {
                        respondingPlayer.sendMessage("§cIP adresi boş olamaz!");
                        return;
                    }

                    int port;
                    try {
                        port = Integer.parseInt(portStr.trim());
                    } catch (NumberFormatException e) {
                        respondingPlayer.sendMessage("§cGeçersiz port numarası!");
                        return;
                    }

                    if (respondingPlayer.getProxy().getServerInfo(serverName.trim()) != null) {
                        respondingPlayer.sendMessage("§c'" + serverName.trim() + "' adında bir sunucu zaten var!");
                        return;
                    }

                    InetSocketAddress socketAddress = new InetSocketAddress(address.trim(), port);
                    ServerInfo serverInfo = new BedrockServerInfo(serverName.trim(), socketAddress, socketAddress);
                    boolean registered = respondingPlayer.getProxy().registerServerInfo(serverInfo);

                    if (registered) {
                        respondingPlayer.sendMessage("§aBaşarılı bir şekilde sunucuyu eklendiniz.");
                    } else {
                        respondingPlayer.sendMessage("§cSunucu eklenemedi!");
                    }
                } catch (Exception e) {
                    respondingPlayer.sendMessage("§cBir hata oluştu: " + e.getMessage());
                }
            }
        });

        player.sendForm(form);
    }

    private void showRemoveServerForm(ProxiedPlayer player) {
        List<ServerInfo> servers = new ArrayList<>(player.getProxy().getServers());

        if (servers.isEmpty()) {
            player.sendMessage("§cKaldırılacak sunucu bulunamadı.");
            showMainMenu(player);
            return;
        }

        SimpleForm form = new SimpleForm(
                "Sunucu Kaldır",
                "Kaldırmak istediğiniz sunucuyu seçin"
        );

        for (ServerInfo server : servers) {
            form.addButton(new Button(server.getServerName(), ButtonType.PATH, "textures/blocks/barrier"));
        }

        form.addButton(new Button("§cGeri", ButtonType.PATH, "textures/ui/back_button_pressed"));

        form.setResponseHandler((respondingPlayer, response) -> {
            if (response instanceof SimpleFormResponse simpleResponse) {
                int buttonId = simpleResponse.getClickedButtonId();
                if (buttonId == servers.size()) {
                    showMainMenu(respondingPlayer);
                } else if (buttonId >= 0 && buttonId < servers.size()) {
                    ServerInfo server = servers.get(buttonId);
                    showRemoveConfirm(respondingPlayer, server);
                }
            }
        });

        player.sendForm(form);
    }

    private void showRemoveConfirm(ProxiedPlayer player, ServerInfo server) {
        ModalForm form = new ModalForm(
                "Sunucu Kaldır",
                "§f'§7" + server.getServerName() + "§f' sunucusunu kaldırmak istediğinize emin misiniz?\n\n" +
                "§fBağlı oyuncu sayısı: §7" + server.getPlayers().size() + " oyuncu\n" +
                "§fAdres: §7" + server.getAddress().getHostString() + ":" + server.getAddress().getPort(),
                "§2Onayla",
                "§4Geri"
        );

        form.setResponseHandler((respondingPlayer, response) -> {
            if (response instanceof ModalFormResponse simpleResponse) {
                if (simpleResponse.isClickedTrue()) {
                    ServerInfo removed = respondingPlayer.getProxy().removeServerInfo(server.getServerName());
                    if (removed != null) {
                        respondingPlayer.sendMessage("§2'" + server.getServerName() + "' §aadlı sunucu başarıyla kaldırıldı.");
                    } else {
                        respondingPlayer.sendMessage("§cSunucu kaldırılamadı!");
                    }
                } else {
                    showMainMenu(respondingPlayer);
                }
            }
        });

        player.sendForm(form);
    }
}
