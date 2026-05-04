package dev.waterdog.waterdogpe.form.element;

import java.util.HashMap;
import java.util.Map;

public class Button {

    private String text;
    private ButtonType type;
    private String image;

    public Button(String text) {
        this.text = text;
    }

    public Button(String text, ButtonType type, String image) {
        this.text = text;
        this.type = type;
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ButtonType getType() {
        return type;
    }

    public void setType(ButtonType type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Map<String, Object> getImageData() {
        if (type == null || image == null) {
            return null;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("type", type.name().toLowerCase());
        data.put("data", image);
        return data;
    }
}
