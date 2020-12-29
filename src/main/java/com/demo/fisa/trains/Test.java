package com.demo.fisa.trains;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.EppsteinKShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class Test {

	private static String NO_SUCH_ROUTE = "NO SUCH ROUTE";
	private static int MAX_PATHS = 1000;
	
	public static double getSumEdgeWeightPath(Graph<String, DefaultWeightedEdge> g, List<String> l) {
		double sum = 0;
		Iterator<String> i = l.iterator();
		String sourceVertex = i.next();
		while(i.hasNext()) {
			String targetVertex = i.next();
			if(g.containsEdge(sourceVertex, targetVertex)) {
		    	DefaultWeightedEdge e = g.getEdge(sourceVertex, targetVertex);
		    	sum += g.getEdgeWeight(e);
		    } else {
		    	return 0;
		    }
			sourceVertex = targetVertex;
		}
		return sum;
	}
	
	public static List<String> getTokens(String str, String token) {
	    List<String> tokens = new ArrayList<>();
	    StringTokenizer tokenizer = new StringTokenizer(str, token);
	    while (tokenizer.hasMoreElements()) {
	        tokens.add(tokenizer.nextToken().trim());
	    }
	    return tokens;
	}
	
	public static String getRouteDistance(Graph<String, DefaultWeightedEdge> g, String route) {		
		List<String> listVertex = getTokens(route, "-");
		double result = getSumEdgeWeightPath(g, listVertex);
		if(result > 0) 
			return String.valueOf(result);
		return NO_SUCH_ROUTE;
	}
	
	public static List<GraphPath<String, DefaultWeightedEdge>> getAllPahts(Graph<String, DefaultWeightedEdge> g, String sVertex, String tVertex, int maxPathLength) {
		AllDirectedPaths<String, DefaultWeightedEdge> adp = new AllDirectedPaths<>(g);
	    return adp.getAllPaths(sVertex, tVertex, false, maxPathLength);
	}
	
	public static long countEdgesWithMaxNumber(Graph<String, DefaultWeightedEdge> g, String sVertex, String tVertex, int maxPathLength) {
	    return getAllPahts(g, sVertex, tVertex, maxPathLength).stream()
		    	.filter(x -> x.getLength() > 0)
		    	.count();
	}

	public static long countEdgesWithExactNumber(Graph<String, DefaultWeightedEdge> g, String sVertex, String tVertex, int maxPathLength) {
		return getAllPahts(g, sVertex, tVertex, maxPathLength).stream()
				.filter(x -> x.getLength() == maxPathLength)
				.count();
	}
	
	public static double getEppsteinKShortestPath(Graph<String, DefaultWeightedEdge> g, String sVertex, String tVertex) {
		EppsteinKShortestPath<String, DefaultWeightedEdge> e = new EppsteinKShortestPath<>(g);
		GraphPath<String, DefaultWeightedEdge> path = 
				e.getPaths(sVertex, tVertex, 2).stream()
				.max(Comparator.comparingDouble(GraphPath<String, DefaultWeightedEdge>::getWeight))
				.get();
		return path.getWeight();
	}
	
	public static double getDijkstraShortestPath(Graph<String, DefaultWeightedEdge> g, String sVertex, String tVertex) {
		DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(g);
		return dijkstraShortestPath.getPathWeight(sVertex, tVertex);
	}
	
	public static double getLengthShortestPath(Graph<String, DefaultWeightedEdge> g, String sVertex, String tVertex) {
		if(sVertex.equals(tVertex)) {
			return getEppsteinKShortestPath(g, sVertex, tVertex); 
		}
		return getDijkstraShortestPath(g, sVertex, tVertex);
	}
	
	public static long countPahtWeightLessThan(Graph<String, DefaultWeightedEdge> g, String sVertex, String tVertex, double maxWeight) {
		EppsteinKShortestPath<String, DefaultWeightedEdge> e = new EppsteinKShortestPath<>(g);
		return  e.getPaths(sVertex, tVertex, MAX_PATHS)
					.stream()
					.filter(x -> x.getWeight() < maxWeight && x.getWeight() != 0)
					.count();
	}
	
	public static Graph<String, DefaultWeightedEdge> loadGraph(String input) {
		List<String> list = getTokens(input, ",");
		Set<String> set = new HashSet<>();
		for(String e : list) {
			set.add(String.valueOf(e.charAt(0)));
			set.add(String.valueOf(e.charAt(1)));
		}
		Graph<String, DefaultWeightedEdge> g = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		set.forEach(x -> g.addVertex(x));
		for(String x : list) {
			DefaultWeightedEdge dwe = g.addEdge(String.valueOf(x.charAt(0)), String.valueOf(x.charAt(1))); 
			g.setEdgeWeight(dwe, Character.getNumericValue(x.charAt(2)));
		}
		//System.out.println(g);
		return g;
	}
	
	public static void main(String[] args) {

	     //load graph
	     Graph<String, DefaultWeightedEdge> g = loadGraph("AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7");
	     
	     //1. The distance of the route A-B-C.
	     //2. The distance of the route A-D.
	     //3. The distance of the route A-D-C.
	     //4. The distance of the route A-E-B-C-D.
	     //5. The distance of the route A-E-D.
	     
	     System.out.println("Output #1: " + getRouteDistance(g, "A-B-C"));
	     System.out.println("Output #2: " + getRouteDistance(g, "A-D"));
	     System.out.println("Output #3: " + getRouteDistance(g, "A-D-C"));
	     System.out.println("Output #4: " + getRouteDistance(g, "A-E-B-C-D"));
	     System.out.println("Output #5: " + getRouteDistance(g, "A-E-D"));
	     
	   
	     //6. The number of trips starting at C and ending at C with a maximum of 3 stops.  
	     //In the sample data below, there are two such trips: C-D-C (2 stops). and C-E-B-C (3 stops).
	     
	     System.out.println("Output #6: " + countEdgesWithMaxNumber(g, "C", "C", 3));
	     //System.out.println(countEdgesWithMaxNumber(g, "B", "B", 3));
	     
	     //7. The number of trips starting at A and ending at C with exactly 4 stops.  
	     //In the sample data below, there are three such trips: A to C (via B,C,D); A to C (via D,C,D); and A to C (via D,E,B).
	     
	     System.out.println("Output #7: " + countEdgesWithExactNumber(g, "A", "C", 4));
	     
	     //8. The length of the shortest route (in terms of distance to travel) from A to C.
	     System.out.println("Output #8: " + getLengthShortestPath(g, "A", "C"));
	     
	     //9. The length of the shortest route (in terms of distance to travel) from B to B.
	     System.out.println("Output #9: " + getLengthShortestPath(g, "B", "B"));
	     
	     //10. The number of different routes from C to C with a distance of less than 30.  
	     //In the sample data, the trips are: CDC, CEBC, CEBCDC, CDCEBC, CDEBC, CEBCEBC, CEBCEBCEBC.

	     System.out.println("Output #10: " + countPahtWeightLessThan(g, "C", "C", 30));
	     //System.out.println(countPahtWeightLessThan(g, "B", "B", 30));	     
	}
}
