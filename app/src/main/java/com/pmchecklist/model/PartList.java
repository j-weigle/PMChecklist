package com.pmchecklist.model;

import android.widget.EditText;

import java.util.ArrayList;

public class PartList {
    private final ArrayList<Part> partList;

    public PartList() {
        partList = new ArrayList<>();
    }

    public ArrayList<Part> getPartList() {
        return partList;
    }

    public void addPart (EditText qty, EditText number, EditText description) {
        Part part = new Part();
        part.setEditTextArr(qty, number, description);
        partList.add(part);
    }
}
