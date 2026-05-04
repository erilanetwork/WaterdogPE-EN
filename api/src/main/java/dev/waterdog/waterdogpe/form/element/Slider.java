package dev.waterdog.waterdogpe.form.element;

public class Slider extends Element {

    private final float min;
    private final float max;
    private final float step;
    private final float defaultValue;

    public Slider(String text, float min, float max) {
        this(text, min, max, 1.0f, min);
    }

    public Slider(String text, float min, float max, float step) {
        this(text, min, max, step, min);
    }

    public Slider(String text, float min, float max, float step, float defaultValue) {
        super(text, "slider");
        this.min = min;
        this.max = max;
        this.step = step;
        this.defaultValue = defaultValue;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float getStep() {
        return step;
    }

    public float getDefaultValue() {
        return defaultValue;
    }
}
