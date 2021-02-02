package com.pmchecklist.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pmchecklist.R;
import com.pmchecklist.activity.FileBrowserActivity;
import com.pmchecklist.model.FileItem;

import java.util.List;

public class FileBrowserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    FileBrowserActivity activity;
    private final List<FileItem> data;

    public FileBrowserAdapter(FileBrowserActivity activity, List<FileItem> data) {
        this.activity = activity;
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.file_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener((view) -> {
            if (data.get(position).getFile().isDirectory()) {
                activity.openDirectory(data.get(position).getFile());
            } else if (data.get(position).getFile().isFile()) {
                activity.openFile(data.get(position).getFile());
            }
        });
        ((ItemViewHolder) holder).checkBox.setOnCheckedChangeListener((checkBox, isChecked) -> {
            if (data.size() > 0) {
                data.get(position).setChecked(isChecked);
            }
        });

        /* Set what appears to the user in the view */
        ((ItemViewHolder)holder).setLabel(data.get(position).getFile().getName());
        ((ItemViewHolder) holder).checkBox.setChecked(data.get(position).isChecked());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView label;
        private final CheckBox checkBox;

        ItemViewHolder(@NonNull View view) {
            super(view);
            label = view.findViewById(R.id.text_view_filename);
            checkBox = view.findViewById(R.id.checkBox);
        }

        void setLabel(String text) {
            label.setText(text);
        }
    }
}
