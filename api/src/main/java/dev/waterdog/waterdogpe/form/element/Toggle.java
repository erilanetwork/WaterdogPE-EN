package dev.waterdog.waterdogpe.form.element;

public class Toggle extends Element {

    private final boolean defaultValue;

    public Toggle(String text) {
        this(text, false);
    }

    public Toggle(String text, boolean defaultValue) {
        super(text, "toggle");
        this.defaultValue = defaultValue;
    }

    public boolean isDefaultValue() {
        return defaultValue;
    }
}
