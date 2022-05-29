package com.example.cse110.teamproject.path;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.example.cse110.teamproject.ExhibitDatabase;
import com.example.cse110.teamproject.IdentifiedWeightedEdge;
import com.example.cse110.teamproject.PathItem;
import com.example.cse110.teamproject.PathItemDao;
import com.example.cse110.teamproject.R;
import com.example.cse110.teamproject.ZooData;

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
    private static List<GraphPath<String, IdentifiedWeightedEdge>> findPath(
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

        // calculate path from last exhibit back to entrance/exit gate
        if (calculatedPaths.size() > 0) {
            String lastExhibitId = calculatedPaths.get(calculatedPaths.size()-1).getEndVertex();
            GraphPath<String, IdentifiedWeightedEdge> lastPath =
                    DijkstraShortestPath.findPathBetween(zooGraph, lastExhibitId, firstNode);

            // add the last path to path list
            calculatedPaths.add(lastPath);
        }

        return calculatedPaths;
    }

    // calculates paths with database + context for json
    public static List<PathInfo> findPath(Context context) {
        final String start = "entrance_exit_gate";

        // initialize some empty List<> calculatedPath

        // load user exhibits into some List<> searchList
        Set<String> searchList = ExhibitDatabase.getSingleton(context)
                .userExhibitListItemDao().getAllUserExhibits().stream()
                .map(n -> n.node_id)
                .collect(Collectors.toSet());

        // note: probably sort before the following

        // construct graph
        Graph<String, IdentifiedWeightedEdge> zooGraph = ZooData.loadZooGraphJSON(context, context.getResources().getString(R.string.curr_graph_info));

        List<GraphPath<String, IdentifiedWeightedEdge>> calculatedPaths = findPath(searchList, zooGraph, start);

        PathItemDao pathItemDao = ExhibitDatabase.getSingleton(context).pathItemDao();
        pathItemDao.deletePathItems();

        for (int i = 0; i < calculatedPaths.size(); i++) {
            pathItemDao.insert(new PathItem(calculatedPaths.get(i).getEndVertex(),
                    calculatedPaths.get(i).getEdgeList().stream()
                            .map(e -> e.getId()).collect(Collectors.toList()),
                    i));
        }

        List<PathInfo> paths = calculatedPaths.stream().map((path) ->
                new PathInfo(path)).collect(Collectors.toList());

        return paths;
    }

    public static GraphPath<String, IdentifiedWeightedEdge> findPathToFixedNext(Context context, String currLoc, String fixedNext) {
        Graph<String, IdentifiedWeightedEdge> zooGraph = ZooData.loadZooGraphJSON(context, context.getResources().getString(R.string.curr_graph_info));

        return DijkstraShortestPath.findPathBetween(zooGraph, currLoc, fixedNext);
    }

    public static List<GraphPath<String, IdentifiedWeightedEdge>> findPathGivenExcludedNodes
            (Context context, String currLoc, List<String> nodesToOmit) {
        final String start = currLoc;

        // load user exhibits into searchList
        Set<String> searchList = ExhibitDatabase.getSingleton(context)
                .userExhibitListItemDao().getAllUserExhibits().stream()
                .map(n -> n.node_id)
                .collect(Collectors.toSet());

        // remove selected nodes from searchList
        for (String s : nodesToOmit) {
            searchList.remove(s);
        }

        Graph<String, IdentifiedWeightedEdge> zooGraph = ZooData.loadZooGraphJSON(context, context.getResources().getString(R.string.curr_graph_info));

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

}