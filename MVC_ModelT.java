import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.List;

import processing.core.PApplet;


/**
 * Implementation of a Vertex object.
 * Each vertex has a unique Id.  The first vertex created
 * has an Id of 0. 
 * Each vertex contains a list of edges that emanate from it.
 */
class Vertex implements Comparable<Vertex> {
	public final int Id;
	public ArrayList<Edge> adj;    // adjacency list for vertex
	public double minDistance = Double.POSITIVE_INFINITY;
	public Vertex predecessor;

	public Vertex(int i) {  Id = i; predecessor = null; adj = new ArrayList<Edge>(); }
	public String toString() {      return Id + " "; }
	public int compareTo(Vertex other) {
		return Double.compare(minDistance, other.minDistance);
	}
}
/**
 * Implementation of an edge.  An edge is directed and goes from
 * source vertex to dest vertex.  It has a real weight.
 */
class Edge {
	public final Vertex source;
	public final Vertex dest;
	public final double weight;

	public Edge(Vertex argSource, Vertex argTarget, double argWeight) {
		source = argSource;
		dest   = argTarget;
		weight = argWeight;
	}
}


/**
 * Model component of Model-View-Controller system. Implements the graph, holds
 * the vertices (numbered 0 to maxNoVertices) and keeps the edges in a boolean
 * adjacency matrix, that is maxNoVertices x maxNoVertices;
 * 
 * @author D. Thiebaut
 *
 */

public class MVC_ModelT  {
	private final static int maxNoVerticesRows = 50; // geometry
	private final static int maxNoVerticesCols = 50; // 50 x 50 grid = 2500 vertices
	// total
	private static int maxNoVertices = maxNoVerticesRows * maxNoVerticesCols;
	private boolean[] visited;
	//private boolean[][] adjMat = null; // adjacency matrix
	public static int probability100 = 35; // probability (as percentage) for an edge
	// (35 means 35%)

	private  MVC_ViewerT viewer = null;// reference to the viewer of the MVC
	// system
	private MVC_ControllerT controller = null;// reference to the controller of
	//Vertex[] vertices;
	public static ArrayList<Vertex> vertices = new ArrayList<Vertex>(maxNoVertices);

	static Vertex source;
	// the MVC system
	static Vertex target;
	static List<Vertex> path = new ArrayList<Vertex>();

	/**
	 * constructor. Nothing to do. The building of the network is done by
	 * initGraph().
	 */
	public MVC_ModelT() {

	}

	/**
	 * mutator. Sets the reference to the controller.
	 * 
	 * @param c
	 */
	public void setController(MVC_ControllerT c) {
		controller = c;
	}

	/**
	 * mutator. Sets the reference to the viewer.
	 * 
	 * @param v
	 */
	public void setViewer(MVC_ViewerT v) {
		viewer = v;
	}

	/**
	 * accessor
	 * 
	 * @return number of vertices in graph
	 */
	public int getNoVertices() {
		return maxNoVertices;
	}

	public int getNoCols() {
		return maxNoVerticesCols;
	}

	public int getNoRows() {
		return maxNoVerticesRows;
	}



	/**
	 * add an undirected edge between u and v.
	 * 
	 * @param v
	 * @param u
	 */

	//public void addEdge(int v, int u) {

	//}

	/**
	 * returns vertex at given row and col. Assumes row & col will always be
	 * valid.
	 * 
	 * @param row
	 * @param col
	 * @return the vertex at row, col
	 */
	public int vertexAtRowColA(int row, int col) {
		return col + row * maxNoVerticesCols;
	}
	public int vertexAtRowCol(int row, int col) {
		return row + col * maxNoVerticesCols;
	}
	/**
	 * Find the shortest path from source to the other reachable nodes.
	 * @param source
	 */
	public static void dijkstraPaths(int vertex) {
		
		source = vertices.get(vertex);
		source.minDistance = 0.;
		PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
		vertexQueue.add(source);

		while (!vertexQueue.isEmpty()) {
			Vertex u = vertexQueue.poll();

			// Visit each edge emanating from u
			for (Edge e : u.adj) {
				Vertex v = e.dest;
				double weight = e.weight;
				double distanceThroughU = u.minDistance + weight;
				if (distanceThroughU < v.minDistance) {
					vertexQueue.remove(v);
					v.minDistance = distanceThroughU;
					v.predecessor = u;
					vertexQueue.add(v);
				}
			}
		
        }
	}
	public ArrayList<Integer> adjList(int v){
		ArrayList<Integer> adj = new ArrayList<Integer>();
		Vertex u = vertices.get(v);

		for(Edge e : u.adj ){
			//System.out.println("edestid"+ e.dest.Id);
			adj.add(e.dest.Id);
		}
		//System.out.println("adj:  "+adj);
		return adj;
	}
	/**
	 * Return an ArrayList of vertices that go from source (see Dijkstra)
	 * to target.
	 * @param target the destination of the path.
	 * @return an arraylist of vertices
	 */
	public static List<Vertex> getShortestPathTo(Vertex target) {
		List<Vertex> path = new ArrayList<Vertex>();
		for (Vertex vertex = target; vertex != null; vertex = vertex.predecessor)
			path.add(vertex);
		Collections.reverse(path);
		//System.out.println(path);
		return path;
	}

	/**
	 * displays the shortest paths found from the start vertex to ALL
	 * reachable vertices.
	 * @param start
	 */
	public void displayShortestPaths(int Start) {

		dijkstraPaths(Start);
		for (Vertex v : vertices) {
			System.out.println("Distance to " + v + ": " + v.minDistance);

			List<Vertex> path = getShortestPathTo(v);
			System.out.println("Path: " + path);
		}
	}
	
	public void showSPath(int Target){
		int NvG = viewer.getvG();
		if(NvG!=-1 && NvG != Target){
		dijkstraPaths(NvG);
		//List<Vertex> path = new ArrayList<Vertex>();
		path = getShortestPathTo(vertices.get(Target));
		
		}
	}
	/**
	 * generate a random graph. The seed of the random number generator is set
	 * to a fixed number, so that the graph generated is always the same.
	 */
	public void initGraph() {
	
		path.clear();
		source=null;
		target = null;
		
		vertices.clear();
		
		Random random = new Random();
		random.setSeed(12345); // seed for random number generator

		for(int i =0; i<maxNoVerticesRows;i++){
			for(int c=0; c< maxNoVerticesCols; c++){
				int currentVertex = vertexAtRowColA(i,c);

				Vertex v = new Vertex(currentVertex);

				vertices.add(v);
			//	System.out.println("vertices: "+vertices);

			}
		}


		visited = new boolean[maxNoVertices]; // automatically initialized to false

		for (int r = 1; r < maxNoVerticesRows; r++) {
			for (int c = 1; c < maxNoVerticesCols; c++) {
				int currentVertex = vertexAtRowCol(r, c);
				int upVertex = vertexAtRowCol(r - 1, c);
				int leftVertex = vertexAtRowCol(r, c - 1);
				//System.out.print(" current "+currentVertex);
				//System.out.print("   upV "+upVertex);
				//System.out.println("  left "+leftVertex);

				// should we create an up connection?

				if (random.nextInt(100) <= probability100){
					//System.out.println("current"+currentVertex);
					//System.out.println("upV"+upVertex);
					vertices.get(currentVertex).adj.add( 
							new Edge(vertices.get(currentVertex),vertices.get(upVertex),1));
					vertices.get(upVertex).adj.add( 
							new Edge(vertices.get(upVertex), vertices.get(currentVertex),1));
				}


				// should we create a left connection?
				if (random.nextInt(100) <= probability100){
					vertices.get(currentVertex).adj.add( 
							new Edge(vertices.get(currentVertex),vertices.get(leftVertex),1));
					vertices.get(leftVertex).adj.add(
							new Edge(vertices.get(leftVertex), vertices.get(currentVertex),1));

				}



				//ArrayList<Edge> a = vertices.get(currentVertex).adj;
				//if(!a.isEmpty())
				//	System.out.println("verticesADJ of "+currentVertex+" :source "+ a.get(0).source +" dest "+  a.get(0).dest);
			}
		}
	}

	/**
	 * recursively visit vertex v and all the vertices u adjacent to v
	 * @param v
	 */


	public void DFS( int vertexId ) {
		Vertex v = vertices.get( vertexId );
		//System.out.println("vertexId: "+ vertexId+"  vertex: "+v);
		visited = new boolean[ vertices.size() ];
		//System.out.print( "DFS starting on " + vertexId + ": visiting " );
		recurseDFS(v);
		//System.out.println("DFSvid: " +v.Id +" DFSv: "+ v);
	}
	public void recurseDFS( Vertex k ) {
		//System.out.println("k: "+k);
		//Vertex u = vertices.get( k );
		visited[ k.Id ] = true;
		//System.out.println( "recurseDFS v: "+k.Id );
		for ( Edge e: k.adj ) {
			//System.out.println("edge source: "+ e.source+" dest: "+e.dest);
			Vertex j = e.dest;
			if ( ! visited[j.Id ] ) 
				recurseDFS( j );
		}
	}



	public boolean isVisited(int v) {

		return visited[v];
	}

}
