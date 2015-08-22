/**
 * MVC1_controller.  
 * The controller of a simple Model-View-Controller (MVC) system.
 * The MVC system displays a 50x50 network of vertices with left,
 * right, up, and down edges to their neighbors.
 * The controller is started by the viewer, which extends PApplet,
 * and which is the first object to become alive.  
 * The controller starts the model which initializes the graph.
 * Every graphical interaction in the viewer is passed on to the
 * controller. 
 * 
 *
 */
public class MVC_ControllerT {
	// links to the viewer and model.
	MVC_ViewerT viewer     = null;
	MVC_ModelT      model      = null;


	/**
	 * constructor.  
	 */
	public MVC_ControllerT() {
	}

	/**
	 * mutator
	 * @param v reference to the viewer
	 */
	public void setViewer( MVC_ViewerT v ) {
		viewer = v;
	}

	/**
	 * mutator
	 * @param m reference to the model
	 */
	public void setModel( MVC_ModelT m ) {
		model = m;
	}

	/**
	 * launched by the viewer, which passes a reference to itself in the
	 * process.  Creates a set of references between the model, the view, 
	 * and the controller.
	 * Initializes the graph held by the model (vertices and edges)
	 * @param v reference to the viewer.
	 */
	public void initAll( MVC_ViewerT v ) {
		// init viewer
		viewer = v;

		// create the model 
		model  = new MVC_ModelT();

		// create reference system between MVC components
		model.setController( this );
		viewer.setModel( model );
		model.setViewer( viewer );

		// create the graph of vertices and edges
		model.initGraph();
	}
	public void vGGet(int vertex){
		viewer.SetvG(vertex); 
	}
	/**
	 * called by viewer when the mouse is over a given vertex.
	 * decides what viewer should do, depending on different 
	 * modes of operation.
	 * @param vertex
	 */
	public void setVertexUnderMouse( int vertex ) {
		// display vertex name
		//viewer.displayVertexName( vertex );

		model.DFS( vertex );
		//model.dijkstraPaths(vertex);
		// display adjacency list
		//viewer.displayAdjacencyList( vertex );
		model.showSPath(vertex);
		viewer.drawSPath();

		//viewer.drawCircles(  vertex );
		//viewer.drawRedC(vertex);
		//viewer.drawCircles2(vertex);
	}

}
