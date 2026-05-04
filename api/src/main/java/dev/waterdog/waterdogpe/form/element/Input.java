package dev.waterdog.waterdogpe.form.element;

public class Input extends Element {

    private final String placeholder;
    private final String defaultText;

    public Input(String text, String placeholder) {
        this(text, placeholder, "");
    }

    public Input(String text, String placeholder, String defaultText) {
        super(text, "input");
        this.placeholder = placeholder;
        this.defaultText = defaultText;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public String getDefaultText() {
        return defaultText;
    }
}
