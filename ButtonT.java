import processing.core.PApplet;

/**
 * A simple class for implementing a square button box.  If the button
 * is clicked, a cross is drawn in the button, otherwise it is 
 * left empty.
 * The button has a status, depending on the last time it was clicked
 * by the mouse.  Every time it is clicked, it switches state.
 */
public class ButtonT {
        private PApplet parent; // link to applet, so the button can display itself
        private int x;                  // x,y coordinates of top left corner
        private int y;
        private int w;                  // width (default 20)
        private int h;                  // height (default 20)
        boolean ONOFF;                  // true = ON, false = OFF
        String caption;                 // the text next to the button
        
        /**
         * constructor
         * @param p a reference to the PApplet, so that it can 
         * @param xx the x coordinate of the top-left corner
         * @param yy the y coordinate of the top-left corner
         * @param s  the name to print next to the button
         */
        ButtonT( PApplet p, int xx, int yy, String s ) {
                parent  = p;
                x               = xx;
                y               = yy;
                w               = 20;
                h               = 20;
                ONOFF   = false;
                caption = s;
        }
        
        public void setONOFF( boolean b )       { ONOFF = b;            }
        public boolean isON( )                          { return ONOFF;         }
        public boolean isOFF( )                         { return !ONOFF;        }
        public void switchState()                       { ONOFF = ! ONOFF;  }
        
        /**
         * draw the button on the applet.
         */
        public void draw() {
                parent.fill( 255, 255, 255 );           // white background
                parent.stroke( 0, 0, 0 );                       // black outline
                parent.strokeWeight( 2 );                       // line width
                parent.rect( x, y, w, h );
                if ( ONOFF ) {
                		parent.fill(98,246,255);
                		parent.rect(x, y, w, h);
                        parent.line( x,  y,  x+w,  y+h );
                        parent.line( x+w, y, x, y+h );
                        
                }
                parent.fill( 0, 0, 0 );                         // black text
                parent.textAlign( parent.LEFT );
                parent.text( caption, x + w + 5, y + h/2 );
        }
        
        public boolean containsMouse() {
                return ( parent.mouseX >= x && parent.mouseX <= x+w 
                                && parent.mouseY >= y && parent.mouseY <= y+h );
        }
}
