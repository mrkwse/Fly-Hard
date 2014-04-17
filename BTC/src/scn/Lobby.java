package scn;

import java.util.LinkedHashMap;

import net.NetworkManager;

import org.newdawn.slick.Color;

import scn.Game.DifficultySetting;
import btc.Main;
import cls.InputBox;
import lib.jog.graphics;
import lib.jog.input;
import lib.jog.audio.Sound;
import lib.ButtonText;

public class Lobby extends Scene {
	
	/** The time since the list of available players was last updated */
	private double timeSinceUpdate;

	private final int CREATE_BUTTON_W = 128;
	private final int CREATE_BUTTON_H = 32;
	private final int CREATE_BUTTON_X = 500;
	private final int CREATE_BUTTON_Y = 200;
	
	private InputBox inputBox;
	
	private ButtonText[] buttons;
	
	private ButtonText[] playerButtons;
	
	/** The map of available players */
	private LinkedHashMap<Integer, String> availablePlayers;
	
	
	protected Lobby() {
		super();
	}
	
	
	@Override
	public void start() {
		inputBox = new InputBox(Color.white, Color.red, 400, 500, 200, 30);
		
		buttons = new ButtonText[1];
		
		ButtonText.Action createGame = new ButtonText.Action() {
			@Override
			public void action() {
				NetworkManager.setPlayerName(inputBox.getText());
				Main.getNetworkManager().sendMessage("INIT");
			}
		};
		
		buttons[0] = new ButtonText("Create Game", createGame, CREATE_BUTTON_X, CREATE_BUTTON_Y,
				CREATE_BUTTON_W, CREATE_BUTTON_H);
	}

	@Override
	public void update(double timeDifference) {
		// Increment the time before the next data send
		timeSinceUpdate += timeDifference;

		// Update the map of available players approximately every two seconds
		if (timeSinceUpdate > 2) {
			// Update the list of players
			updateAvailablePlayers();

			// Reset the time
			timeSinceUpdate = 0;
		}
				
		inputBox.update(timeDifference);
	}
	
	/**
	 * Checks the server to get any updates to the map of available players.
	 */
	private void updateAvailablePlayers() {
		// Get the available opponents from the server
		String[] openConnections = NetworkManager
				.postMessage("GET_OPEN_CONNECTIONS").split(";");
		
		// Clear the map of available players
		availablePlayers = new LinkedHashMap<Integer, String>();
		
		// Format the open connections into a hashmap
		String[] currentEntry;
		for (int i = 0; i < openConnections.length; i++) {
			if (!openConnections[i].equals("NO CONNECTIONS")) {
				currentEntry = openConnections[i].split(":");

				switch (currentEntry.length) {
				case 1:
					availablePlayers.put(Integer.valueOf(currentEntry[0]),
							"ANON");
					break;
				case 2:
					availablePlayers.put(Integer.valueOf(currentEntry[0]),
							currentEntry[1]);
					break;
				}
			}
		}
		
		// Create a button for each player
		playerButtons = new ButtonText[availablePlayers.size()];
		
		// Get a list of client IDs
		Integer[] clientIDs = getAvailablePlayerIDs();
		
		int curY = 200;
		for (int i = 0; i < playerButtons.length; i++) {
			ButtonText.Action currentAction = createPlayerButtonAction(clientIDs[i]);
			
			playerButtons[i] = new ButtonText(availablePlayers.get(clientIDs[i]),
					currentAction, 200, curY, 100, 30);
			
			curY += 40;
		}
	}

	@Override
	public void draw() {
		graphics.setColour(255, 255, 255); // White

		inputBox.draw();

		graphics.rectangle(false, CREATE_BUTTON_X, CREATE_BUTTON_Y,
				CREATE_BUTTON_W, CREATE_BUTTON_H);

		if (buttons != null) {
			for (ButtonText b : buttons) {
				b.draw();
			}
		}

		if (playerButtons != null) {
			for (ButtonText b : playerButtons) {
				b.draw();
			}
		}
	}
	
	@Override
	public void mousePressed(int key, int x, int y) {}

	/**
	 * Causes a button to act if mouse released over it.
	 */
	@Override
	public void mouseReleased(int key, int x, int y) {
		if (buttons != null) {
			for (ButtonText b : buttons) {
				if (b.isMouseOver(x, y)) {
					b.act();
				}
			}
		}
		
		if (playerButtons != null) {
			for (ButtonText b : playerButtons) {
				if (b.isMouseOver(x, y)) {
					b.act();
				}
			}
		}
	}

	@Override
	public void keyPressed(int key) {}

	/**
	 * Quits back to title scene on escape button.
	 */
	@Override
	public void keyReleased(int key) {
		if (key == input.KEY_ESCAPE) {
			Main.closeScene();
		}
	}

	@Override
	public void close() {}

	@Override
	public void playSound(Sound sound) {}
	
	/**
	 * Selects a game to play.
	 * @param clientID - the ID of the client to connect to
	 */
	private void selectGame(int clientID) {
		NetworkManager.postMessage("JOIN:" + clientID);
		Main.setScene(MultiPlayerGame
				.createMultiPlayerGame(DifficultySetting.EASY));
	}
	
	/**
	 * Creates a new button action to connect to a player.
	 * @param id - the ID of the player which this button
	 * 			will connect to
	 * @return a button action which will cause the player to open
	 * 			a connection to the client with the specified ID
	 */
	private ButtonText.Action createPlayerButtonAction(final int id) {
		return new ButtonText.Action() {
			@Override
			public void action() {
				selectGame(id);
				System.out.println("Selected client: " + id);
			}
		};
	}
	
	/**
	 * Gets a list of all available player IDs from the
	 * available players map.
	 * @return a list of the IDs of all available players
	 */
	private Integer[] getAvailablePlayerIDs() {
		return availablePlayers.keySet()
				.toArray(new Integer[availablePlayers.size()]);
	}

}
