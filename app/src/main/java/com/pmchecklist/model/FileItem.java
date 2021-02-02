package com.pmchecklist.model;

import java.io.File;

public class FileItem {
    File file;
    private boolean isChecked;

    public FileItem(File file) {
        this.file = file;
        this.isChecked = false;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public File getFile() {
        return this.file;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
