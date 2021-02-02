package com.pmchecklist.pdf_gen;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;

import com.pmchecklist.R;
import com.pmchecklist.model.Checklist;
import com.pmchecklist.model.ChecklistItem;

import java.util.ArrayList;
import java.util.Arrays;

public class PdfColumnGenerator {
    private final Checklist checklist;

    private ArrayList<ChecklistItem> okList;
    private ArrayList<ChecklistItem> rrList;
    private ArrayList<ChecklistItem> naList;
    private ArrayList<ChecklistItem> safetyList;

    private ArrayList<ArrayList<ChecklistItem>> cols;
    private ChecklistItem overflowHeader = null;

    public PdfColumnGenerator (Checklist checklist) {
        this.checklist = checklist;
    }

    public ArrayList<ChecklistItem> getColumn(int columnNum) {
        if (0 <= columnNum && columnNum < cols.size()) {
            return cols.get(columnNum);
        }
        return null;
    }

    public void generatePdfChecklistColumns(View view) {
        generateChecklistCategories(view);
        generateChecklistColumns();
    }

    private void generateChecklistCategories(View view) {
        Resources resources = view.getResources();
        okList = new ArrayList<>();
        rrList = new ArrayList<>();
        naList = new ArrayList<>();
        safetyList = new ArrayList<>();

        ChecklistItem okHeader = new ChecklistItem();
        okHeader.setHeader(resources.getString(R.string.pdf_colheader_ok));
        okList.add(okHeader);

        ChecklistItem rrHeader = new ChecklistItem();
        rrHeader.setHeader(resources.getString(R.string.pdf_colheader_rr));
        rrList.add(rrHeader);

        ChecklistItem naHeader = new ChecklistItem();
        naHeader.setHeader(resources.getString(R.string.pdf_colheader_na));
        naList.add(naHeader);

        ChecklistItem siHeader = new ChecklistItem();
        siHeader.setHeader(resources.getString(R.string.pdf_colheader_safety));
        safetyList.add(siHeader);

        String sectionHeader = "";
        for (ChecklistItem item : checklist.getChecklist()) {
                if (!TextUtils.isEmpty(item.getHeader()))
                    sectionHeader = item.getHeader();
                else if (item.getSelection() == ChecklistItem.Selection.OK)
                    okList.add(item);
                else if (item.getSelection() == ChecklistItem.Selection.RR) {
                    if (sectionHeader.equals(resources.getString(R.string.checklist_safety_header)))
                        safetyList.add(item);
                    else
                        rrList.add(item);
                } else if (item.getSelection() == ChecklistItem.Selection.NA)
                    naList.add(item);
        }
    }

    private void generateChecklistColumns() {
        ArrayList<ChecklistItem> col0 = fillChecklistColumn();
        ArrayList<ChecklistItem> col1 = fillChecklistColumn();
        ArrayList<ChecklistItem> col2 = fillChecklistColumn();
        cols = new ArrayList<>(Arrays.asList(col0, col1, col2));
    }

    private ArrayList<ChecklistItem> fillChecklistColumn() {
        int maxColumnItems = 22;
        int currColItems = 0;
        ArrayList<ChecklistItem> col = new ArrayList<>();
        ChecklistItem currItem = null;

        if (overflowHeader != null) {
            currColItems += 1;
            col.add(overflowHeader);
            overflowHeader = null;
        }

        while (currColItems < maxColumnItems) {
            if (!okList.isEmpty()) {
                currItem = okList.remove(0);
            } else if (!rrList.isEmpty()) {
                currItem = rrList.remove(0);
            } else if (!naList.isEmpty()) {
                currItem = naList.remove(0);
            } else if (!safetyList.isEmpty()) {
                currItem = safetyList.remove(0);
            }

            if (currItem != null) {
                currColItems += 1;
                if (!TextUtils.isEmpty(currItem.getHeader()) && currColItems >= maxColumnItems) {
                    overflowHeader = currItem;
                } else {
                    col.add(currItem);
                }
                currItem = null;
            } else  {
                break;
            }
        }

        return col;
    }
}
