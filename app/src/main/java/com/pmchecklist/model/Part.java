package com.pmchecklist.model;

import android.widget.EditText;

public class Part {
    private String qty;
    private String partNumber;
    private String description;
    private EditText[] editTextArr;

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getQty() {
        return qty;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setEditTextArr(EditText qty, EditText partNumber, EditText description) {
        editTextArr = new EditText[]{qty, partNumber, description};
    }

    public EditText[] getEditTextArr() {
        return editTextArr;
    }
}
