package com.example.cse110.teamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExhibitsDirectionsActivity extends AppCompatActivity {
    final String DIR_FORMAT = "%d. Walk %.0f feet along %s from '%s' to '%s'.\n\n";
    final String DIST_FORMAT = "%.0f ft";
    final String EMPTY_STRING = "";
    final String JSON_EDGE = "sample_edge_info.json";
    final String JSON_ZOO = "sample_zoo_graph.json";
    final String LABEL_FORMAT = "(%s, %.0f ft)";

    Button prevButton;
    Button nextButton;
    TextView destName;
    TextView destDistance;
    TextView destLocation;
    TextView prevButtonLabel;
    TextView nextButtonLabel;
    TextView directionSteps;

    int directionOrder;
    String directions;
    double totalDistance;

    GraphPath<String, IdentifiedWeightedEdge> currentPath;
    Map<String, ZooData.EdgeInfo> eInfo;
    ExhibitListItemDao exhibitListItemDao;
    List<GraphPath<String, IdentifiedWeightedEdge>> pathList;
    Graph<String, IdentifiedWeightedEdge> zooGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibits_directions);

        // dao used for querying ExhibitNodeItem
        exhibitListItemDao = ExhibitDatabase.getSingleton(this).exhibitListItemDao();

        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);
        destName = findViewById(R.id.dest_name);
        destDistance = findViewById(R.id.dest_dist);
        destLocation = findViewById(R.id.dest_loc);
        prevButtonLabel = findViewById(R.id.prev_button_label);
        nextButtonLabel = findViewById(R.id.next_button_label);
        directionSteps = findViewById(R.id.direction_steps);

        totalDistance = 0;
        directionOrder = 0;
        directions = EMPTY_STRING;

        // get zoo data to be used for displaying directions
        zooGraph = ZooData.loadZooGraphJSON(this,JSON_ZOO);

        // find path and store it as a list
        pathList = PathFinder.findPath(this);

        // get path to first exhibit in the list
        currentPath = pathList.get(directionOrder);

//        // disable prev button when on first page
//        prevButton.setEnabled(directionOrder != 0);
//        nextButton.setEnabled(directionOrder != pathList.size() - 1);

        // Load the information about edges
        eInfo = ZooData.loadEdgeInfoJSON(this,JSON_EDGE);

        // update the page to display correct path info
        displayDirection();
        displayDestinationInfo();
        updateButtonAndLabel();
    }

    public void onPreviousIconClicked(View view) {
        // get the path for previous exhibit
        currentPath = pathList.get(--directionOrder);
        displayDirection();
        displayDestinationInfo();
        updateButtonAndLabel();
    }

    public void onNextIconClicked(View view) {
        // get the path for next exhibit
        currentPath = pathList.get(++directionOrder);
        displayDirection();
        displayDestinationInfo();
        updateButtonAndLabel();
    }

    @SuppressLint("DefaultLocale")
    public void displayDirection() {
        int i = 1;
        List<String> vertexList = currentPath.getVertexList();
        for (IdentifiedWeightedEdge e : currentPath.getEdgeList()) {
            ExhibitNodeItem source = exhibitListItemDao.getExhibitByNodeId(vertexList.get(i-1));
            ExhibitNodeItem target = exhibitListItemDao.getExhibitByNodeId(vertexList.get(i));
            directions += String.format(DIR_FORMAT,
                    i,
                    zooGraph.getEdgeWeight(e),
                    Objects.requireNonNull(eInfo.get(e.getId())).street,
                    source.name,
                    target.name);
            i++;
        }
        directionSteps.setText(directions);
        directions = EMPTY_STRING;
    }

    @SuppressLint("DefaultLocale")
    public void displayDestinationInfo() {
        List<IdentifiedWeightedEdge> edgeList = currentPath.getEdgeList();
        String destId = currentPath.getEndVertex();
        ExhibitNodeItem destinationNode = exhibitListItemDao.getExhibitByNodeId(destId);


        destName.setText(destinationNode.name);

        @SuppressLint("DefaultLocale") String distance = String.format(DIST_FORMAT, currentPath.getWeight());
        destDistance.setText(distance);


        String street = Objects.requireNonNull(eInfo.get(edgeList.get(edgeList.size()-1).getId())).street;
        destLocation.setText(street);

        @SuppressLint("DefaultLocale") String label = String.format(LABEL_FORMAT, destinationNode.name, currentPath.getWeight());
        prevButtonLabel.setText(label);

        if (directionOrder == pathList.size()-1) {
            nextButtonLabel.setText(EMPTY_STRING);
        }
        else {
            GraphPath<String, IdentifiedWeightedEdge> nextPath = pathList.get(directionOrder + 1);
            String nextDestId = nextPath.getEndVertex();
            ExhibitNodeItem nextDestNode = exhibitListItemDao.getExhibitByNodeId(nextDestId);
            label = String.format(LABEL_FORMAT, nextDestNode.name, nextPath.getWeight());
            nextButtonLabel.setText(label);
        }
        totalDistance = 0.0;
    }

    public void updateButtonAndLabel() {
        prevButton.setEnabled(directionOrder != 0);
        nextButton.setEnabled(directionOrder != pathList.size() - 1);

        if (directionOrder == 0) {
            prevButtonLabel.setText(EMPTY_STRING);
        }

        if (directionOrder == pathList.size() - 1) {
            nextButtonLabel.setText(EMPTY_STRING);
        }
    }


}