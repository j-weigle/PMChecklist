package com.pmchecklist.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.pmchecklist.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar;
        actionBar = getSupportActionBar();

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#663333"));
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(colorDrawable);
        }
    }

    public void goToElectricChecklist(View view) {
        Intent electricActivity = new Intent(this, ChecklistActivity.class);
        electricActivity.putExtra("checklist_type", "Electric Checklist");
        startActivity(electricActivity);
    }

    public void goToCombustionChecklist(View view) {
        Intent combustionActivity = new Intent(this, ChecklistActivity.class);
        combustionActivity.putExtra("checklist_type", "Combustion Checklist");
        startActivity(combustionActivity);
    }

    public void openGeneratedDocumentsFolder(View view) {
        Intent fileBrowserActivity = new Intent(this, FileBrowserActivity.class);
        startActivity(fileBrowserActivity);
    }
}