package com.example.cse110.teamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.List;
import java.util.Map;

public class ExhibitsDirectionsActivity extends AppCompatActivity {
    Button prevButton;
    Button nextButton;
    TextView destName;
    TextView destDistance;
    TextView destLocation;
    TextView prevButtonLabel;
    TextView nextButtonLabel;

    int directionOrder;
    List<GraphPath<String, IdentifiedWeightedEdge>> pathList;
    GraphPath<String, IdentifiedWeightedEdge> currentPath;
    ExhibitListItemDao exhibitListItemDao;
    Graph<String, IdentifiedWeightedEdge> zooGraph;
    Map<String, ZooData.EdgeInfo> eInfo;
    String directions;
    String directionFormat = "%d. Walk %.0f meters along %s from '%s' to '%s'.\n";
    double totalDistance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibits_directions);

        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);
        destName = findViewById(R.id.dest_name);
        destDistance = findViewById(R.id.dest_dist);
        destLocation = findViewById(R.id.dest_loc);
        prevButtonLabel = findViewById(R.id.prev_button_label);
        nextButtonLabel = findViewById(R.id.next_button_label);

        // find path
        zooGraph = ZooData.loadZooGraphJSON(this,"sample_zoo_graph.json");
        pathList = PathFinder.findPath(this);

        // get first path in the list
        directionOrder = 0;
        currentPath = pathList.get(directionOrder);

        // 2. Load the information about our nodes and edges...
        eInfo = ZooData.loadEdgeInfoJSON(this,"sample_edge_info.json");

        exhibitListItemDao = ExhibitDatabase.getSingleton(this).exhibitListItemDao();
    }

    public void onPreviousIconClicked(View view) {
        prevButton.setOnClickListener(v -> {
            currentPath = pathList.get(--directionOrder);

            displayDirection();
            displayDestinationInfo();


            prevButton.setEnabled(directionOrder != 0);
            if (directionOrder == 0) {
                prevButtonLabel.setText("");
            }
        });
    }

    public void onNextIconClicked(View view) {
        prevButton.setOnClickListener(v -> {
            currentPath = pathList.get(++directionOrder);

            displayDirection();
            displayDestinationInfo();

            nextButton.setEnabled(directionOrder != pathList.size() - 1);
            if (directionOrder == pathList.size() - 1) {
                nextButtonLabel.setText("");
            }
        });
    }

    public void displayDirection() {
        int i = 1;
        for (IdentifiedWeightedEdge e : currentPath.getEdgeList()) {
            ExhibitNodeItem source = exhibitListItemDao.getExhibitByNodeId(zooGraph.getEdgeSource(e));
            ExhibitNodeItem target = exhibitListItemDao.getExhibitByNodeId(zooGraph.getEdgeTarget(e));
            directions += String.format(directionFormat,
                    i,
                    zooGraph.getEdgeWeight(e),
                    eInfo.get(e.getId()).street,
                    source.name,
                    target.name);
            i++;
        }
//        directionView.setText(directions);
        directions = "";
    }

    public void displayDestinationInfo() {
        List<IdentifiedWeightedEdge> pathEdgeList = currentPath.getEdgeList();
        IdentifiedWeightedEdge lastEdge = pathEdgeList.get(pathEdgeList.size()-1);
        ExhibitNodeItem destinationNode = exhibitListItemDao.getExhibitByNodeId(zooGraph.getEdgeTarget(lastEdge));
        String street = eInfo.get(lastEdge.getId()).street;

        destName.setText(destinationNode.name);
        destDistance.setText(Double.toString(currentPath.getWeight()));
        destLocation.setText(street);

        prevButtonLabel.setText(destinationNode.name + "," + Double.toString(currentPath.getWeight()));
        totalDistance = 0;
    }
}