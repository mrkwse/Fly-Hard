//    /|  /|  /|  /
//   / | / | / | /   In use by mwuk **BEWARE CHANGES**
//  /  |/  |/  |/
//
// V 0.9

package cls;

import java.io.Serializable;

import btc.Main;
import scn.Game;
import lib.jog.graphics;
import lib.jog.input;
import lib.jog.window;

public class FlightStrip implements Serializable {

	/** Serialisation ID */
	private static final long serialVersionUID = -7542014798949722639L;
	
	/** The default width */
	private static final int STANDARD_WIDTH = 160;
	
	/** The default height */
	private static final int STANDARD_HEIGHT = 60;
	
	/** The default separation */
	private static final int SEPARATION = 10;

	/** Whether the flight strip should be drawn or not */
	private boolean isVisible;
	
	/** Whether the flight strip is active or not */
	private boolean isActive;

	/** The aircraft which the flight strip is linked to */
    private Aircraft aircraft;

    /** The flight strip's vertical position */
    private double verticalPos;

    /** The flight strip's width */
    private double width;

    /** The flight strip's height */
    private double height;
    
    /** The flight strip's vertical position */
    private double xOffset;
    
    /** The flight strip's vertical position */
    private double yOffset;


    /**
     * Constructor for flight strips.
     * @param aircraft - the linked aircraft
     */
    public FlightStrip(Aircraft aircraft) {
    	this.isVisible = true;
    	this.isActive = false;
    	this.aircraft = aircraft;
    	this.verticalPos = getNextSlot();
    	this.width = STANDARD_WIDTH;
    	this.height = STANDARD_HEIGHT;
    	this.xOffset = 0;
    	this.yOffset = 0;
    }
    
    /**
     * Constructor for flight strips.
     * @param width - the width of the strip
     * @param height - the height of the strip
     * @param aircraft - the linked aircraft
     */
    public FlightStrip(double x, double width, double height,
    		Aircraft aircraft) {
    	this.isVisible = true;
    	this.isActive = false;
    	this.aircraft = aircraft;
    	this.verticalPos = getNextSlot();
    	this.width = width;
    	this.height = height;
    	this.xOffset = Double.NaN;
    	this.yOffset = Double.NaN;
    }
    
    /**
     * Constructor for flight strips.
     * @param flightStrip - the flight strip to copy
     */
    private FlightStrip(FlightStrip flightStrip) {
    	isVisible = flightStrip.isVisible;
        aircraft = (flightStrip.aircraft != null)
        		? flightStrip.aircraft.clone() : null;
        verticalPos = flightStrip.verticalPos;
        width = flightStrip.width;
        height = flightStrip.height;
    }
    
    
    /**
     * Updates the flight strip.
     */
    public void update(double dt) {
    	this.verticalPos = getNextSlot();
    	
    	// If the mouse is hovering over the flight strip
    	if (isMouseOver()) {
    		isActive = true;
    	} else {
    		isActive = false;
    	}
    }
    
    /**
     * Draws the flight strip.
     * @param xOffset - the horizontal offset from the window's left edge
     * @param yOffset - the vertical offset from the window's top edge
     */
    public void draw(double xOffset, double yOffset) {
    	this.xOffset = xOffset;
    	this.yOffset = yOffset;
    	
    	if (isVisible) {
    		graphics.setFont(Main.flightstripFont);
    		drawOutline();
    		drawFlightNumber();
    		drawAirline();
    		drawAltitude();
    		drawRoute();
    		drawStatus();
    		graphics.setFont(Main.standardFont);
    		
    		if (isActive) {
    			graphics.setViewport(Game.getXOffset(), Game.getYOffset(),
    					window.width() - (2 * Game.getXOffset()),
    					window.height() - (2 * Game.getYOffset()));
    			aircraft.drawFlightPath();
    			graphics.setViewport();
    		}
    	}
    }

    private void drawOutline() {
    	// TODO mouseover/click highlight (plane/strip sync)
        graphics.setColour(graphics.blue);
        graphics.rectangle(true, xOffset, yOffset + verticalPos,
        		width, height);
        graphics.setColour(graphics.white);
        graphics.rectangle(true, xOffset + 2, yOffset + verticalPos + 2,
        		40, height - 4);
    }

    private void drawFlightNumber() {
    	graphics.setColour(graphics.black);
    	graphics.print(aircraft.getName().substring(0,2),
    			(xOffset + 2), (yOffset + verticalPos + 2));
    	graphics.print(aircraft.getName().substring(2,5),
    			(xOffset + 2), (yOffset + verticalPos + 30));
    }

    private void drawAirline() {
    	graphics.setColour(graphics.white);
        graphics.print(aircraft.getAirline(),
        		(xOffset + 2 + 40), (yOffset + verticalPos + 2));

    }

    private void drawAltitude() {
        graphics.print(String.format("%,d", (int) aircraft.getPosition().getZ()) + "ft",
        		(xOffset + (width/2)), ((yOffset + verticalPos + height) - 24));
    }
    
    private void drawRoute() {
    	String routeText = aircraft.getFlightPlan().getOriginName().substring(0, 3)
    			+ " TO "
    			+ aircraft.getFlightPlan().getDestinationName().substring(0, 3);
    	
    	graphics.print(routeText,
    			(xOffset + (width/2) - 20), (yOffset + verticalPos + height - 36));
    }
    
    private void drawStatus() {
    	if (aircraft.isInDanger()) {
    		graphics.setColour(graphics.red);
    		graphics.rectangle(true, (xOffset + 42),
    				(yOffset + verticalPos + height - 14),
    				116, 12);
    		graphics.setColour(graphics.white);
            graphics.printCentred("SHIIIIIIIIT", (xOffset + 100),
            		(yOffset + verticalPos + height - 14),
            		1, 1);

    	} else {
    		graphics.setColour(graphics.green);
    		graphics.rectangle(true, (xOffset + 42),
    				(yOffset + verticalPos + height - 14),
    				116, 12);
    		graphics.setColour(graphics.white);
            graphics.printCentred("AWW YISS", (xOffset + 100),
            		(yOffset + verticalPos + height - 14),
            		1, 1);
    	}
    }
    
    
    /**
     * Handles mouse press events.
     * @param key - the mouse key which was pressed
     * @param x - the x position of the mouse
     * @param y - the y position of the mouse
     */
    public void mousePressed(int key, int x, int y) {
    	if (isMouseOver()) {
    		Game.getInstance().getPlayer().setSelectedAircraft(aircraft);
    	}
    }
    
    /**
     * Handles mouse release events.
     * <p>
     * Not currently in use
     * </p>
     * @param key - the mouse key which was pressed
     * @param x - the x position of the mouse
     * @param y - the y position of the mouse
     */
    public void mouseReleased(int key, int mx, int my) {}
    
    /**
     * Checks if the mouse is over the flight strip.
     * @return <code>true</code> if the mouse is over the flight strip,
     * 			otherwise <code>false</code>
     */
    private boolean isMouseOver() {
    	if (xOffset != Double.NaN || yOffset != Double.NaN) {
    		return input.isMouseInRect((int) xOffset,
    				(int) (verticalPos + yOffset),
    				(int) width, (int) height);
    	} else {
    		return false;
    	}
    }
    
    
    /**
     * Gets the aircraft connected to the flight strip.
     * @return the aircraft connected to the flight strip
     */
    public Aircraft getAircraft() {
    	return aircraft;
    }
    
    /**
     * Gets the top of the next available slot for a flight strip.
     * @return the top of the next available slot for a flight strip
     */
    private double getNextSlot() {
    	double nextSlot = 0;
    	
    	if (Game.getInstance() != null
    			&& Game.getInstance().getPlayer() != null
    			&& Game.getInstance().getPlayer().getFlightStrips() != null) {
    		for (FlightStrip fs : Game.getInstance()
    				.getPlayer().getFlightStrips()) {
    			if (fs.isVisible) {
    				nextSlot += fs.height + SEPARATION;
    			}
    		}
    	}
    	
    	return nextSlot;
    }
    
    /**
     * Shows strip for selected aircraft (prototyping only).
     * <p>
     * This will potentially become a method to call externally
     * to trigger instantiation of a strip.
     * </p>
     */
	public void show() {
		isVisible = true;
	}

	/**
	 * Stops the flight strip from being drawn.
	 */
    public void hide() {
        isVisible = false;
    }
    
    
    /**
	 * Clones the flight strip.
	 */
	public FlightStrip clone() {
		return new FlightStrip(this);
	}

}
