package com.example.cse110.teamproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder>{
    private List<PlanItem> planItems = Collections.emptyList();

    public void setPlanItems(List<PlanItem> newPlanItems){
        this.planItems.clear();
        this.planItems = newPlanItems;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.plan_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setPlanItem(planItems.get(position));
    }

    @Override
    public int getItemCount() {
        return planItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView street;
        private final TextView exhibit;
        private final TextView distance;
        private PlanItem planitem;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            this.street = itemView.findViewById(R.id.street_text);
            this.exhibit = itemView.findViewById(R.id.exhibit_text);
            this.distance = itemView.findViewById(R.id.distance_text);
        }

        public PlanItem getPlanItem(){
            return planitem;
        }

        public void setPlanItem(PlanItem planItem) {
            this.planitem = planItem;
            this.street.setText(planItem.street);
            this.distance.setText(planItem.distance);
            this.exhibit.setText(String.valueOf(planItem.exhibit));
        }
    }
}
