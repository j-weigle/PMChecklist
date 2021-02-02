package com.pmchecklist.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.pmchecklist.R;
import com.pmchecklist.adapter.FileBrowserAdapter;
import com.pmchecklist.model.FileItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileBrowserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    RecyclerView recyclerView;
    private File currentDirectory;
    private SortMethod currentSortMethod;
    private List<FileItem> currentFileList;

    private enum SortMethod {
        Newest("Newest First"), Oldest("Oldest First"),
        Alphabetical("A-Z"), RevAlphabetical("Z-A");

        final String value;

        SortMethod(String value) { this.value = value; }

        @NonNull
        @Override
        public String toString() {
            return value;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        /*
        Set the current sorting method for file order and put sorting options in spinner (dropdown)
         */
        currentSortMethod = SortMethod.Newest;
        Spinner spinner = findViewById(R.id.sort_spinner);
        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, SortMethod.values()));

        recyclerView = findViewById(R.id.file_browser_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        /*
        Open starting directory and set that as the current file list
         */
        currentDirectory = this.getExternalFilesDir(null);
        File[] files = sortCurrentDirectory();
        updateFileList(files);

        /*
        Initialize recycler view with current file list
         */
        RecyclerView.Adapter<RecyclerView.ViewHolder> fileBrowserAdapter = new FileBrowserAdapter(this, currentFileList);
        recyclerView.setAdapter(fileBrowserAdapter);
    }

    public void openDirectory(File directory) {
        currentDirectory = directory;
        File[] files = sortCurrentDirectory();
        updateFileList(files);
        updateFileBrowserAdapter();
    }

    public void openFile(File file) {
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
                Snackbar.make(this.findViewById(R.id.file_browser_recycler),
                        "Install a pdf viewer to open this file",
                        Snackbar.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void updateFileList(File[] files) {
        if (files != null) {
            currentFileList = new ArrayList<>();
            for (File file : files) {
                currentFileList.add(new FileItem(file));
            }
        }
    }

    /*
    Reuses the visible recycler view to display the current file list
     */
    private void updateFileBrowserAdapter() {
        RecyclerView.Adapter<RecyclerView.ViewHolder> fileBrowserAdapter = new FileBrowserAdapter(this, currentFileList);
        recyclerView.swapAdapter(fileBrowserAdapter, true);
    }

    public void confirmDeleteFiles(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Do you really want to delete the selected files?")
                .setIcon(android.R.drawable.ic_menu_delete)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> deleteCheckedFiles())
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void deleteCheckedFiles() {
        for (int i = 0; i < currentFileList.size(); i++) {
            FileItem fileItem = currentFileList.get(i);
            if (fileItem.isChecked()) {
                boolean result;
                if (fileItem.getFile().isDirectory()) {
                    result = deleteDirectory(fileItem.getFile());
                } else {
                    result = fileItem.getFile().delete();
                }
                if (!result) {
                    Snackbar.make(this.findViewById(R.id.file_browser_recycler),
                            "Failed to delete file",
                            Snackbar.LENGTH_LONG).show();
                    Log.d("FileBrowserActivity", "FAILED TO DELETE FILE");
                } else {
                    currentFileList.remove(i);
                    i--;
                }
            }
        }
        updateFileBrowserAdapter();
    }

    /*
    Recursively deletes files/directories in a directory
     */
    private boolean deleteDirectory(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteDirectory(child);
                }
            }
        }
        return file.delete();
    }

    private File[] sortCurrentDirectory() {
        if (currentDirectory.exists() && currentDirectory.isDirectory()) {
            File[] files = currentDirectory.listFiles();
            if (files != null) {
                if (currentSortMethod == SortMethod.Newest) {
                    Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
                } else if (currentSortMethod == SortMethod.Oldest) {
                    Arrays.sort(files, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));
                } else if (currentSortMethod == SortMethod.Alphabetical) {
                    Arrays.sort(files);
                } else if (currentSortMethod == SortMethod.RevAlphabetical) {
                    Arrays.sort(files, Collections.reverseOrder());
                }
                return files;
            }
        }
        return null;
    }

    private void goUpOneDirectory () {
        String fullPath = currentDirectory.getAbsolutePath();
        String[] splitPath = fullPath.split("/");
        String[] newSplitPath = new String[splitPath.length-1];
        if (newSplitPath.length >= 0) {
            System.arraycopy(splitPath, 0, newSplitPath, 0, newSplitPath.length);
        }
        String newPath = TextUtils.join("/", newSplitPath);
        openDirectory(new File(newPath));
    }

    @Override // spinner method
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentSortMethod = (SortMethod) parent.getItemAtPosition(position);
        openDirectory(currentDirectory);
    }

    @Override // spinner method
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override // handle the back button in the title bar
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (currentDirectory.getAbsolutePath().equals(this.getExternalFilesDir(null).getAbsolutePath())) {
                finish();
            } else {
                goUpOneDirectory();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // physical back button action
    public void onBackPressed() {
        if (currentDirectory.getAbsolutePath().equals(this.getExternalFilesDir(null).getAbsolutePath())) {
            finish();
        } else {
            goUpOneDirectory();
        }
    }
}