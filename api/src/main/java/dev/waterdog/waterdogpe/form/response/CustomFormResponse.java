package dev.waterdog.waterdogpe.form.response;

import java.util.HashMap;
import java.util.Map;

public class CustomFormResponse implements FormResponse {

    private final Map<Integer, Object> responses;

    public CustomFormResponse(Map<Integer, Object> responses) {
        this.responses = responses;
    }

    public Object getResponse(int id) {
        return responses.get(id);
    }

    public String getInputResponse(int id) {
        return (String) responses.get(id);
    }

    public int getDropdownResponse(int id) {
        return (int) (double) responses.get(id);
    }

    public int getStepSliderResponse(int id) {
        return (int) (double) responses.get(id);
    }

    public float getSliderResponse(int id) {
        return (float) (double) responses.get(id);
    }

    public boolean getToggleResponse(int id) {
        return (boolean) responses.get(id);
    }

    public Map<Integer, Object> getResponses() {
        return responses;
    }
}
