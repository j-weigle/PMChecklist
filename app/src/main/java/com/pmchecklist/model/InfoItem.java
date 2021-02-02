package com.pmchecklist.model;

import android.widget.EditText;

public class InfoItem {
    private String fieldName;
    private String userInput;
    private EditText editText;
    private boolean required;

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setEditText(EditText editText) { this.editText = editText; }

    public EditText getEditText() { return editText; }

    public void setRequired(boolean required) { this.required = required; }

    public boolean isRequired() { return required; }
}
