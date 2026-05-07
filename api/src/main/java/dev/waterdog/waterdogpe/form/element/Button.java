package dev.waterdog.waterdogpe.form.element;

import lombok.Getter;
import lombok.Setter;

public class Button {

    @Setter
    @Getter
    private String text;
    private ButtonImage image;

    public Button(String text) {
        this.text = text;
    }

    public Button(String text, ButtonType type, String image) {
        this.text = text;
        if (type != null && image != null) {
            this.image = new ButtonImage(type.name().toLowerCase(), image);
        }
    }

    public ButtonType getType() {
        return image == null ? null : ButtonType.valueOf(image.type.toUpperCase());
    }

    public void setType(ButtonType type) {
        if (type == null) {
            this.image = null;
        } else {
            String data = image == null ? "" : image.data;
            this.image = new ButtonImage(type.name().toLowerCase(), data);
        }
    }

    public String getImage() {
        return image == null ? null : image.data;
    }

    public void setImage(String image) {
        if (image == null) {
            this.image = null;
        } else {
            String type = this.image == null ? "path" : this.image.type;
            this.image = new ButtonImage(type, image);
        }
    }

    public ButtonImage getButtonImage() {
        return image;
    }

    public void setButtonImage(ButtonImage image) {
        this.image = image;
    }

    @Getter
    public static class ButtonImage {
        private final String type;
        private final String data;

        public ButtonImage(String type, String data) {
            this.type = type;
            this.data = data;
        }

    }
}
