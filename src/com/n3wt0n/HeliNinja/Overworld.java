package com.n3wt0n.HeliNinja;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class Overworld extends BasicGame {
	
	private int updateCounter = 0;
	private int updateLimit = 20;
	
	private float minAlpha = 0.25f;
	private float alphaStep = 0.07f;
	
	private Image overworld;
	
	private Image[] worlds;
	private Image[] darkWorlds;
	private int[][] worldLocation;
	private boolean[] insideWorld;
	private float[] transCount;
	private boolean[] completedWorld;

	private Image player;
	private Image spinningBlade;
	private int playerX, playerY;
	
	private MasterNinja masterNinja;
	private AcademySign academySign;

	public Overworld() {
		super("Helicopter Ninja Overworld");
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		
		worlds = new Image[3];
		darkWorlds = new Image[worlds.length];
		insideWorld = new boolean[worlds.length];
		transCount = new float[worlds.length];
		worldLocation = new int[worlds.length][2];
		completedWorld = new boolean[worlds.length];
		
		worldLocation[0][0] = 110; // set image's x location
		worldLocation[0][1] = 285; // set image's y location
		worldLocation[1][0] = 210; // set next image's x location
		worldLocation[1][1] = 250; // etc
		worldLocation[2][0] = 460;
		worldLocation[2][1] = 70;
		
		for (int i = 0; i < worlds.length; i++) {
			worlds[i] = new Image("images/overworld_0" + (i+1) + ".png");
			darkWorlds[i] = new Image("images/overworld_0" + (i+1) + ".png");
			insideWorld[i] = false;
			transCount[i] = minAlpha;
			completedWorld[i] = false;
		}

		initWorldImages();
		
		masterNinja = new MasterNinja();
		masterNinja.setLocation(180, 120);
		
		academySign = new AcademySign();
		academySign.setLocation(20,120);
		
		overworld = new Image("images/overworld_rock.png");

		spinningBlade = new Image("images/BladeSpinning.png");
		player = new Image("images/HeliNinja.png");
		playerX = 85;
		playerY = 300;
		gc.setMouseCursor("images/blank_cursor.png",0,0);
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		
		overworld.draw(0, 0);
		for (int i = 0; i < worlds.length; i++) {
			darkWorlds[i].draw(worldLocation[i][0], worldLocation[i][1]);
			worlds[i].draw(worldLocation[i][0], worldLocation[i][1]);
		}
		
		masterNinja.render();
		academySign.render();
		
		player.drawCentered(playerX, playerY-10);
		spinningBlade = spinningBlade.getFlippedCopy(true, false);
		spinningBlade.drawCentered(playerX, playerY-player.getHeight()+3);
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		
		if (updateCounter < updateLimit) {
			updateCounter += delta;
			return;
		} else {
			updateCounter -= updateLimit;
		}
		
		Input input = gc.getInput();

		int mouseX = input.getMouseX();
		int mouseY = input.getMouseY();
		playerX = mouseX;
		playerY = mouseY;

		for (int i = 0; i < worlds.length; i++) {
			insideWorld[i] = false;
			// Check to see if the mouse is hovering over the "start game" item
			if ((mouseX >= worldLocation[i][0] && mouseX <= worldLocation[i][0] + worlds[i].getWidth())
					&& (mouseY >= worldLocation[i][1] && mouseY <= worldLocation[i][1] + worlds[i].getHeight())) {
				insideWorld[i] = true;
			}

			if (insideWorld[i]) {
				if (transCount[i] < 1f) {
					transCount[i] += alphaStep;
				}
			} else {
				if (transCount[i] > minAlpha) {
					transCount[i] -= alphaStep;
				}
			}
			worlds[i].setAlpha(transCount[i]);
		}
		
		masterNinja.update(delta);
		academySign.update(delta);

	}

	public void initWorldImages() {
		for (int i = 0; i < worlds.length; i++) {
			Image image;

			image = worlds[i];
			image.setAlpha(minAlpha);

			image = darkWorlds[i];
			image.setColor(0, 0f, 0f, 0f, 100);
			image.setColor(1, 0f, 0f, 0f, 100);
			image.setColor(2, 0f, 0f, 0f, 100);
			image.setColor(3, 0f, 0f, 0f, 100);
		}
	}

	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new Overworld());
		app.setDisplayMode(640, 480, false);
		app.setShowFPS(false);
		app.start();
	}
	
	private class MasterNinja {
		
		private int x;
		private int y;
		
		private int curImage = 0;

		private Image[] imgMaster = new Image[2];
		private float rotation;
		private float rotationAngle = 15;

		private int wiggleDelta = 0;
		private int wiggleControl = 100;
		
		public MasterNinja() throws SlickException {
			imgMaster[0] = new Image("images/RocketHorseMasterNinja_01.png");
			imgMaster[1] = new Image("images/RocketHorseMasterNinja_02.png");
			for (int i = 0; i < imgMaster.length; i++) {
				imgMaster[i].setCenterOfRotation((imgMaster[i].getWidth()/2)-12, imgMaster[i].getHeight());
			}
			rotation = 0f;
		}
		
		public void update(int delta) {
			curImage++;
			curImage %= 2;
			
			float theValue;
			wiggleDelta += delta;
			if (wiggleDelta > wiggleControl) {
				wiggleDelta -= wiggleControl;
			}
			theValue = (float) (wiggleDelta / (wiggleControl * 0.5));
			theValue = (float) (theValue * Math.PI);
			theValue = (float) Math.sin(theValue);
			rotation = (float) (rotationAngle * theValue);
			
			imgMaster[curImage].setRotation(rotation);
		}
		
		public void render() {
			imgMaster[curImage].draw(x, y);
		}
		
		public void setLocation(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	private class AcademySign {
		
		private Image imgSign;
		private float x;
		private float y;
		
		private int floatCount = 0;
		private int floatFreq = 75;
		
		public AcademySign() throws SlickException {
			imgSign = new Image("images/overworld_00.png");
		}
		
		public void update(int delta) {
			floatCount++;
			if (floatCount < (floatFreq/2)) {
				setLocation(x,y+0.1f);
			} else {
				setLocation(x,y-0.1f);
			}
			floatCount %= floatFreq;
		}
		
		public void render() {
			imgSign.draw(x,y);
		}
		
		public void setLocation(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

}