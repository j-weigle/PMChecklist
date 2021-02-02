package com.pmchecklist.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pmchecklist.R;
import com.pmchecklist.model.ChecklistItem;

import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private final List<ChecklistItem> data;

    public ChecklistAdapter(List<ChecklistItem> data) {
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(data.get(position).getHeader())) {
            return TYPE_ITEM;
        } else {
            return TYPE_HEADER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == TYPE_HEADER) {
            view = inflater.inflate(R.layout.checklist_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.checklist_rg_description, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            /* Set what appears to the user in the view */
            ((HeaderViewHolder) holder).setHead(data.get(position).getHeader());
        } else if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).rg.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.radio_button_ok) {
                    data.get(position).setSelection(ChecklistItem.Selection.OK);
                } else  if (checkedId == R.id.radio_button_rr) {
                    data.get(position).setSelection(ChecklistItem.Selection.RR);
                } else  if (checkedId == R.id.radio_button_na) {
                    data.get(position).setSelection(ChecklistItem.Selection.NA);
                } else {
                    data.get(position).setSelection(ChecklistItem.Selection.None);
                }
            });

            /* Set what appears to the user in the view */
            ((ItemViewHolder) holder).setDesc(data.get(position).getDescription());
            ((ItemViewHolder) holder).setVisualSelection(data.get(position).getSelection());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView header;

        HeaderViewHolder(@NonNull View view) {
            super(view);
            header = view.findViewById(R.id.text_view_header);
        }

        void setHead(String text) {
            header.setText(text);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView description;
        private final RadioGroup rg;

        ItemViewHolder(@NonNull View view) {
            super(view);
            description = view.findViewById(R.id.text_view_description);
            rg = view.findViewById(R.id.radio_group_selection);
        }

        void setDesc(String text) {
            description.setText(text);
        }

        void setVisualSelection(ChecklistItem.Selection selection) {
            if (selection == ChecklistItem.Selection.None) {
                rg.clearCheck();
            } else if (selection == ChecklistItem.Selection.OK) {
                rg.check(R.id.radio_button_ok);
            } else if (selection == ChecklistItem.Selection.RR) {
                rg.check(R.id.radio_button_rr);
            } else if (selection == ChecklistItem.Selection.NA) {
                rg.check(R.id.radio_button_na);
            }
        }
    }
}