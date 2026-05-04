package dev.waterdog.waterdogpe.form.response;

import dev.waterdog.waterdogpe.form.element.Button;

public class SimpleFormResponse implements FormResponse {

    private final Button clickedButton;
    private final int clickedButtonId;

    public SimpleFormResponse(Button clickedButton, int clickedButtonId) {
        this.clickedButton = clickedButton;
        this.clickedButtonId = clickedButtonId;
    }

    public Button getClickedButton() {
        return clickedButton;
    }

    public int getClickedButtonId() {
        return clickedButtonId;
    }
}
