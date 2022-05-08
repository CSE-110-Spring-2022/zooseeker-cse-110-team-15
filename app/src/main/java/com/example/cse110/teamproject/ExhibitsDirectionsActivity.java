package com.example.cse110.teamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.List;
import java.util.Map;

public class ExhibitsDirectionsActivity extends AppCompatActivity {
//    final String nextButtonLabel = "Next";
//    final String prevButtonLabel = "Previous";
////    PathItemDao pathItemDao;
////    PathItem pathItem;
//    Button prevButton;
//    Button nextButton;
//
//    // yame
//    List<PathItem> pathItemList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_exhibits_directions);
//
////        pathItemDao = ExhibitDatabase.getSingleton(this).pathItemDao();
////        pathItem = pathItemDao.getByOrder(0);
//
//        prevButton = findViewById(R.id.previous_button);
//        nextButton = findViewById(R.id.next_button);
//
////        pathItemList = pathItemDao.getAll();
//
//        Graph<String, IdentifiedWeightedEdge> zooGraph = ZooData.loadZooGraphJSON(this,"sample_zoo_graph.json");
//        List<GraphPath<String, IdentifiedWeightedEdge>> graphPathList = PathFinder.findPath(this);
//
//        ExhibitListItemDao exhibitListItemDao;
//        // 2. Load the information about our nodes and edges...
//        Map<String, ZooData.EdgeInfo> eInfo = ZooData.loadEdgeInfoJSON(this,"sample_edge_info.json");
//
//        ExhibitListItemDao dao = ExhibitDatabase.getSingleton(this).exhibitListItemDao();
//
//
//        int i = 1;
//        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
//            ExhibitNodeItem source = dao.getExhibitByNodeId(zooGraph.getEdgeSource(e));
//            ExhibitNodeItem target = dao.getExhibitByNodeId(zooGraph.getEdgeTarget(e));
//            System.out.printf("  %d. Walk %.0f meters along %s from '%s' to '%s'.\n",
//                    i,
//                    g.getEdgeWeight(e),
//                    eInfo.get(e.getId()).street,
//                    vInfo.get(g.getEdgeSource(e).toString()).name,
//                    vInfo.get(g.getEdgeTarget(e).toString()).name);
//            i++;
//        }
//
//    }
//
//    public void onPreviousIconClicked(View view) {
//        prevButton.setOnClickListener(v -> {
////            pathItem = pathItemDao.getByOrder(pathItem.order--);
//            // Textview.setText(pathItem.node_id)... and so on
//
//            if (pathItem.order == 0) {
//                prevButton.setEnabled(false);
//            }
//            else {
//                prevButton.setEnabled(true);
//            }
//        });
//    }
//
//    public void onNextIconClicked(View view) {
//        prevButton.setOnClickListener(v -> {
////            pathItem = pathItemDao.getByOrder(pathItem.order++);
//            // Textview.setText(pathItem.node_id)... and so on
//
////            if (pathItem.order == pathItemList.size() - 1) {
//                nextButton.setEnabled(false);
//            }
////            else {
//                nextButton.setEnabled(true);
//            }
//        });
//    }

}