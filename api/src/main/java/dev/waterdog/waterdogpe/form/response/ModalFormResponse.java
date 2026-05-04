package dev.waterdog.waterdogpe.form.response;

public class ModalFormResponse implements FormResponse {

    private final boolean clickedTrue;

    public ModalFormResponse(boolean clickedTrue) {
        this.clickedTrue = clickedTrue;
    }

    public boolean isClickedTrue() {
        return clickedTrue;
    }

    public boolean isClickedFalse() {
        return !clickedTrue;
    }
}
