package com.n3wt0n.HeliNinja.Game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class TheGame extends StateBasedGame {

	public static final int MAINMENUSTATE = 0;
	public static final int GAMEPLAYSTATE = 1;

	public TheGame() {
		super("Helicopter Ninja");
	}

	public void initStatesList(GameContainer gameContainer)
			throws SlickException {

		gameContainer.setTargetFrameRate(60);
		gameContainer.setVSync(true);

		this.addState(new MenuState(MAINMENUSTATE));
		this.addState(new GameState(GAMEPLAYSTATE));
	}

	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new TheGame());
		app.setDisplayMode(640, 480, false);
		app.setShowFPS(true);
		app.start();
	}
}