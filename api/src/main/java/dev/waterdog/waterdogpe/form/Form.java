package dev.waterdog.waterdogpe.form;

import dev.waterdog.waterdogpe.form.response.FormResponse;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

import java.util.function.BiConsumer;

public interface Form {

    String getTitle();

    void setTitle(String title);

    String getJsonData();

    BiConsumer<ProxiedPlayer, FormResponse> getResponseHandler();

    void setResponseHandler(BiConsumer<ProxiedPlayer, FormResponse> handler);

    default void handleResponse(ProxiedPlayer player, FormResponse response) {
        if (getResponseHandler() != null) {
            getResponseHandler().accept(player, response);
        }
    }
}
