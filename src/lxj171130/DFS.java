/**
 * @author Leejia James
 *
 * Depth-first search (DFS)
 * 1. Implemented algorithm to find topological order of DAG using DFS
 * 2. Implemented algorithm to find topological order of DAG using the 
 * 	method of removing nodes with no incoming edges
 * 3. Implemented algorithm to find the number of connected components
 *  of a given undirected graph. Each node gets a cno.
 *
 * Ver 1.0: 2018/10/26 Implemented team task (1)
 * Ver 2.0: 2018/10/27 Implemented individual tasks (2 & 3)
 */

package lxj171130;

import rbk.Graph.Vertex;
import rbk.Graph;
import rbk.Graph.Edge;
import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Factory;
import rbk.Graph.Timer;

import java.io.File;
import java.util.List;
import java.util.Queue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

public class DFS extends GraphAlgorithm<DFS.DFSVertex> {
	enum Color 
	{ 
	    WHITE, BLACK, GREY; 
	}
	static int time;
	int topNum;
	LinkedList<Vertex> finishList;
	Queue<Vertex> zeroQueue;
	boolean notDAG;
	static int cno;
	DFS d2;
	
    public static class DFSVertex implements Factory {
	int cno;
	Color color;
	Vertex parent;
	int top;
	int dis;
	int fin;
	int in;
	public DFSVertex(Vertex u) {
        color = Color.WHITE;
        parent = null;
        top = 0;
	}
	public DFSVertex make(Vertex u) { return new DFSVertex(u); }
    }

    public DFS(Graph g) {
	super(g, new DFSVertex(null));
    }
    
    /**
     * Recursive algorithm to visit the nodes of a graph
     * @param Graph g
     * @return DFS
     */
    public static DFS depthFirstSearch(Graph g) {
    	DFS dfstemp = new DFS(g);
    	dfstemp.topNum = g.size();
    	dfstemp.finishList = new LinkedList<>();
		for(Vertex u: g) {
		    dfstemp.get(u).color = Color.WHITE;
		    dfstemp.get(u).parent = null;
		    dfstemp.get(u).top = 0;
		}
		time = 0;
		cno = 0;
		
    	for(Vertex u: g) {
    		if(dfstemp.get(u).color == Color.WHITE) {
				dfstemp.dfsVisit(u,++cno);
    		}
    	}
    	return dfstemp;
    }

    /**
     * Utility method for depthFirstSearch()
     * @param Vertex u
     */
    private void dfsVisit(Vertex u, int cno) { 
    	// u is visited by DFS
    	// Precondition: u is white
		get(u).color = Color.GREY;
		get(u).dis = ++time;
		get(u).cno = cno;
		
	    for(Edge e: g.incident(u)) {
			Vertex v = e.otherEnd(u);
			if(get(v).color == Color.WHITE) {
			    get(v).parent = u;
			    dfsVisit(v,cno);
			}
			else if(get(v).color == Color.GREY) {
				// cycle detected
				notDAG = true;
			}
	    }
		get(u).top = topNum--;
		finishList.addFirst(u);
		get(u).color = Color.BLACK;
		get(u).fin = ++time;
	}

	/**
	 * Member function to find topological order
	 * @return List of vertices in topological order if graph is DAG, 
	 * 			null otherwise
	 */
    public List<Vertex> topologicalOrder1() {
    	DFS d1 = depthFirstSearch(g);
    	if(d1.notDAG) {
    		return null;
    	}
    	else {
    		return d1.finishList;
    	}
    }

    /**
     * Finds the number of connected components of the graph g by running dfs
     * Enters the component number of each vertex u in u.cno
     * The graph g is available as a class field via GraphAlgorithm
     * @return the number of connected components
     */
    public int connectedComponents() {
    	d2 = depthFirstSearch(g);
    	return cno;
    }

    /**
     * Gets the component number of the vertex given
     * @param Vertex u
     * @return component number of Vertex u
     */
    public int cno(Vertex u) {
    	return d2.get(u).cno;
    }
    
    /**
     * Finding topological order of a DAG using DFS
     * @param Graph g
     * @return List of vertices in topological order, null if g is not a DAG
     */
    public static List<Vertex> topologicalOrder1(Graph g) {
		DFS d = new DFS(g);
		return d.topologicalOrder1();
    }

    /**
     * Finding topological order of a DAG using the method of removing 
     * nodes with no incoming edges
     * @param Graph g
     * @return List of vertices in topological order, null if g is not a DAG
     */
    public static List<Vertex> topologicalOrder2(Graph g) {
    	DFS dfstemp1 = new DFS(g);
    	dfstemp1.zeroQueue = new LinkedList<>();
    	dfstemp1.finishList = new LinkedList<>();
    	for(Vertex u: g) {
    		dfstemp1.get(u).in = u.inDegree();
    		if(dfstemp1.get(u).in == 0) {
    			dfstemp1.zeroQueue.add(u);
    		}
    		if(g.size() == dfstemp1.zeroQueue.size()) {
    			return null;
    		}
    	}
    	int count = 0;
    	while(!dfstemp1.zeroQueue.isEmpty()) {
    		Vertex u = dfstemp1.zeroQueue.remove();
    		dfstemp1.get(u).top = ++count;
    		dfstemp1.finishList.add(u);
    		for(Edge e: g.outEdges(u)) {
    			Vertex v = e.otherEnd(u);
    			dfstemp1.get(v).in--;
    			if(dfstemp1.get(v).in == 0) {
    				dfstemp1.zeroQueue.add(v);
    			}
    		}
    	}
    	if(g.size() == count) {
    		return dfstemp1.finishList;    				
    	}
    	else {
        	return null;    		
    	}
    }

    public static void main(String[] args) throws Exception {
	//String string = "7 8   1 2 2   1 3 3   2 4 5   3 4 4   4 5 1   5 1 7   6 7 1   7 6 1 0";
	String string = "7 6   1 2 2   1 3 3   2 4 5   3 4 4   4 5 1   6 7 1 0";
	Scanner in;
	// If there is a command line argument, use it as file from which
	// input is read, otherwise use input from string.
	in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(string);
	
	// Read graph from input
        //Graph g = Graph.readGraph(in);
        Graph g = Graph.readDirectedGraph(in);
	g.printGraph(false);
	
	List<Vertex> finishList = topologicalOrder1(g);	
	System.out.println("Topological order of DAG using DFS");
	if(finishList == null) {
		System.out.println("--Not a DAG--");
	}
	else {
		for(Vertex u: finishList) {
		    System.out.print(u + " ");
		}
	}
	System.out.println();
	System.out.println();
	
	List<Vertex> finishList1 = topologicalOrder2(g);	
	System.out.println("Topological order of DAG using Method2");
	if(finishList1 == null) {
		System.out.println("--Not a DAG--");
	}
	else {
		for(Vertex u: finishList1) {
		    System.out.print(u + " ");
		}
	}
	System.out.println();
	
	
	String string1 = "7 8   1 2 2   1 3 3   2 4 5   3 4 4   4 5 1   5 1 7   6 7 1   7 6 1 0";
	//String string1 = "7 6   1 2 2   1 3 3   2 4 5   3 4 4   4 5 1   6 7 1 0";
	Scanner in1;
	// If there is a command line argument, use it as file from which
	// input is read, otherwise use input from string.
	in1 = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(string1);
	
	// Read graph from input
        Graph g1 = Graph.readGraph(in1);
        //Graph g = Graph.readDirectedGraph(in1);
	g1.printGraph(false);

	System.out.println("Connected components of undirected graph");
	DFS d = new DFS(g1);
	int numcc = d.connectedComponents();
	System.out.println("Number of components: " + numcc + "\nu\tcno");
	for(Vertex u: g1) {
	    System.out.println(u + "\t" + d.cno(u));
	}
	System.out.println();
	
	
    }
}
