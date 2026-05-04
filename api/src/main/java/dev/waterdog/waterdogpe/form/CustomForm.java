package dev.waterdog.waterdogpe.form;

import dev.waterdog.waterdogpe.form.element.Element;
import dev.waterdog.waterdogpe.form.response.FormResponse;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import dev.waterdog.waterdogpe.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class CustomForm implements Form {

    private String title;
    private List<Element> elements = new ArrayList<>();
    private BiConsumer<ProxiedPlayer, FormResponse> responseHandler;

    public CustomForm(String title) {
        this.title = title;
    }

    public CustomForm(String title, List<Element> elements) {
        this.title = title;
        this.elements = elements;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void addElement(Element element) {
        elements.add(element);
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
        data.put("type", "custom_form");
        data.put("title", title);
        data.put("content", elements);
        return FileUtils.GSON.toJson(data);
    }
}
