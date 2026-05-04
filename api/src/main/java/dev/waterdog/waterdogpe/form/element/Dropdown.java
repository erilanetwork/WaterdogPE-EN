package dev.waterdog.waterdogpe.form.element;

import java.util.List;

public class Dropdown extends Element {

    private final List<String> options;
    private final int defaultIndex;

    public Dropdown(String text, List<String> options) {
        this(text, options, 0);
    }

    public Dropdown(String text, List<String> options, int defaultIndex) {
        super(text, "dropdown");
        this.options = options;
        this.defaultIndex = defaultIndex;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getDefaultIndex() {
        return defaultIndex;
    }
}
