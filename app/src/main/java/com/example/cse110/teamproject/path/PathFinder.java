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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PathFinder {

    private static String ENTRANCE = "entrance_exit_gate";

    // calculates path given search list, graph - no calls to database
    // returns list of pair of vertex or child if exists and path
    private static List<Pair<String, GraphPath<String, IdentifiedWeightedEdge>>> findPath(
            Set<String> searchList,
            Graph<String, IdentifiedWeightedEdge> zooGraph,
            String firstNode,
            Context context
            ) {
        String start = getVertexNameFromExhibit(firstNode, context);

        List<Pair<String, GraphPath<String, IdentifiedWeightedEdge>>> calculatedPaths = new ArrayList();

        DijkstraShortestPath dijkstras = new DijkstraShortestPath(zooGraph);

        while(!searchList.isEmpty()) {
            Log.d("problem", start);
            ShortestPathAlgorithm.SingleSourcePaths<String, IdentifiedWeightedEdge> paths = dijkstras.getPaths(start);

            // find exhibit with shortest path
            Pair<String, Double> shortestPair = null;
            String childNodeId = null;

            for (String exhibit : searchList) {
                String currExhibit = getVertexNameFromExhibit(exhibit, context);

                double currWeight = paths.getWeight(currExhibit);
                if ((shortestPair == null) || currWeight < shortestPair.second) {
                    childNodeId = exhibit;
                    shortestPair = new Pair(currExhibit, currWeight);
                }
            }

            // add path to return list
            calculatedPaths.add(new Pair(childNodeId, paths.getPath(shortestPair.first)));
            start = shortestPair.first;

            searchList.remove(childNodeId);
        }

        // calculate path from last exhibit back to entrance/exit gate
        if (calculatedPaths.size() > 0) {
            String lastExhibitId = calculatedPaths.get(calculatedPaths.size()-1).second.getEndVertex();
            GraphPath<String, IdentifiedWeightedEdge> lastPath =
                    DijkstraShortestPath.findPathBetween(zooGraph, lastExhibitId, ENTRANCE);

            // add the last path to path list
            calculatedPaths.add(new Pair(ENTRANCE, lastPath));
        }

        return calculatedPaths;
    }

    // calculates paths with database + context for json
    public static List<PathInfo> findPath(Context context) {

        // Initialize vertex info map to be used for finding path
        String nodeInfo = context.getResources().getString(R.string.curr_node_info);

        // initialize some empty List<> calculatedPath

        // load user exhibits into some List<> searchList
        Set<String> searchList = ExhibitDatabase.getSingleton(context)
                .userExhibitListItemDao().getAllUserExhibits().stream()
                .map(n -> n.node_id)
                .collect(Collectors.toSet());

        // note: probably sort before the following

        // construct graph
        Graph<String, IdentifiedWeightedEdge> zooGraph = ZooData.loadZooGraphJSON(context, context.getResources().getString(R.string.curr_graph_info));

        List<Pair<String, GraphPath<String, IdentifiedWeightedEdge>>> calculatedPaths = findPath(searchList, zooGraph, ENTRANCE, context);


        PathItemDao pathItemDao = ExhibitDatabase.getSingleton(context).pathItemDao();
        pathItemDao.deletePathItems();

        for (int i = 0; i < calculatedPaths.size(); i++) {
            pathItemDao.insert(new PathItem(calculatedPaths.get(i).second.getEndVertex(),
//            pathItemDao.insert(new PathItem(calculatedPaths.get(i).first,
                    calculatedPaths.get(i).second.getEdgeList().stream()
                            .map(e -> e.getId()).collect(Collectors.toList()),
                    i));
        }

        List<PathInfo> paths = calculatedPaths.stream().map((path) ->
                new PathInfo(path.first, path.second)).collect(Collectors.toList());

        return paths;
    }

    // returns parent node if exists, otherwise returns child node
    // vertex name isnt always same as exhibit (multiple exhibits in one vertex)
    static String getVertexNameFromExhibit(String exhibit, Context context) {
        String currExhibit = exhibit;
        // Initialize vertex info map to be used for finding path
        String nodeInfo = context.getResources().getString(R.string.curr_node_info);
        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON(context, nodeInfo);
        String parentId = vInfo.get(exhibit).parent_id;

        if (parentId != null) {
            currExhibit = parentId;
        }

        return currExhibit;
    }

    public static GraphPath<String, IdentifiedWeightedEdge> findPathToFixedNext(Context context, String currLoc, String fixedNext) {
        Graph<String, IdentifiedWeightedEdge> zooGraph = ZooData.loadZooGraphJSON(context, context.getResources().getString(R.string.curr_graph_info));

        String fromVertex = getVertexNameFromExhibit(currLoc, context);
        String toVertex = getVertexNameFromExhibit(fixedNext, context);

        Log.d("<findPathToFixedNext call>", "currLoc: " + currLoc + " w/ fixedNext: " + fixedNext);
//        if (currLoc.equals(fixedNext)) {
//            return new GraphWalk<String, IdentifiedWeightedEdge>(zooGraph, currLoc, fixedNext, new ArrayList<>(), 0);
//        }
        return DijkstraShortestPath.findPathBetween(zooGraph, fromVertex, toVertex);
    }

    public static List<PathInfo> findPathGivenExcludedNodes
            (Context context, String currLoc, List<String> nodesToOmit) {
        Log.d("fpcen nodesToOmit", nodesToOmit.toString());
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
        Log.d("searchlist", searchList.toString());

        Graph<String, IdentifiedWeightedEdge> zooGraph = ZooData.loadZooGraphJSON(context, context.getResources().getString(R.string.curr_graph_info));

        List<Pair<String, GraphPath<String, IdentifiedWeightedEdge>>> calculatedPaths = findPath(searchList, zooGraph, start, context);


        PathItemDao pathItemDao = ExhibitDatabase.getSingleton(context).pathItemDao();
        pathItemDao.deletePathItems();

        for (int i = 0; i < calculatedPaths.size(); i++) {
            pathItemDao.insert(new PathItem(calculatedPaths.get(i).second.getEndVertex(),
                    calculatedPaths.get(i).second.getEdgeList().stream()
                            .map(e -> e.getId()).collect(Collectors.toList()),
                    i));
        }

        List<PathInfo> returnPathList = new ArrayList();
        Log.d("fpcen returnPathList", returnPathList.toString());

        for (Pair<String, GraphPath<String, IdentifiedWeightedEdge>> pair : calculatedPaths) {
            PathInfo pathInfo = new PathInfo(pair.first, pair.second);
            returnPathList.add(pathInfo);
        }

        return returnPathList;
    }

}