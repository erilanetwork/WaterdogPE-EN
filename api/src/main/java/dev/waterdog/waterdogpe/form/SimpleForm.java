package dev.waterdog.waterdogpe.form;

import dev.waterdog.waterdogpe.form.element.Button;
import dev.waterdog.waterdogpe.form.response.FormResponse;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import dev.waterdog.waterdogpe.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class SimpleForm implements Form {

    private String title;
    private String content;
    private List<Button> buttons = new ArrayList<>();
    private BiConsumer<ProxiedPlayer, FormResponse> responseHandler;

    public SimpleForm(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public SimpleForm(String title, String content, List<Button> buttons) {
        this.title = title;
        this.content = content;
        this.buttons = buttons;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public void addButton(Button button) {
        buttons.add(button);
    }

    @Override
    public BiConsumer<ProxiedPlayer, FormResponse> getResponseHandler() {
        return responseHandler;
    }

    @Override
    public void setResponseHandler(BiConsumer<ProxiedPlayer, FormResponse> handler) {
        this.responseHandler = handler;
    }

    @Override
    public String getJsonData() {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "form");
        data.put("title", title);
        data.put("content", content);
        data.put("buttons", buttons);
        return FileUtils.GSON.toJson(data);
    }
}
