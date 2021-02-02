package com.pmchecklist.model;

import android.text.TextUtils;

import java.util.ArrayList;

public class Checklist {
    private final ArrayList<ChecklistItem> checklist;
    private final String type;

    public Checklist (String type) {
        checklist = new ArrayList<>();
        this.type = type;
    }

    public ArrayList<ChecklistItem> getChecklist() {
        return checklist;
    }

    public String getType() { return type; }

    /*
    sections should be {{header}, {list, of, items}}
     */
    public void addSectionToChecklist(String[][] section) {
        ChecklistItem item = new ChecklistItem();
        item.setHeader(section[0][0]);
        checklist.add(item);

        for (String description : section[1]) {
            item = new ChecklistItem();
            item.setDescription(description);
            checklist.add(item);
        }
    }

    public boolean allItemsSelected() {
        if (!checklist.isEmpty()) {
            for (ChecklistItem item : checklist) {
                if (item.getSelection() == ChecklistItem.Selection.None) {
                    if (TextUtils.isEmpty(item.getHeader())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
