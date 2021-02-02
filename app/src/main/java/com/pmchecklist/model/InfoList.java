package com.pmchecklist.model;

import android.widget.EditText;

import java.util.ArrayList;

public class InfoList {
    private final ArrayList<InfoItem> infoList;

    public InfoList() {
        infoList = new ArrayList<>();
    }

    public ArrayList<InfoItem> getInfoList() {
        return infoList;
    }

    public void addBlankInfoItem (String fieldName, EditText editText, boolean isRequired) {
        InfoItem item = new InfoItem();
        item.setFieldName(fieldName);
        item.setEditText(editText);
        item.setRequired(isRequired);

        infoList.add(item);
    }
}