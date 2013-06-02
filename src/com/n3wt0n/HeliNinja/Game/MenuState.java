package com.n3wt0n.HeliNinja.Game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class MenuState extends BasicGameState {

	protected int stateID = -1;
	
	private int updateCounter = 0;
	
	private float minAlpha = 0.15f;
	private float alphaStep = minAlpha;

	private Image background;
	private Image overworld;
	private Image title;
	private Image largeBlade;
	private int titleX;
	
	private Image[] menuItem;
	private int[][] menuItemLocation;
	private boolean[] insideItem;
	private float[] transCount;

	private Image player;
	private Image spinningBlade;
	private int playerX, playerY;
	
	private Sound musicLoop;

	public MenuState(int stateID) {
		this.stateID = stateID;
	}

	@Override
	public int getID() {
		return stateID;
	}

	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		
		menuItem = new Image[3];
		insideItem = new boolean[menuItem.length];
		transCount = new float[menuItem.length];
		menuItemLocation = new int[menuItem.length][2];
		
		menuItemLocation[0][0] = 273; // set image's x location
		menuItemLocation[0][1] = 177; // set image's y location
		menuItemLocation[1][0] = 78; // set next image's x location
		menuItemLocation[1][1] = 371; // etc
		menuItemLocation[2][0] = 510;
		menuItemLocation[2][1] = 410;

		largeBlade = new Image("images/HeliBlade_large.png");
		
		overworld = new Image("images/background_01.png");
		overworld.setAlpha(0.10f);
		
		background = new Image("images/menu_background_01.png");
		title = new Image("images/menu_title.png");

		menuItem[0] = new Image("images/menu_start.png");
		menuItem[1] = new Image("images/menu_highscore.png");
		menuItem[2] = new Image("images/menu_end.png");
		
		for (int i = 0; i < menuItem.length; i++) {
			insideItem[i] = false;
			transCount[i] = minAlpha;
		}

		spinningBlade = new Image("images/BladeSpinning.png");
		player = new Image("images/HeliNinja.png");
		playerX = 85;
		playerY = 300;
		gc.setMouseCursor("images/blank_cursor.png",0,0);
		
		titleX = (gc.getWidth()-title.getWidth())/2;
		
		musicLoop = new Sound("audio/music/MujikAngel.ogg");
		musicLoop.loop();
	}

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		
		background.draw(0, 0);
		overworld.draw(0,0);
		title.draw(titleX,10);
		largeBlade.draw(20,85);
		
		for (int i = 0; i < menuItem.length; i++) {
			menuItem[i].draw(menuItemLocation[i][0], menuItemLocation[i][1]);
		}
		player.drawCentered(playerX, playerY);
		spinningBlade = spinningBlade.getFlippedCopy(true, false);
		spinningBlade.drawCentered(playerX, playerY-player.getHeight()+13);
		
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		
		if (updateCounter < 50) {
			updateCounter += delta;
			return;
		} else {
			updateCounter -= delta;
		}
		
		Input input = gc.getInput();

		int mouseX = input.getMouseX();
		int mouseY = input.getMouseY();
		playerX = mouseX;
		playerY = mouseY;

		for (int i = 0; i < menuItem.length; i++) {
			insideItem[i] = false;
			// Check to see if the mouse is hovering over the "start game" item
			if ((mouseX >= menuItemLocation[i][0] && mouseX <= menuItemLocation[i][0] + menuItem[i].getWidth())
					&& (mouseY >= menuItemLocation[i][1] && mouseY <= menuItemLocation[i][1] + menuItem[i].getHeight())) {
				insideItem[i] = true;
			}

			if (insideItem[i]) {
				if (transCount[i] < 1f) {
					transCount[i] += alphaStep;
				}
			} else {
				if (transCount[i] > minAlpha) {
					transCount[i] -= alphaStep;
				}
			}
			menuItem[i].setAlpha(transCount[i]);
		}

		// Start
		if (insideItem[0] && input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			this.startGame(sbg);
			System.out.println("This is where we'd normally start the game");
		}

		// Highscore
		if (insideItem[1] && input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			// this.startGame(sbg);
			System.out.println("This is where we'd normally start the game");
		}

		// Exit
		if (insideItem[2] && input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			musicLoop.stop();
			gc.exit();
		}

	}
	
	public void startGame(StateBasedGame sbg) {
		musicLoop.stop();
		sbg.enterState(TheGame.GAMEPLAYSTATE);
	}

}
