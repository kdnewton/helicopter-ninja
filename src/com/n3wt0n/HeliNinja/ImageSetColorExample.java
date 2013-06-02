package com.n3wt0n.HeliNinja;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class ImageSetColorExample extends BasicGame {

	Image test;

	public ImageSetColorExample() {
		super("setColor Example");
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		test = new Image("images/greybox.png");
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		
		int x = 10, y = 10;

		g.setBackground(new Color(0.25f, 0.25f, 0.25f));

		drawImage(x, y, 0);
		g.drawString("0", (x+37), y);

		y += 37;
		drawImage(x, y, 1);
		g.drawString("1", (x+37), y);

		y += 37;
		drawImage(x, y, 2);
		g.drawString("2", (x+37), y);

		y += 37;
		drawImage(x, y, 3);
		g.drawString("3", (x+37), y);

		y += 37;
		test = new Image("images/greybox.png");
		test.setColor(0, 0, 0, 0, 0);
		test.setColor(1, 0, 0, 0, 0);
		test.draw(x, y);
		g.drawString("0,1", (x+37), y);

		x += (37*3);
		test = new Image("images/greybox.png");
		test.setColor(0, 0, 0, 0, 0);
		test.setColor(2, 0, 0, 0, 0);
		test.draw(x, y);
		g.drawString("0,2", (x+37), y);

		x += (37*3);
		test = new Image("images/greybox.png");
		test.setColor(0, 0, 0, 0, 0);
		test.setColor(3, 0, 0, 0, 0);
		test.draw(x, y);
		g.drawString("0,3", (x+37), y);

		x = 10;
		y += 37;
		test = new Image("images/greybox.png");
		test.setColor(1, 0, 0, 0, 0);
		test.setColor(2, 0, 0, 0, 0);
		test.draw(x, y);
		g.drawString("1,2", (x+37), y);

		x += (37*3);
		test = new Image("images/greybox.png");
		test.setColor(1, 0, 0, 0, 0);
		test.setColor(3, 0, 0, 0, 0);
		test.draw(x, y);
		g.drawString("1,3", (x+37), y);

		x = 10;
		y += 37;
		test = new Image("images/greybox.png");
		test.setColor(2, 0, 0, 0, 0);
		test.setColor(3, 0, 0, 0, 0);
		test.draw(x, y);
		g.drawString("2,3", (x+37), y);

		x = 10;
		y += 37;
		test = new Image("images/greybox.png");
		test.setColor(0, 0, 0, 0, 0);
		test.setColor(1, 0, 0, 0, 0);
		test.setColor(2, 0, 0, 0, 0);
		test.draw(x, y);
		g.drawString("0,1,2", (x+37), y);

		x += (37*3);
		test = new Image("images/greybox.png");
		test.setColor(0, 0, 0, 0, 0);
		test.setColor(1, 0, 0, 0, 0);
		test.setColor(3, 0, 0, 0, 0);
		test.draw(x, y);
		g.drawString("0,1,3", (x+37), y);

		x += (37*3);
		test = new Image("images/greybox.png");
		test.setColor(0, 0, 0, 0, 0);
		test.setColor(2, 0, 0, 0, 0);
		test.setColor(3, 0, 0, 0, 0);
		test.draw(x, y);
		g.drawString("0,2,3", (x+37), y);

		x = 10;
		y += 37;
		test = new Image("images/greybox.png");
		test.setColor(1, 0, 0, 0, 0);
		test.setColor(2, 0, 0, 0, 0);
		test.setColor(3, 0, 0, 0, 0);
		test.draw(x, y);
		g.drawString("1,2,3", (x+37), y);
	}

	public void drawImage(int x, int y, int corner) throws SlickException {
		test = new Image("images/greybox.png");
		test.setColor(corner, 0, 0, 0, 0);
		test.draw(x, y);
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {

	}

	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new ImageSetColorExample());
		app.setDisplayMode(640, 480, false);
		app.setShowFPS(false);
		app.start();
	}

}