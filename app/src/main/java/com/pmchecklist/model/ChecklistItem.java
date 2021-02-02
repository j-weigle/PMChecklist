package com.pmchecklist.model;

public class ChecklistItem {
    public enum Selection {
        None, OK, RR, NA
    }

    private String header;
    private String description;
    private Selection selection;

    public ChecklistItem() {
        this.header = "";
        this.description = "";
        this.selection = Selection.None;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public void setDescription(String description) { this.description = description; }

    public String getDescription() {
        return description;
    }

    public void setSelection(Selection selection) { this.selection = selection; }

    public Selection getSelection() {
        return selection;
    }
}
