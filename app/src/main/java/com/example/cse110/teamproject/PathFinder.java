package com.example.cse110.teamproject;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ManyToManyShortestPathsAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraManyToManyShortestPaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathFinder extends Application {

    public static List<GraphPath<String, IdentifiedWeightedEdge>> findPath(Context context) {
        String start = "entrance_exit_gate";

        // Graph<String, IdentifiedWeightedEdge>;

        // initialize some empty List<> calculatedPath                     // TODO: determine return value
        List<GraphPath<String, IdentifiedWeightedEdge>> calculatedPaths = new ArrayList<>();

        // load user exhibits into some List<> searchList
        Set<String> searchList = ExhibitDatabase.getSingleton(context)
                .userExhibitListItemDao().getAllUserExhibits().stream()
                .map(n -> n.node_id)
                .collect(Collectors.toSet());

        // note: probably sort before the following

        // construct graph
        Graph<String, IdentifiedWeightedEdge> zooGraph = ZooData.loadZooGraphJSON(context, "sample_zoo_graph.json");

        DijkstraShortestPath dijkstras = new DijkstraShortestPath(zooGraph);


        while(!searchList.isEmpty()) {
            ShortestPathAlgorithm.SingleSourcePaths<String, IdentifiedWeightedEdge> paths = dijkstras.getPaths(start);

            // find exhibit with shortest path
            Pair<String, Double> shortestPair = null;
            for (String exhibit : searchList) {
                double currWeight = paths.getWeight(exhibit);
                if ((shortestPair == null) || currWeight < shortestPair.second) {
                    shortestPair = new Pair(exhibit, currWeight);
                }
            }

            // add path to return list
            calculatedPaths.add(paths.getPath(shortestPair.first));
            start = shortestPair.first;

            searchList.remove(shortestPair.first);
        }

        PathItemDao pathItemDao = ExhibitDatabase.getSingleton(context).pathItemDao();
        pathItemDao.deletePathItems();

        for (int i = 0; i < calculatedPaths.size(); i++) {
            pathItemDao.insert(new PathItem(calculatedPaths.get(i).getEndVertex(),
                    calculatedPaths.get(i).getEdgeList().stream()
                            .map(e -> e.getId()).collect(Collectors.toList()),
                    i));
        }

        return calculatedPaths;
    }
}
