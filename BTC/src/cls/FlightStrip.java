//    /|  /|  /|  /
//   / | / | / | /   In use by mwuk **BEWARE CHANGES**
//  /  |/  |/  |/
//
// V 0.1

package cls;

import lib.TextBox;
import lib.jog.graphics;
import lib.jog.input;                        // <= May not be required
import lib.jog.input.EventHandler;          // <= May not be required

// public class Strip extends lib.TextBox

public class FlightStrip implements EventHandler {
    private boolean isVisible;
    private cls.Aircraft currentAircraft;
    private double positionX, positionY, width, height;

    /**
     * Consructor for each altimeter
     * @param x the x coord to draw at
     * @param y tht y coord to draw at
     * @param w the width of the strip
     * @param h the height of the strip
     */
    public FlightStrip(double x, double y, double w, double h) {
        positionX = x;
        positionY = y;
        width = w;
        height = h;
        hide();
    }

      /**
       * Shows strip for selected aircraft (prototyping only)
       * (this will potentially become a method to call externally
       * to trigger instantiation of a strip)
       * @param aircraft The aircraft to display the strip of.
       */
    public void show(cls.Aircraft aircraft) {
        if(aircraft != null) {
            currentAircraft = aircraft;
            isVisible = true;
        }
    }


    public void hide() {
        currentAircraft = null;
        isVisible = false;
    }

    public void draw() {
        drawOutline();
        if(isVisible){
        drawFlightNumber();
//      drawAirline();
        }
    }

    private void drawOutline(){
        graphics.setColour(graphics.blue);
        graphics.rectangle(false, positionX, positionY, width, height);
    }

    private void drawFlightNumber(){
        graphics.print(currentAircraft.getName(), (positionX + (width/2)), (positionY + (height/2)));
    }

    @Override
    public void mousePressed(int key, int x, int y) {}

    @Override
    public void mouseReleased(int key, int mx, int my) {}

    @Override
    public void keyPressed(int key) {}

    @Override
    public void keyReleased(int key) {}


//    public void update(double dt)

}
