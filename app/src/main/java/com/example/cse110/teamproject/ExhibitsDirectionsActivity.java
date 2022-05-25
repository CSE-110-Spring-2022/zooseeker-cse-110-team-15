package com.example.cse110.teamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cse110.teamproject.path.PathFinder;
import com.example.cse110.teamproject.path.PathManager;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExhibitsDirectionsActivity extends AppCompatActivity {
    final String DIR_FORMAT = "%d. Walk %.0f feet along %s from '%s' to '%s'.\n\n";
    final String DIST_FORMAT = "%.0f ft";
    final String EMPTY_STRING = "";
    final String LABEL_FORMAT = "(%s, %.0f ft)";
    final String SHARED_PREF_KEY = "shared_dir_mode";
    final String BRIEF_DIR_VAL = "brief_dir";
    final String DETAILED_DIR_VAL = "detailed_dir";

    private SharedPreferences preferences;

    // have to be non final since we get filenames in on create
    String JSON_EDGE = "";
    String JSON_ZOO = "";

    Button prevButton;
    Button nextButton;
    TextView destName;
    TextView destDistance;
    TextView destLocation;
    TextView prevButtonLabel;
    TextView nextButtonLabel;
    TextView directionSteps;

    boolean briefMode;
    int directionOrder;
    String directions;
    double totalDistance;

    double totalBriefDistance;

    GraphPath<String, IdentifiedWeightedEdge> currentPath;
    Map<String, ZooData.EdgeInfo> eInfo;
    ExhibitListItemDao exhibitListItemDao;
    List<GraphPath<String, IdentifiedWeightedEdge>> pathList;
    Graph<String, IdentifiedWeightedEdge> zooGraph;

    PathManager pathManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        JSON_EDGE = this.getResources().getString(R.string.curr_edge_info);
        JSON_ZOO = this.getResources().getString(R.string.curr_graph_info);
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

        // disable prev button when on first page
        prevButton.setEnabled(directionOrder != 0);
        nextButton.setEnabled(pathList.size() - 1 != directionOrder && pathList.size() > 0);

        // Load the information about edges
        eInfo = ZooData.loadEdgeInfoJSON(this, JSON_EDGE);

        // Create user location
        pathManager = new PathManager(this);

        preferences = getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);

        if (preferences.getString(SHARED_PREF_KEY, null).equals(BRIEF_DIR_VAL)) {
            briefMode = true;
        }
        else {
            briefMode = false;
        }

        if (pathList.size() > 0) {
            // get first path in the list
            currentPath = pathList.get(directionOrder);

            // update the page to display correct path info
            switchDirectionMode(briefMode);
            displayDestinationInfo();
            updateButtonAndLabel();
        }

        sharedPreferenceChangeListener(preferences);
    }

    private void sharedPreferenceChangeListener(SharedPreferences sp) {
        sp.registerOnSharedPreferenceChangeListener((sharedPreferences, s) -> {
            if (sharedPreferences.getString(SHARED_PREF_KEY, null).equals(BRIEF_DIR_VAL)) {
                //currently a brief direction mode is selected -> so call displayBriefDirection();
                briefMode = true;
                switchDirectionMode(true);

            }
            else if (sharedPreferences.getString(SHARED_PREF_KEY, null).equals(DETAILED_DIR_VAL)) {
                //currently a detailed direction mode is selected -> so call displayDetailedDirection();
                briefMode = false;
                switchDirectionMode(false);
            }
        });
    }

    public void onPreviousIconClicked(View view) {
        // get the path for previous exhibit
        currentPath = pathList.get(--directionOrder);
        switchDirectionMode(briefMode);
        displayDestinationInfo();
        updateButtonAndLabel();
        notifyDirectionOrderChange();
    }

    public void onNextIconClicked(View view) {
        // get the path for next exhibit
        currentPath = pathList.get(++directionOrder);
        switchDirectionMode(briefMode);
        displayDestinationInfo();
        updateButtonAndLabel();
        notifyDirectionOrderChange();
    }

    public void notifyDirectionOrderChange() {
        pathManager.updateCurrentDirectionIndex(directionOrder);
    }

    @SuppressLint("DefaultLocale")
    public void displayBriefDirection() {
        // total distance within the nodes that share the same street
        double distanceSum;

        List<String> vertexList = currentPath.getVertexList();
        List<IdentifiedWeightedEdge> currentEdgeList = currentPath.getEdgeList();

        // index used to notify which index to skip inside the currentEdgeList
        int skippingIndex = 0;

        for (int i = 0; i < currentEdgeList.size(); i++) {
            if (i != skippingIndex) {
               continue;
            }

            int j = i;
            distanceSum = zooGraph.getEdgeWeight(currentEdgeList.get(i));
            int nodeIndex = i + 1;

            while (j < currentEdgeList.size() - 1 &&
                Objects.requireNonNull(eInfo.get(currentEdgeList.get(j).getId())).street.equals(
                Objects.requireNonNull(eInfo.get(currentEdgeList.get(j + 1).getId())).street)) {
            distanceSum += zooGraph.getEdgeWeight(currentEdgeList.get(j + 1));

            nodeIndex++;
            j++;
            skippingIndex = j + 1;
            }

            if (j == i) {
                skippingIndex = i + 1;
            }

            ExhibitNodeItem source = exhibitListItemDao.getExhibitByNodeId(vertexList.get(i));
            ExhibitNodeItem target = exhibitListItemDao.getExhibitByNodeId(vertexList.get(nodeIndex));
            directions += String.format(DIR_FORMAT,
                    i + 1,
                    distanceSum,
                    Objects.requireNonNull(eInfo.get(currentEdgeList.get(j).getId())).street,
                    source.name,
                    target.name);
        }
        directionSteps.setText(directions);
        directions = EMPTY_STRING;
    }

    @SuppressLint("DefaultLocale")
    public void displayDetailedDirection() {
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

    public void onSettingsButtonClicked(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void switchDirectionMode(boolean mode) {
        if (mode) {
            //currently a brief direction mode is selected -> so call displayBriefDirection();
            displayBriefDirection();
        }
        else {
            //currently a detailed direction mode is selected -> so call displayDetailedDirection();
            displayDetailedDirection();
        }
    }
}