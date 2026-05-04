package dev.waterdog.waterdogpe.form;

import dev.waterdog.waterdogpe.form.response.FormResponse;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import dev.waterdog.waterdogpe.utils.FileUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ModalForm implements Form {

    private String title;
    private String content;
    private String button1;
    private String button2;
    private BiConsumer<ProxiedPlayer, FormResponse> responseHandler;

    public ModalForm(String title, String content, String button1, String button2) {
        this.title = title;
        this.content = content;
        this.button1 = button1;
        this.button2 = button2;
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

    public String getButton1() {
        return button1;
    }

    public void setButton1(String button1) {
        this.button1 = button1;
    }

    public String getButton2() {
        return button2;
    }

    public void setButton2(String button2) {
        this.button2 = button2;
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
        data.put("type", "modal");
        data.put("title", title);
        data.put("content", content);
        data.put("button1", button1);
        data.put("button2", button2);
        return FileUtils.GSON.toJson(data);
    }
}
