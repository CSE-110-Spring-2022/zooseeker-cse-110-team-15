package com.example.cse110.teamproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlanActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    ExhibitListItemDao exhibitListItemDao;
    double totalDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        exhibitListItemDao = ExhibitDatabase.getSingleton(this)
                .exhibitListItemDao();

        List<PlanItem> planItemList;


        PlanAdapter adapter = new PlanAdapter();
        adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.plan_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Map<String, ZooData.EdgeInfo> eInfo = ZooData.loadEdgeInfoJSON(this, this.getResources().getString(R.string.curr_edge_info));

        totalDistance = 0;
        planItemList = PathFinder.findPath(this).stream().map(gp-> {
            List<IdentifiedWeightedEdge> identifiedWeightedEdges = gp.getEdgeList();
            double pathWeight = gp.getWeight();
            totalDistance += pathWeight;
            String nodeName = exhibitListItemDao.getExhibitByNodeId(gp.getEndVertex()).name;
            String edgeID = identifiedWeightedEdges.get(identifiedWeightedEdges.size() - 1).getId();
            String edgeName = eInfo.get(edgeID).street;
            return new PlanItem(edgeName, nodeName, totalDistance);
        }).collect(Collectors.toList());

        adapter.setPlanItems(planItemList);

    }

    public void onDirectionsClicked(View view) {
        Intent intent = new Intent(this, ExhibitsDirectionsActivity.class);
        startActivity(intent);
    }
}