package com.example.cse110.teamproject;

import android.content.Context;
import android.util.Pair;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PathFinder {

    // calculates path given search list, graph - no calls to database
    private static List<GraphPath<String,IdentifiedWeightedEdge>> findPath(
            Set<String> searchList,
            Graph<String, IdentifiedWeightedEdge> zooGraph,
            String firstNode) {
        String start = firstNode;

        List<GraphPath<String, IdentifiedWeightedEdge>> calculatedPaths = new ArrayList();

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

        return calculatedPaths;
    }

    // calculates paths with database + context for json
    public static List<GraphPath<String, IdentifiedWeightedEdge>> findPath(Context context) {
        final String start = "entrance_exit_gate";

        // initialize some empty List<> calculatedPath

        // load user exhibits into some List<> searchList
        Set<String> searchList = ExhibitDatabase.getSingleton(context)
                .userExhibitListItemDao().getAllUserExhibits().stream()
                .map(n -> n.node_id)
                .collect(Collectors.toSet());

        // note: probably sort before the following

        // construct graph
        Graph<String, IdentifiedWeightedEdge> zooGraph = ZooData.loadZooGraphJSON(context, "sample_zoo_graph.json");

        List<GraphPath<String, IdentifiedWeightedEdge>> calculatedPaths = findPath(searchList, zooGraph, start);

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

    public static GraphPath<String, IdentifiedWeightedEdge> findPathToFixedNext(Context context, String currLoc, String fixedNext) {
        Set<String> searchList = ExhibitDatabase.getSingleton(context)
                .userExhibitListItemDao().getAllUserExhibits().stream()
                .map(n -> n.node_id)
                .collect(Collectors.toSet());

        Graph<String, IdentifiedWeightedEdge> zooGraph = ZooData.loadZooGraphJSON(context, "sample_zoo_graph.json");

        return DijkstraShortestPath.findPathBetween(zooGraph, currLoc, fixedNext);
    }

}