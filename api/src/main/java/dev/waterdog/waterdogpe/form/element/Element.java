package dev.waterdog.waterdogpe.form.element;

public abstract class Element {

    private final String text;
    private final String type;

    public Element(String text, String type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }
}
