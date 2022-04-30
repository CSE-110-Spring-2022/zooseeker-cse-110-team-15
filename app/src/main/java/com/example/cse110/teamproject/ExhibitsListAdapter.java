package com.example.cse110.teamproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class ExhibitsListAdapter extends RecyclerView.Adapter<ExhibitsListAdapter.ViewHolder> {
    private List<ExhibitNodeItem> exhibitItems = Collections.emptyList();

    public void setExhibitListItems(List<ExhibitNodeItem> newExhibitItems) {
        this.exhibitItems.clear();
        this.exhibitItems = newExhibitItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.exhibit_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setExhibitItem(exhibitItems.get(position));
    }

    @Override
    public int getItemCount() {
        return exhibitItems.size();
    }

//    @Override
//    public long getItemID

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private ExhibitNodeItem exhibitItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.exhibit_item_text);
        }

        public ExhibitNodeItem getExhibitItem() {
            return exhibitItem;
        }

        public void setExhibitItem(ExhibitNodeItem exhibitItem) {
            this.exhibitItem = exhibitItem;
            this.textView.setText(exhibitItem.name);
        }


    }
}
