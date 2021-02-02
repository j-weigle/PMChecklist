package com.pmchecklist.pdf_gen;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.pmchecklist.R;
import com.pmchecklist.model.ChecklistItem;
import com.pmchecklist.model.InfoItem;
import com.pmchecklist.model.InfoList;
import com.pmchecklist.model.Part;
import com.pmchecklist.model.PartList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class PdfPrinter {
    private final InfoList infoList;
    private final PartList partList;

    private Canvas canvas;

    public PdfPrinter (InfoList infoList, PartList partList) {
        this.infoList = infoList;
        this.partList = partList;
    }

    private PrintAttributes getPrintAttributes() {
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setResolution(new PrintAttributes.Resolution("0", "800x450dpi", 800, 450));
        builder.setMediaSize(new PrintAttributes.MediaSize("0", "14in x 7.875in", 14000, 7875));
        builder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);
        return builder.build();
    }

    public File generatePdfFile(PdfColumnGenerator pdfColumnGenerator, View view) {
        /*
        Create the document to get a canvas to draw to
         */
        PrintedPdfDocument document = new PrintedPdfDocument(view.getContext(), getPrintAttributes());
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(document.getPageWidth(), document.getPageHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        canvas = page.getCanvas();

        /*
        Draw everything to the canvas
         */
        drawForkliftImage(view);
        Paint paint = new Paint();
        drawLayoutBorders(paint);
        drawHeaderInformation(view, paint);
        drawWONumber(paint);
        int x = 5;
        int y = 100;
        drawCheckColumn(pdfColumnGenerator.getColumn(0), x, y, view, paint);
        x = 341;
        drawCheckColumn(pdfColumnGenerator.getColumn(1), x, y, view, paint);
        x = 677;
        drawCheckColumn(pdfColumnGenerator.getColumn(2), x, y, view, paint);
        drawInfoCol(paint);
        drawPartsColumn(paint);

        document.finishPage(page);

        /*
        Create a file utilizing information from the user
         */
        ArrayList<InfoItem> infoListInstance = infoList.getInfoList();
        String dirPath = getDirectoryString(infoListInstance);
        File pdfDirPath = createDirectory(dirPath, view);
        String wONumber = infoListInstance.get(0).getUserInput().toUpperCase();
        File file = createFile(pdfDirPath, wONumber);

        /*
        Write to the created document
         */
        try {
            document.writeTo(new FileOutputStream(file));
        } catch (IOException e) {e.printStackTrace();}

        document.close();

        return file;
    }

    private void drawLayoutBorders(Paint paint) {
        paint.setColor(Color.parseColor("#999999"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        // whole document border
        canvas.drawRect(canvas.getClipBounds(), paint);
        // company info
        canvas.drawRect(306, 10, 702, 75, paint);
        // left checklist section
        canvas.drawRect(0, 85, 336, 403, paint);
        // center checklist section
        canvas.drawRect(336, 85, 672, 403, paint);
        // right checklist section
        canvas.drawRect(672, 85, 1008, 403, paint);
        // info|parts vertical separator
        canvas.drawLine(504, 403, 504, 567, paint);

        paint.setStyle(Paint.Style.FILL);
    }

    private void drawForkliftImage(View view) {
        Drawable forklift = Objects.requireNonNull(ResourcesCompat.getDrawable(
                view.getResources(), R.drawable.forklift_clipart, null));
        Rect forkliftBounds = new Rect(10, 0, 90, 90);
        forklift.setBounds(forkliftBounds);
        forklift.draw(canvas);
    }

    private void drawHeaderInformation(View view, Paint paint) {
        int x, y;
        Resources resources = view.getResources();
        paint.setColor(Color.BLACK);
        paint.setTextSize(18);
        x = 85;
        y = 50;
        paint.setUnderlineText(true);
        paint.setTypeface(Typeface.DEFAULT);
        canvas.drawText(resources.getString(R.string.pdf_company_name), x, y, paint);
        paint.setUnderlineText(false);

        paint.setTypeface(Typeface.MONOSPACE);
        paint.setTextSize(13);
        x = 311;
        y = 32;
        canvas.drawText(resources.getString(R.string.pdf_addr_company), x, y, paint);
        y += paint.getTextSize();
        canvas.drawText(resources.getString(R.string.pdf_addr_street), x, y, paint);
        y += paint.getTextSize();
        canvas.drawText(resources.getString(R.string.pdf_addr_city_state_zip), x, y, paint);
        y -= paint.getTextSize();
        x = 600;
        canvas.drawText(resources.getString(R.string.pdf_phone_num), x, y, paint);
        y += paint.getTextSize();
        x -= paint.measureText("FAX ");
        canvas.drawText(resources.getString(R.string.pdf_fax_num), x, y, paint);
    }

    private void drawCheckColumn(ArrayList<ChecklistItem> column, int x, int y, View view, Paint paint) {
        Resources resources = view.getResources();
        paint.setTextSize(13);
        for (ChecklistItem item : column) {
            if (!TextUtils.isEmpty(item.getHeader())) {
                paint.setFakeBoldText(true);
                if (item.getHeader().equals(resources.getString(R.string.pdf_colheader_rr))) {
                    paint.setColor(Color.rgb(255,100, 0)); // orange
                } else if (item.getHeader().equals(resources.getString(R.string.pdf_colheader_safety))) {
                    paint.setColor(Color.RED);
                } else {
                    paint.setColor(Color.BLACK);
                }
                canvas.drawText(item.getHeader(), x, y, paint);
                paint.setFakeBoldText(false);
            } else {
                canvas.drawText("- " + item.getDescription(), x, y, paint);
            }
            y += paint.getTextSize();
        }
        paint.setColor(Color.BLACK);
    }

    private void drawWONumber(Paint paint) {
        int x, y;
        paint.setTextSize(18);
        paint.setFakeBoldText(true);
        x = 720;
        y = 40;
        canvas.drawText("PM CHECKLIST", x, y, paint);
        paint.setFakeBoldText(false);
        y += paint.getTextSize();
        canvas.drawText(String.format("WO# %s", infoList.getInfoList().get(0).getUserInput()), x, y, paint);
    }

    private void drawInfoCol(Paint paint) {
        int x, y;
        int maxWidth = 63;
        paint.setTextSize(13);

        ArrayList<InfoItem> infList = infoList.getInfoList();
        String[][] texts = {
                {infList.get(1).getFieldName()+":", infList.get(1).getUserInput()},
                {infList.get(2).getFieldName()+":", infList.get(2).getUserInput()},
                {infList.get(3).getFieldName()+":", infList.get(3).getUserInput()},
                {infList.get(4).getFieldName()+":", infList.get(4).getUserInput()},
                {infList.get(5).getFieldName()+":", infList.get(5).getUserInput()},
                {infList.get(6).getFieldName()+":", infList.get(6).getUserInput()},
                {infList.get(7).getFieldName()+":", infList.get(7).getUserInput()},
                {infList.get(8).getFieldName()+":", infList.get(8).getUserInput()}
        };
        y = 420;
        x = 5;
        for (String[] text : texts) {
            canvas.drawText(text[0], x, y, paint);
            canvas.drawText(text[1], x + 110, y, paint);
            y += paint.getTextSize();
        }

        String[] splitAddtComments = infList.get(9).getUserInput().split(" ");
        ArrayList<String> addtComments = new ArrayList<>();
        StringBuilder lineBuilder = new StringBuilder(infList.get(9).getFieldName()+": ");
        for (String word : splitAddtComments) {
            if (lineBuilder.length() + word.length() + 1 < maxWidth) {
                lineBuilder.append(" ").append(word);
            } else {
                addtComments.add(lineBuilder.toString());
                lineBuilder.setLength(0);
                if (word.length() < maxWidth) {
                    lineBuilder.append(word);
                } else {
                    // this is highly unlikely, but maybe handle it at some point by splitting
                    // the 'word' into actual words using some sort of auto-correct system
                    System.err.println("This word is way too long");
                }
            }
        }
        // last loop will not add final word to addtComments, add it here
        addtComments.add(lineBuilder.toString());

        int maxAdditionalCommentLines = 3;
        int currLines = 0;
        for (String line : addtComments) {
            if (currLines < maxAdditionalCommentLines) {
                canvas.drawText(line, x, y, paint);
                y += paint.getTextSize();
                currLines += 1;
            } else { break; }
        }
    }

    private void drawPartsColumn(Paint paint) {
        int x, y;
        x = 509;
        y = 420;
        canvas.drawText("PARTS: {QTY, #, DESCRIPTION}", x, y, paint);
        y += paint.getTextSize();
        for (Part part : partList.getPartList()) {
            if (!TextUtils.isEmpty(part.getQty())
                &&  !TextUtils.isEmpty(part.getPartNumber())
                &&  !TextUtils.isEmpty(part.getDescription())) {
                String line = String.format("{%s, %s, %s}",
                        part.getQty(), part.getPartNumber(), part.getDescription());
                canvas.drawText(line, x, y, paint);
                y += paint.getTextSize();
            }
        }
    }

    private String getDirectoryString(ArrayList<InfoItem> infoListInstance) {
        // directory structure:
        //      Customer
        //          |> Make
        //              |> Date
        //                  |> WONumber.pdf
        String customer = infoListInstance.get(1).getUserInput()
                .toUpperCase().replaceAll("[^A-Z]", "");
        String make = infoListInstance.get(3).getUserInput()
                .toUpperCase().replaceAll("[^A-Z]", "");
        String date = infoListInstance.get(7).getUserInput()
                .replaceAll("[^0-9]","");

        return customer + "/" + make + "/" + date;
    }

    private File createDirectory(String dirPath, View view) {
        File pdfDirPath = new File(view.getContext().getExternalFilesDir(null).getAbsolutePath(), dirPath);
        if (!pdfDirPath.exists()) {
            if (!pdfDirPath.mkdirs()) {
                Log.println(Log.ERROR, "PdfPrinter", "PDF DIR CREATION FAILED");
                return null;
            }
        }
        return pdfDirPath;
    }

    private File createFile(File pdfDirPath, String wONumber) {
        File file = new File(pdfDirPath.getAbsolutePath(), wONumber + ".pdf");
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    Log.println(Log.ERROR, "PdfPrinter", "FILE CREATION FAILED");
                    return null;
                }
            } catch (IOException e) {e.printStackTrace();}
        }
        return file;
    }
}
