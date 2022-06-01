package com.example.cse110.teamproject;

import static com.example.cse110.teamproject.persistence.PersistData.Activity.DIRECTIONS;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse110.teamproject.path.PathFinder;
import com.example.cse110.teamproject.persistence.PersistData;

import org.jgrapht.GraphPath;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
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

        AtomicReference<String> streetName = new AtomicReference<>("previous street name placeholder");
        planItemList = PathFinder.findPath(this).stream().map(pathInfo-> {
            GraphPath<String, IdentifiedWeightedEdge> gp = pathInfo.getPath();
            List<IdentifiedWeightedEdge> identifiedWeightedEdges = gp.getEdgeList();
            double pathWeight = gp.getWeight();
            totalDistance += pathWeight;
            if (identifiedWeightedEdges.size() > 0) {
                String edgeID = identifiedWeightedEdges.get(identifiedWeightedEdges.size() - 1).getId();
                streetName.set(eInfo.get(edgeID).street);
            }
            String nodeName = exhibitListItemDao.getExhibitByNodeId(pathInfo.nodeId).name;
            return new PlanItem(streetName.get(), nodeName, totalDistance);
        }).collect(Collectors.toList());

        adapter.setPlanItems(planItemList);

    }

    public void onDirectionsClicked(View view) {
        Intent intent = new Intent(this, ExhibitsDirectionsActivity.class);
        startActivity(intent);
    }

    public void onSettingsButtonClicked(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        PersistData persistData = new PersistData();
        persistData.writeActivity(PersistData.Activity.PLAN, this);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        PersistData persistData = new PersistData();
//        persistData.resume(PersistData.Activity.PLAN, this);
//    }
}