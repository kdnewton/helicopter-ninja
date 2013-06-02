package com.n3wt0n.HeliNinja;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class InputManager {

	protected Player player;
	
	private float maxYVel = 300;
	private float maxXVel = 100;
	
	private float rotation;
	private float maxRotation;

	private float xVel;

	public InputManager(Player player) {
		this.player = player;
	}
	
	public void init() {
		maxRotation = player.getMaxRotation();
	}

	public void update(GameContainer gc, int delta) throws SlickException {

		Input input = gc.getInput();

		player.preUpdate(delta);
		rotation = Math.abs(player.getRotation());
		
		xVel = (rotation/maxRotation)*maxXVel;

		if (input.isKeyPressed(Input.KEY_F)) {
			gc.setFullscreen(!gc.isFullscreen());
		}

		player.setMoving(false);

		// Then update player state based on key input
		if (input.isKeyPressed(Input.KEY_UP)) {
			player.applyForce(0, -maxYVel);
		}
		if (input.isKeyDown(Input.KEY_UP)) {
			player.applyForce(0, -maxYVel);
		}

		if (input.isKeyDown(Input.KEY_RIGHT)) {
			player.setFacingRight(true);
			if (!player.isOnGround()) {
				player.applyForce(xVel, 0);
				player.tiltRight();
			}
		} else if (input.isKeyDown(Input.KEY_LEFT)) {
			player.setFacingRight(false);
			if (!player.isOnGround()) {
				player.applyForce(-xVel, 0);
				player.tiltLeft();
			}
		} else {
			// reduce the xVel smoothly, until they are hovering.
			player.setVelocity(player.getVelX()*0.98f, player.getVelY());
			player.steadyTilt();
		}
	}
	
	public void setMaxYVel(float vel) {
		this.maxYVel = vel;
	}
	
	public void setMaxXVel(float vel) {
		this.maxXVel = vel;
	}
}
