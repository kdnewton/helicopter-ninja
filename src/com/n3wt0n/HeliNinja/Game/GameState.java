package com.n3wt0n.HeliNinja.Game;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.World;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import com.n3wt0n.G2DP.Camera;
import com.n3wt0n.G2DP.MapUtil;
import com.n3wt0n.G2DP.SoundWrapper;
import com.n3wt0n.HeliNinja.InputManager;
import com.n3wt0n.HeliNinja.Player;

public class GameState extends BasicGameState {

	protected int stateID = -1;

	private World world;
	private Camera camera; // Our own camera. Not the G2DP camera.
	private TiledMap map;
	private Player player;
	private MapUtil mapUtil;

	private SoundWrapper swrapper;
	private InputManager iManager;

	private boolean levelComplete;
	private String currentLevel = "levels/level_01.tmx"; // the starting level
	private String nextLevel;
	private int[][] levelExits;
	private String[] levelForExit;
	private int playerPosX, playerPosY;

	// How often to read in the keyboard input.
	// Used to average the input so that input
	// is not processed faster on one PC compared
	// to another slower PC.
	private int controlDelta = 10;
	private int myDelta = 0; // Zero to start, changes in the update method
	
	// How long it takes to get through a particular level
	private int levelTimer;
	// How many tries it takes to get through a level w/out crashing
	private int attemptCounter;

	public GameState(int stateID) {
		this.stateID = stateID;
	}

	@Override
	public int getID() {
		return stateID;
	}

	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
		world = new World(new Vector2f(0, 250), 20);
		swrapper = new SoundWrapper();
		nextLevel = currentLevel;
		map = new TiledMap(currentLevel);
		
		mapUtil = new MapUtil(map, world);

		gc.setTargetFrameRate(60);
		gc.setVSync(true);

		player = new Player(world, 50, 50, new Image("images/HeliNinja.png")
				.getWidth(), new Image("images/HeliNinja.png").getHeight(), 1f,
				"BoB the Ninja", swrapper);
		player.initImage(new Image("images/HeliNinja.png"));
		player.flipBaseImage();

		world.add(player.getBody());
		
		mapUtil.buildMap();
		player.setMapUtil(mapUtil);
		
		camera = new Camera(gc, map, mapUtil, player);
		camera.setDrawBounds(true);

		iManager = new InputManager(player);
		iManager.init();
		
		levelTimer = 0;
		attemptCounter = 0;

		levelComplete = false;
		initLevelExits(map);
	}

	public void initLevelExits(TiledMap map) {
		int curIndex = 0;
		int exitCount = 0;

		for (int i = 0; i < map.getObjectGroupCount(); i++) {
			for (int j = 0; j < map.getObjectCount(i); j++) {
				if (map.getObjectName(i, j).equalsIgnoreCase("Goal")) {
					exitCount++;
				}
			}
		}

		levelExits = new int[exitCount][4];
		levelForExit = new String[exitCount];

		for (int i = 0; i < map.getObjectGroupCount(); i++) {
			for (int j = 0; j < map.getObjectCount(i); j++) {
				if (map.getObjectName(i, j).equalsIgnoreCase("Goal")) {
					levelExits[curIndex][0] = map.getObjectX(i, j);
					levelExits[curIndex][1] = map.getObjectY(i, j);
					levelExits[curIndex][2] = levelExits[curIndex][0] + map.getObjectWidth(i, j);
					levelExits[curIndex][3] = levelExits[curIndex][1] + map.getObjectHeight(i, j);
					// This next call is mandatory. The "levels/level_whatever.tmx" is the "fallback" return if a "to" is not found.
					levelForExit[curIndex++] = map.getObjectProperty(i, j, "to", "levels/level_01.tmx");
				}
			}
		}
	}

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		camera.render(gc, g);
		world.step();
//		camera.drawBody(g, player.getBody());
//		camera.drawBody(g, player.getBladeBody());
//		camera.drawPlatformBodies(g, world);
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {

		if (levelComplete) {
			loadMap(nextLevel, gc, sbg);
		}
		
		// Averages input to a set interval (controlDelta). For example,
		// a controlDelta of 1000 would limit input to once per second.
		myDelta += delta;
		if (myDelta < controlDelta) {
			return;
		} else {
			myDelta -= controlDelta;
		}

		player.preUpdate(delta);

		if (player.isAlive()) {
			iManager.update(gc, delta);
		} else if (player.isOnGround()) {
			player.setIsAlive(true);
			player.setX(camera.getPlayerStartX());
			player.setY(camera.getPlayerStartY());
			player.setRotation(0);
		}
		
		camera.update(gc, delta);

		playerPosX = (int) player.getX();
		playerPosY = (int) (player.getY() + (player.getHeight() / 2) - map
				.getTileHeight());

		for (int i = 0; i < levelExits.length; i++) {

			if ((playerPosX >= (int) levelExits[i][0] && playerPosX <= (int) levelExits[i][2])
					&& (playerPosY >= (int) levelExits[i][1] && (playerPosY+3) <= (int) levelExits[i][3])) {
				this.levelComplete = true;
				nextLevel = levelForExit[i];
				break;
			}
		}
	}

	public void reset(GameContainer gc, StateBasedGame sbg) throws SlickException {
		init(gc, sbg);
	}

	public void loadMap(String level, GameContainer gc, StateBasedGame sbg) throws SlickException {
		System.out.println ("Loading level: " + level);
		world.clear();
		map = null;
		mapUtil = null;
		camera = null;
		iManager = null;
//		nextLevel = null;
		
		System.out.println ("Loading new map: " + level);
		player.stopSound();
		map = new TiledMap(level);
		currentLevel = level;
//		reset(gc);
//		if (nextLevel == null) {
			map = new TiledMap(currentLevel);
//		}
		
		mapUtil = new MapUtil(map, world);

		world.add(player.getBody());
		
		mapUtil.buildMap();
		player.setMapUtil(mapUtil);
		camera = new Camera(gc, map, mapUtil, player);
		camera.setDrawBounds(true);

		iManager = new InputManager(player);
		iManager.init();
		
		levelTimer = 0;
		attemptCounter = 0;

		levelComplete = false;
		initLevelExits(map);
	}

}
