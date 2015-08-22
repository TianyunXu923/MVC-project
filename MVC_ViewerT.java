import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PFont;
import controlP5.*;


/**
 * The viewer class of a simple Model-View-Controller (MVC) system.
 * The MVC system displays a 50x50 network of vertices with left,
 * right, up, and down edges to their neighbors.
 * This class is the main class, as it extends PApplet, which will 
 * automatically call setup() on start-up, and will repeatedly call
 * draw(), 30 times a second.  
 * Constantly redraws the network of vertices and edges held by the 
 * model, 30 times a second.  
 * Passes special conditions to the controller for decision.  For
 * example, when the mouse is over a vertex, or when the mouse
 * is clicked somewhere on the canvas of the applet.
 * 
 *
 */
public class MVC_ViewerT extends PApplet {
	MVC_ControllerT         controller = null;
	MVC_ModelT              model      = null;
	public  int             WIDTH      = 800; // best generic width
	public  int                     HEIGHT     = 800; // best generic height
	private final int       DELTAX     = 10;  // no pixels between vertices
	private final int       DELTAY     = 10;  // no pixels between vertices
	private int             MINDIST    = 5;   // min dist for vertex detected 
	private int             vertexUnderMouse = -1;
	// as under mouse pointer
	private ButtonT 		button1;
	private ButtonT      button2;
	private ButtonT      button3;
	private ButtonT      button4;
	public  boolean clicked = false;
	public static int vG =-1;
	List<Vertex> path = new ArrayList<Vertex>();
	//public static int vK1=0;
	//public static int vK2=1;
	//public static int vP =-1;
	String textName = "density";
	ControlP5 controlP5;



	/**
	 * mutator: sets a reference to the model.
	 * @param m reference to the model
	 */
	public void setModel( MVC_ModelT m ) {
		model = m;
	}

	/**
	 * helper function.  Given a vertex number, returns its position
	 * (x, y) on applet canvas. Vertex 0 will be at (DELTAX, DELTAY).
	 * @param vertex the vertex to position
	 * @param noRows the number of rows in the network
	 * @param noCols the number of columns in the network
	 * @return an int array.  xy[0] is x, xy[1] is y.  In pixels.
	 */
	private int[] getXY( int vertex, int noRows, int noCols ) {
		int[] xy = new int[2];
		xy[0] = DELTAX + (int) ( vertex / noRows ) * DELTAX;
		xy[1] = DELTAY + ( vertex % noRows ) * DELTAY;
		return xy;
	}

	/**
	 * computes the number of vertex located at location (row, col).
	 * @param row
	 * @param col
	 * @return the number (int) of the vertex.
	 */
	private int vertexAtRowCol( int row, int col ) {
		int v = row + col * model.getNoRows();
		//int v = col + row * model.getNoRows();
		if ( v < 0 || v > model.getNoVertices() )
			return -1;
		return v;
	}

	/**
	 * the entry point for the MVC system.  The PApplet will automatically
	 * call setup() first.  This is the place that initializes everything
	 * up, including controller and model.  Gets the controller to link
	 * the 3 systems together.  The controller will initialize the model with
	 * the graph.
	 */
	public void setup() {
		//--- create model and controller, create reference --- 
		//--- system between all three.                                     ---
		controller = new MVC_ControllerT( );
		controller.initAll( this );

		//--- make window fit model graph exactly        ---
		//--- add blank area of 30 pixels high at bottom ---
		//DELTAX = round( WIDTH / (model.getNoCols() + 1 ) );
		//DELTAY = round( HEIGHT / (model.getNoRows() + 1 ) );
		WIDTH = DELTAX * (model.getNoCols() + 1 );
		HEIGHT = 30 + DELTAY * (model.getNoRows() + 1 );

		//--- set Applet geometry ---
		size( WIDTH, HEIGHT );
		smooth();

		//--- create the button ---
		button1 = new ButtonT( this, 10, HEIGHT-25, "Magic" );
		button2 = new ButtonT(this, 90, HEIGHT-25, "Highlight\nConnected");
		button3 = new ButtonT(this, 190, HEIGHT-25,"Path");
		button4 = new ButtonT(this, 380, HEIGHT-25, "Info*~*");

		PFont font = createFont("arial",15);

		controlP5 = new ControlP5(this);

		int y = 20;
		int spacing = 60;

		controlP5.addTextfield("Density")
		.setPosition(260,HEIGHT-25)
		.setSize(50,20)
		.setFont(font)
		.setFocus(true)
		.setColor(color(255,255,255))
		;
		y += spacing;


		// textFont(font);

	}
	public void controlEvent(ControlEvent theEvent) {

		if(theEvent.isAssignableFrom(Textfield.class)) {
			String percent = theEvent.getStringValue();
			int p = Integer.parseInt(percent);
			model.probability100 = p;
			//println("controlEvent: accessing a string from controller '"
			//      +theEvent.getName()+"': "
			//    +theEvent.getStringValue()
			//  );
			model.initGraph();
		}
	}

	/**
	 * return the vertex that is within MINDIST pixels of the current mouse location.
	 * @return the vertex number, or -1 if none found.
	 */
	public int closestVertexToMouse() {
		int row = round( (mouseY-DELTAY) / DELTAY );
		int col = round( (mouseX-DELTAX) / DELTAX );
		int v = vertexAtRowCol( row, col );
		if ( v == -1 ) 
			return -1;
		int[] xy = getXY( v, model.getNoRows(), model.getNoCols() );
		if ( dist( xy[0], xy[1], mouseX, mouseY ) < MINDIST ) 
			return v;
		return -1;
	}

	/**
	 * draw() is called automatically 30 times a second by mechanism inside
	 * the PApplet that is hidden from the programmer.  The role of draw() is
	 * to erase the screen (too fast for the user to notice) and redraw the 
	 * graph with vertices and edges, and whatever text needs to be displayed
	 * on the canvas.
	 * Detects if the mouse pointer is over a vertex and if so, calls the 
	 * controller to decide what should be done in this case.
	 */
	public void draw() {

		// erase window, make background white
		background( 255,255,255 );
		//background(0,0,0);
		fill( 0, 0, 0 );   
		textSize(12);// black text
		textAlign( this.LEFT );
		text( "Input",315,HEIGHT-18 );
		text("Density:)", 315, HEIGHT-8);

		// draw button
		button1.draw();
		button2.draw();
		button3.draw();
		button4.draw();
		// get geometry of grid
		int V = model.getNoVertices();
		int noRows = model.getNoRows();
		int noCols = model.getNoCols();

		// if mouse just over a vertex, tell controller
		// so that specific action can be taken.
		vertexUnderMouse = closestVertexToMouse();
		if ( vertexUnderMouse != -1 )
			controller.setVertexUnderMouse( vertexUnderMouse );

		if(vG!=-1 ){
			int[] xy3 = getXY(vG, model.getNoRows(),model.getNoCols());

			fill(255,0,0);
			stroke(255,0,0);
			ellipse(xy3[0],xy3[1],11,11);
		}

		if(button2.isON()){
			// draw larger colored circles for the vertices visited by the last DFS
			fill( 255, 204, 0 ); // make vertices black

			//strokeWeight( 2 );       // make line width 2 pixels
			stroke(68, 98,219);  //orange outline
			//stroke(random(255),random(255),random(255));
			for ( int v = 0; v < V; v++ ) {
				if(! model.isVisited(v))
					continue;

				// get coordinates of Vertex v
				int[] xy = getXY( v, noRows, noCols );

				//if(model.visited[v]){
				//fill(0,255,0);
				//stroke(255,0,0);

				ellipse(xy[0],xy[1],6,6);

			}
			if(button3.isON()){


			}
			// draw vertices and edges

			fill(random(255),random(255),random(255));   // fill vertices random color 
			stroke(0,0,0);
			//strokeWeight(2);  // make line width 2 pixels
		}
		// get adjacency list for each vertex and display edges

		for ( int v = 0; v < V; v++ ) {
			// get coordinates of Vertex v
			int[] xy = getXY( v, noRows, noCols );
			// draw black disk for Vertex v
			//System.out.println(xy[0]+""+" "+xy[1]+"");
			strokeWeight(2);
			fill(random(255),random(255),random(255));
			stroke(0,0,0);
			ellipse( xy[0], xy[1], 4, 4 );

			//get adjacency list for each vertex and display
			if (button1.isON() ){

				for ( int u:  model.adjList( v ) ) { 
					int[] xy2 = getXY( u, noRows, noCols );

					line( xy[0], xy[1], xy2[0], xy2[1]);
					stroke(random(255),random(255),random(255));
				}


			}
			// click button2: get adjacency list for each vertex and display edges
			//if(button2.isON() ){
			if(button4.isON()){
				//fill(23, 66, 235,8);
				
				fill(88, 55, 89,2);
				rect(105, 90, 300,300, 20);
				fill( 234, 193, 23 );            
				textSize(15);// black text
				textAlign( this.LEFT );
				text( "Project Made By: Tianyun Xu (Maria)",120,120 );
				text("Submitted Time: Dec 19/ 2014 :)", 120, 150);
				text("Instructor: Dominique Thiebaut.", 120,180);
				textSize(12);
				text("* Hightlight Button will highlight connected", 120, 210);
				text("components when the mouse touches the  ", 120, 230);
				text("relative vertex. ", 120, 250);
				text("* Path Button will show you the shortest ", 120, 270);
				text("way from the vertex you clicked to the", 120, 290);
				text("vertex the mouse touches.", 120, 310);
				text("* Input integer(0~100) to change the density.", 120, 330);
				text("* Click on Magic if you want to have fun!", 120, 350);
				textSize(15);
				text("            Enjoy! ",120,380);
				fill(255,0,0,10);
				triangle(300,360,270,380,330,380);
				fill(52, 125, 101,10);
				triangle(300,380,250,400,350,400);
				fill(126, 53, 23,10);
				rect(290,400,20,20);
				
				
			}

			for ( int u:  model.adjList( v ) ) { 

				//System.out.println(model.adjList( v ));
				int[] xy2 = getXY( u, noRows, noCols );
				//System.out.println(xy[0]+" "+ xy[1]+"  "+xy2[0]+"  "+ xy2[1]);
				//fill(255,255,255);

				line( xy[0], xy[1], xy2[0], xy2[1] );
				//	}                               
			}

			vertexUnderMouse = closestVertexToMouse();
			if ( vertexUnderMouse != -1 )
				controller.setVertexUnderMouse( vertexUnderMouse ); 
		}}


	/**
	 * displays the vertex number next to the circle representing it.
	 * This is typically done whenever the mouse pointer is over a vertex.
	 * @param vertexNo
	 */

	public void displayVertexName(int vertexNo ) {
		// get position of vertex
		int[] xy = getXY( vertexNo, model.getNoRows(), model.getNoCols() );
		// set text color to red 
		fill( 255, 0, 0 );  
		// display text
		text( " "+vertexNo, xy[0], xy[1] );
	}

	public void displayAdjacencyList( int vertexNo ) {
		String s = "";
		for ( int u:  model.adjList( vertexNo ) ) 
			s += u + ", ";
		// remove last ", " part of the string
		s = s.trim();
		if ( s.length() > 0 )
			s = s.substring( 0, s.length()-1 );
		text( s, 100, HEIGHT-25 );
	}
	public void drawCircles( int vertexNo ) {
		int[] xy = getXY( vertexNo, model.getNoRows(), model.getNoCols() );
		int radius = frameCount % 15;
		fill(255,255,0);
		ellipse( xy[0], xy[1], 2*radius, 2*radius );
	}


	public void drawSPath(){
		if(button3.isON()){		
			//System.out.println("pathsize: "+model.path.size());
			if(model.path.size() >1){
				//System.out.println("path: " + path);
				for(int i=0; i<model.path.size()-1;i++){
					Vertex u = model.path.get(i);
					Vertex q = model.path.get(i+1);

					int[] xy4 = getXY(u.Id, model.getNoRows(), model.getNoCols());
					int[] xy5 = getXY(q.Id, model.getNoRows(), model.getNoCols() );
					//System.out.println("xy4: "+xy4[0]+", "+xy4[1]);
					//System.out.println("xy5: "+ xy5[0]+", "+xy5[1]);
					//ellipse(xy4[0],xy4[1],6,6);
					//ellipse(xy5[0],xy5[1],6,6);
					strokeWeight(4);
					stroke(255,255,0);
					line(xy4[0],xy4[1],xy5[0],xy5[1]);
					fill(255,255,0);

				}
			}}
	}


	public int getvG(){
		return vG;
	}

	/**
	 * this is called automatically by the Applet if the user left-clicks the mouse
	 * on the canvas of the applet.  The location of the click can be found in mouseX
	 * and mouseY.
	 */
	public void mouseClicked() {


		System.out.println( "Mouse clicked at "+mouseX + ", " + mouseY );
		if ( button1.containsMouse() ) {
			button1.switchState();
		}
		else if(button2.containsMouse()){
			button2.switchState();
		}
		else if(button3.containsMouse()){
			button3.switchState();	
		}
		else if(button4.containsMouse()){
			button4.switchState();
		}

		else{
			//if(vG!=-1){
			int Ck = closestVertexToMouse();

			controller.vGGet(Ck);
		}
	}



	public void SetvG(int vertex){
		vG = vertex;
		for(int i=0;i<model.vertices.size();i++){
			model.vertices.get(i).predecessor =null;
			model.vertices.get(i).minDistance = Double.POSITIVE_INFINITY;
		}
		model.path.clear();
		model.source=null;
		model.target = null;

	}

	static public void main(String args[]) {
		PApplet.main( "MVC_ViewerT" );
	}
}

