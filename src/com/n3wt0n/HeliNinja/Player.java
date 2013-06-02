package com.n3wt0n.HeliNinja;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import com.n3wt0n.G2DP.Entity;
import com.n3wt0n.G2DP.MapUtil;
import com.n3wt0n.G2DP.SoundWrapper;

public class Player extends Entity {

	private boolean isAlive = true;

	private MapUtil mapUtil;

	private Image initialImage;
	private Image image;
	private Image blade;
	private Image bladeSpinning;

	private Body bladeBody;

	private int offGroundTimer = 0;
	private int totalOffGroundTimer = 0;

	private Sound swoosh;
	private boolean loopingSound;

	private int MAX_Y_VEL = 100;

	@SuppressWarnings("unused")
	private String currentState = "Init State";

	private float maxRotate;
	private float rotation;
	private float rotationSpeed;

	public Player(World world, float x, float y, int width, int height,
			float mass, String name, SoundWrapper swrap) throws SlickException {
		super(world, x, y, width, height, mass, name, swrap);
		super.setFacingRight(true);

		swoosh = new Sound("audio/sfx/heliswoosh.ogg");
		loopingSound = false;

		blade = new Image("images/HeliBlade.png");
		bladeSpinning = new Image("images/BladeSpinning.png");
		bladeBody = new Body(new Box(bladeSpinning.getWidth() - 14,
				bladeSpinning.getHeight() - 8), 0.01f);

		maxRotate = 45f;

		rotationSpeed = 1.0f;

		setXY(x, y);
	}

	public void initImage(Image image) {
		super.setImage(image);
		initialImage = image;
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		image = initialImage;

		if (isFalling()) {
			currentState = "Falling";
		} else if (isOnGround()) {
			if (isMoving())
				currentState = "isMoving";
			else {
				currentState = "OnGround";
				image.setRotation(0);
			}
		}

		if (!isFacingRight()) {
			image = initialImage.getFlippedCopy(true, false);
		}

		if (isMoving() && isOnGround()) {
			currentState = "rotating -> " + rotation;
		}
		image.setRotation(rotation);
		image.drawCentered(getVisualX(), getVisualY());

		if (!isAlive()) {
			return;
		}

		if (this.isOnGround()) {
			blade.draw(getVisualX() - 5, getVisualY() - (image.getHeight() / 2));
		} else {
			bladeSpinning = bladeSpinning.getFlippedCopy(true, false);

			bladeSpinning.setCenterOfRotation(bladeSpinning.getWidth() / 2,
					(image.getHeight() / 2 + bladeSpinning.getHeight() / 2));
			bladeSpinning.setRotation(rotation);
			bladeSpinning.drawCentered(getVisualX(), getVisualY() - (image.getHeight() / 2));
		}
	}

	public void preUpdate(int delta) {
		if (!this.isOnGround()) {
			if (!loopingSound) {
				loopingSound = true;
				stopSound();
			}
		} else {
			if (loopingSound) {
				loopingSound = false;
				stopSound();
			}
		}
	}

	@Override
	public void update(int delta) throws SlickException {

		// update the flag for the actor being on the ground. The
		// physics engine will cause constant tiny bounces as the
		// the body tries to settle - so don't consider the body
		// to have left the ground until it's done so for some time
		boolean on = onGroundImpl(getBody());
		if (!on) {
			offGroundTimer += delta;
			totalOffGroundTimer += delta;
			if (offGroundTimer > 100) {
				super.setOnGround(false);
			}
		} else {
			offGroundTimer = 0;
			this.setVelocity(0, 0);
			super.setOnGround(true);
		}

		if (!isAlive() && !isOnGround()) {
			rotateUntilCrash();
			// this.body.setRotation(0);
			return;
		} else if (!isAlive() && isOnGround()) {
			// initialImage = bladeSpinning;
			// image = initialImage;
			return;
		}

		// keep velocity constant throughout the updates
		setVelocity(getVelX(), getVelY());
		// if we're standing on the ground negate gravity. This stops
		// some instability in physics
		getBody().setGravityEffected(!on);

		// clamp y
		if (getVelY() < -MAX_Y_VEL) {
			setVelocity(getVelX(), -MAX_Y_VEL);
		}

		if (!this.isOnGround()) {
			if (!loopingSound) {
				loopingSound = true;
				swoosh.loop();
			}
		} else {
			if (loopingSound) {
				loopingSound = false;
				stopSound();
			}
		}

		float theRot = (float) Math.toRadians(this.getRotation());
		getBody().setRotation(theRot);
		bladeBody.setRotation(theRot);
		float oldX = getX();
		float oldY = getY();
		float newX;
		float newY;

		try {
			AffineTransform transformer = AffineTransform.getRotateInstance(
					theRot, oldX, oldY);
			Point2D before = new Point2D.Double(oldX, oldY
					- (image.getHeight() / 2) - 2);
			Point2D after = new Point2D.Double();
			after = transformer.transform(before, after);

			newX = (float) after.getX();
			newY = (float) after.getY();
			bladeBody.setPosition(newX, newY);
		} catch (Exception e) {
			System.out.println(e);
		}

		if (bladeCollision()) {
			isAlive = false;
			stopSound();
		}
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setIsAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public void stopSound() {
		swoosh.stop();
	}

	public int getOffGroundTimer() {
		return offGroundTimer;
	}

	public int getTotalOffGroundTimer() {
		return totalOffGroundTimer;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public float getRotation() {
		return rotation;
	}

	public float getMaxRotation() {
		return this.maxRotate;
	}

	public void tiltLeft() {
		if (rotation > (maxRotate * -1)) {
			rotation -= rotationSpeed;
		}
	}

	public void tiltRight() {
		if (rotation < maxRotate) {
			rotation += rotationSpeed;
		}
	}

	public void steadyTilt() {
		if (rotation > 0) {
			rotation -= rotationSpeed;
		} else if (rotation < 0) {
			rotation += rotationSpeed;
		}
		return;
	}

	/**
	 * Get the Phys2D Body of the Entity.
	 * 
	 * @return Phys2D Body.
	 */
	public Body getBladeBody() {
		return bladeBody;
	}

	public void setMapUtil(MapUtil mapUtil) {
		this.mapUtil = mapUtil;
	}

	public boolean bladeCollision() {
		boolean boo = false;
		Box box = (Box) bladeBody.getShape();
		Vector2f[] pts = box.getPoints(bladeBody.getPosition(), bladeBody
				.getRotation());
		Vector2f v1 = pts[0];
		Vector2f v2 = pts[1];
		Vector2f v3 = pts[2];
		Vector2f v4 = pts[3];

		if (mapUtil.isTileTypeAt((int) v1.x, (int) v1.y, "PLATFORMS")
				|| mapUtil.isTileTypeAt((int) v2.x, (int) v2.y, "PLATFORMS")
				|| mapUtil.isTileTypeAt((int) v3.x, (int) v3.y, "PLATFORMS")
				|| mapUtil.isTileTypeAt((int) v4.x, (int) v4.y, "PLATFORMS")) {
			boo = true;
		}

		return boo;
	}

	public void rotateUntilCrash() {
		if (!isAlive) {
			rotation += 5;
		} else {
			rotation = 0;
		}
	}

}
