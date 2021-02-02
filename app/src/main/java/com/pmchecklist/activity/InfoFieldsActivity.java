
package com.pmchecklist.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;
import com.pmchecklist.R;
import com.pmchecklist.model.Checklist;
import com.pmchecklist.model.InfoItem;
import com.pmchecklist.model.InfoList;
import com.pmchecklist.model.Part;
import com.pmchecklist.model.PartList;
import com.pmchecklist.pdf_gen.PdfColumnGenerator;
import com.pmchecklist.pdf_gen.PdfPrinter;

import java.io.File;

public class InfoFieldsActivity extends AppCompatActivity {
    private Checklist checklist;
    private PartList partList;
    private InfoList infoList;

    private int currPartsLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_fields);

        currPartsLines = 0;
        checklist = ChecklistActivity.getChecklist();
        infoList = new InfoList();
        partList = new PartList();

        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#663333"));
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(checklist.getType());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        buildEmptyInfoList();
    }

    /*
    Adds a blank info item to the info list for each field in the activity view
     */
    private void buildEmptyInfoList() {
        EditText editText;

        editText = findViewById(R.id.edit_text_wo_number);
        infoList.addBlankInfoItem("WO#", editText, true);//0

        editText = findViewById(R.id.edit_text_customer);
        infoList.addBlankInfoItem("CUSTOMER", editText, true);//1

        editText = findViewById(R.id.edit_text_phone);
        infoList.addBlankInfoItem("PHONE #", editText, false);//2
        EditText phoneEditText = editText;
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (phoneEditText.getText().toString().trim().length() < 10) {
                phoneEditText.setError("10 characters minimum");
            } else {
                phoneEditText.setError(null);
            }
        });

        editText = findViewById(R.id.edit_text_make);
        infoList.addBlankInfoItem("MAKE", editText, true);//3

        editText = findViewById(R.id.edit_text_model);
        infoList.addBlankInfoItem("MODEL", editText, true);//4

        editText = findViewById(R.id.edit_text_serial);
        infoList.addBlankInfoItem("SERIAL #", editText, true);//5

        editText = findViewById(R.id.edit_text_meter_hours);
        infoList.addBlankInfoItem("METER HOURS", editText, true);//6

        editText = findViewById(R.id.edit_text_date);
        infoList.addBlankInfoItem("DATE", editText, true);//7
        EditText dateEditText = editText;
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (dateEditText.getText().toString().trim().length() < 8) {
                dateEditText.setError("8 characters minimum");
            } else {
                dateEditText.setError(null);
            }
        });

        editText = findViewById(R.id.edit_text_st_initials);
        infoList.addBlankInfoItem("SERVICE TECH", editText, true);//8

        editText = findViewById(R.id.edit_text_additional_comments);
        infoList.addBlankInfoItem("ADDITIONAL COMMENTS", editText, false);//9
    }

    /*
    Adds another triplet of edit texts to the view for adding another part to the pdf
     */
    public void addPartFieldsLine(View view) {
        int maxPartsLines = 10;
        if (currPartsLines < maxPartsLines) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View pfView = inflater.inflate(R.layout.part_fields, (ViewGroup) view.getParent(), false);

            LinearLayout parentLayout = findViewById(R.id.info_fields_linear);
            int childIdx = parentLayout.indexOfChild(findViewById(R.id.button_add_part));
            parentLayout.addView(pfView, childIdx);

            partList.addPart(
                    (EditText) ((ViewGroup) pfView).getChildAt(0),
                    (EditText) ((ViewGroup) pfView).getChildAt(1),
                    (EditText) ((ViewGroup) pfView).getChildAt(2)
            );

            currPartsLines += 1;
        } else {
            Toast.makeText(
                    this,
                    "Max number of unique parts reached",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    /*
    Uses all the user input to generate a checklist pdf and opens it
     */
    public void createPdf(View view) {
        runOnUiThread(() -> {
            if (!fillInfoList())
                return;
            if (!fillPartList())
                return;

            fixFieldFormatting();

            /*
            Organize checklist into columns according to selections made
             */
            PdfColumnGenerator pdfColumnGenerator = new PdfColumnGenerator(checklist);
            pdfColumnGenerator.generatePdfChecklistColumns(view);

            /*
            "Print" the checklist
             */
            PdfPrinter pdfPrinter = new PdfPrinter(infoList, partList);
            File file = pdfPrinter.generatePdfFile(pdfColumnGenerator, view);

            if (file != null && file.exists()) {
                Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);

                Intent intent = ShareCompat.IntentBuilder.from(this)
                        .setStream(uri)
                        .setType("application/pdf")
                        .getIntent()
                        .setAction(Intent.ACTION_VIEW)
                        .setDataAndType(uri, "application/pdf")
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Snackbar.make(view,
                            "Install a pdf viewer to open this file",
                            Snackbar.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    /*
    Gets the user input from the info field edit texts and stores them in the info list
     */
    private boolean fillInfoList() {
        String userIn;
        for (InfoItem infoItem : infoList.getInfoList()) {
            userIn = infoItem.getEditText().getText().toString().trim();
            if (TextUtils.isEmpty(userIn) && infoItem.isRequired()) {
                Toast.makeText(
                        this,
                        "Please fill required field: " + infoItem.getFieldName(),
                        Toast.LENGTH_LONG
                ).show();
                return false;
            } else {
                infoItem.setUserInput(userIn);
            }
        }
        return true;
    }

    /*
    Gets the user input from the parts list edit texts and stores them in the part list
     */
    private boolean fillPartList() {
        String userIn;
        for (Part part : partList.getPartList()) {
            EditText[] editTexts = part.getEditTextArr();
            if (anyAreFilled(editTexts)) {
                if (anyAreEmpty(editTexts)) {
                    Toast.makeText(
                            this,
                            "Please fill all or none of each part",
                            Toast.LENGTH_LONG
                    ).show();
                    return false;
                } else {
                    userIn = editTexts[0].getText().toString().trim();
                    part.setQty(userIn);
                    userIn = editTexts[1].getText().toString().trim();
                    part.setPartNumber(userIn);
                    userIn = editTexts[2].getText().toString().trim();
                    part.setDescription(userIn);
                }
            }
        }
        return true;
    }

    private boolean anyAreFilled(EditText[] editTexts) {
        for (EditText editText : editTexts) {
            if (!TextUtils.isEmpty(editText.getText().toString().trim())) {
                return true;
            }
        }
        return false;
    }

    private boolean anyAreEmpty(EditText[] editTexts) {
        for (EditText editText : editTexts) {
            if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                return true;
            }
        }
        return false;
    }

    /*
    Adds formatting to phone numbers and dates to make them look good on the pdf
     */
    private void fixFieldFormatting() {
        // 2 - Phone Number
        InfoItem phoneItem = infoList.getInfoList().get(2);
        String number = phoneItem.getUserInput();
        number = number.replaceAll("[^0-9]",""); // leave only numbers
        if (number.length() == 10) {
            number = "(" + number.substring(0, 3) + ") " + number.substring(3, 6) + "-" + number.substring(6);
            phoneItem.setUserInput(number);
        }

        // 7 - Date
        InfoItem dateItem = infoList.getInfoList().get(7);
        String date = dateItem.getUserInput();
        date = date.replaceAll("[^0-9]", ""); // leave only numbers
        if (date.length() == 8) {
            date = date.substring(0, 2) + "-" + date.substring(2, 4) + "-" + date.substring(4);
            dateItem.setUserInput(date);
        }
    }

    @Override // handle the back button in the title bar
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}