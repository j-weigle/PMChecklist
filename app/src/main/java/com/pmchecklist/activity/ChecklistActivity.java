package com.pmchecklist.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.pmchecklist.R;
import com.pmchecklist.adapter.ChecklistAdapter;
import com.pmchecklist.model.Checklist;
import com.pmchecklist.model.ChecklistContents;

public class ChecklistActivity extends AppCompatActivity {
    private static Checklist checklist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        Bundle b = getIntent().getExtras();
        String checklistType;
        if (b != null) {
            checklistType = b.getString("checklist_type");
        } else {
            Log.println(Log.ERROR,"ChecklistActivity",
                "FAILED TO GET CHECKLIST TYPE; getIntent().getExtras() RETURNED NULL BUNDLE"
            );
            return;
        }

        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#663333"));
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(checklistType);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView = findViewById(R.id.checklist_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        /*
        Get the checklist contents of the type selected and fill the checklist
         */
        checklist = new Checklist(checklistType);
        ChecklistContents checklistContents = new ChecklistContents(this.getResources());
        for (String[][] section : checklistContents.getContents(checklistType)) {
            checklist.addSectionToChecklist(section);
        }

        RecyclerView.Adapter<RecyclerView.ViewHolder> checklistAdapter = new ChecklistAdapter(checklist.getChecklist());
        recyclerView.setAdapter(checklistAdapter);
    }

    /*
    If every checklist item has a selection made, go to the InfoFields activity
     */
    public void goToInfoFieldsFromChecklist(View view) {
        Intent intent;
        if (checklist.allItemsSelected()) {
            intent = new Intent(this, InfoFieldsActivity.class);
        } else {
            Snackbar.make(view, "Please make a selection for all items", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
            return;
        }
        startActivity(intent);
    }

    public static Checklist getChecklist() {
        return checklist;
    }

    @Override // handle the back button in the title bar
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
