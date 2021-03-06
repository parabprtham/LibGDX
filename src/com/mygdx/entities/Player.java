package com.mygdx.entities;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.MyGdxGame;
import com.sun.javafx.scene.text.HitInfo;

public class Player extends SpaceObjects {

	private float[] flamex;
	private float[] flamey;

	private boolean left;
	private boolean right;
	private boolean up;

	private boolean hit;
	private boolean dead;

	private static float maxHitTime = 2;
	private float hitTimer;

	private Line2D.Float[] hitLines;
	private Point2D.Float[] hitLinesVector;

	private float maxSpeed;
	private float acceleration;
	private float deacceleration;
	private float acceleratingTime;
	private int maxbullet = 4;
	private ArrayList<Bullet> bullets;

	private long point;
	private int extraLives;
	private long requiredScore;

	public Player() {
		x = MyGdxGame.height / 2;
		y = MyGdxGame.width / 2;

		maxSpeed = 200;
		acceleration = 100;
		deacceleration = 10;

		shapex = new float[4];
		shapey = new float[4];
		flamex = new float[3];
		flamey = new float[3];

		radians = 3.1415f / 2;
		rotationalSpeed = 3;

		hit = false;
		dead = false;
		hitTimer = 0;

		point = 0;
		extraLives = 0;
		requiredScore = 10000;
	}

	public Player(ArrayList<Bullet> bulletList) {
		// TODO Auto-generated constructor stub
		x = MyGdxGame.height / 2;
		y = MyGdxGame.width / 2;

		maxSpeed = 300;
		acceleration = 200;
		deacceleration = 10;

		shapex = new float[4];
		shapey = new float[4];
		flamex = new float[3];
		flamey = new float[3];

		radians = 3.1415f / 2;
		rotationalSpeed = 3;

		this.bullets = bulletList;

	}

	private void setShape() {

		shapex[0] = x + MathUtils.cos(radians) * 8;
		shapey[0] = y + MathUtils.sin(radians) * 8;

		shapex[1] = x + MathUtils.cos(radians - 4 * 3.1415f / 5) * 8;
		shapey[1] = y + MathUtils.sin(radians - 4 * 3.1415f / 5) * 8;

		shapex[2] = x + MathUtils.cos(radians + 3.1415f) * 5;
		shapey[2] = y + MathUtils.sin(radians + 3.1415f) * 5;

		shapex[3] = x + MathUtils.cos(radians + 4 * 3.1415f / 5) * 8;
		shapey[3] = y + MathUtils.sin(radians + 4 * 3.1415f / 5) * 8;

	}

	private void setFlame() {
		flamex[0] = x + MathUtils.cos(radians - 5 * 3.1415f / 6) * 5;
		flamey[0] = y + MathUtils.sin(radians - 5 * 3.1415f / 6) * 5;

		flamex[1] = x + MathUtils.cos(radians - 3.1415f)
				* (6 + acceleratingTime * 50);
		flamey[1] = y + MathUtils.sin(radians - 3.1415f)
				* (6 + acceleratingTime * 50);

		flamex[2] = x + MathUtils.cos(radians + 5 * 3.1415f / 6) * 5;
		flamey[2] = y + MathUtils.sin(radians + 5 * 3.1415f / 6) * 5;

	}

	public void setLeft(boolean b) {
		left = b;
	}

	public void setRight(boolean b) {
		right = b;
	}

	public void setUp(boolean b) {
		up = b;
	}

	public void shoot() {

		if (bullets.size() == maxbullet)
			return;
		bullets.add(new Bullet(x, y, radians));
	}

	public long getScore() {
		return point;
	}

	public int getLives() {
		return extraLives;
	}

	public void lostLife() {
		extraLives--;
	}

	public void increamentScore(long l) {
		point += l;
	}

	public void update(float dt) {

		if (point >= requiredScore) {
			extraLives++;
			requiredScore += 10000;
		}

		if (hit) {
			hitTimer += dt;
			if (hitTimer > maxHitTime) {
				dead = true;
				hitTimer = 0;
			}

			for (int i = 0; i < hitLines.length; i++) {
				hitLines[i].setLine(hitLines[i].x1 + hitLinesVector[i].x * 10
						* dt, hitLines[i].y1 + hitLinesVector[i].y * 10 * dt,
						hitLines[i].x2 + hitLinesVector[i].x * 10 * dt,
						hitLines[i].y2 + hitLinesVector[i].y * 10 * dt);
			}
		}

		// turning
		if (left)
			radians += rotationalSpeed;
		else if (right)
			radians -= rotationalSpeed;

		// Acceleration
		if (up) {
			dx += MathUtils.cos(radians) * acceleration * dt;
			dy += MathUtils.sin(radians) * acceleration * dt;
			acceleratingTime += dt;
			if (acceleratingTime > 0.1f)
				acceleratingTime = 0;
		} else
			acceleratingTime = 0;

		// deaccleration
		float vec = (float) Math.sqrt(dx * dx + dy * dy);
		if (vec > 0) {
			dx -= (dx / vec) * deacceleration * dt;
			dy -= (dy / vec) * deacceleration * dt;
		}

		if (vec > maxSpeed) {
			dx = (dx / vec) * maxSpeed;
			dy = (dy / vec) * deacceleration;
		}
		// set position
		x += dx * dt;
		y += dy * dt;

		// set Shape
		setShape();
		// setflame
		if (up)
			setFlame();

		// screen wrap
		wrap();
	}

	public boolean isDead() {
		return dead;
	}

	public void reset() {
		x = MyGdxGame.height / 2;
		y = MyGdxGame.width / 2;
		setShape();
		hit = dead = false;
	}

	public void draw(ShapeRenderer sr) {
		sr.setColor(1, 1, 1, 1);
		sr.begin(ShapeType.Line);

		if (hit) {
			for (int i = 0; i < hitLines.length; i++) {
				sr.line(hitLines[i].x1, hitLines[i].y1, hitLines[i].x2,
						hitLines[i].y2);
			}
			sr.end();
			return;
		}

		for (int i = 0, j = shapex.length - 1; i < shapex.length; j = i++) {
			sr.line(shapex[i], shapey[i], shapex[j], shapey[j]);
		}

		if (up) {
			for (int i = 0, j = flamex.length - 1; i < flamex.length; j = i++) {
				sr.line(flamex[i], flamey[i], flamex[j], flamey[j]);
			}

		}

		sr.end();

	}

	public void hit() {
		// TODO Auto-generated method stub

		if (hit)
			return;

		hit = true;
		dx = dy = 0;
		left = right = up = false;

		hitLines = new Line2D.Float[4];
		for (int i = 0, j = hitLines.length - 1; i < hitLines.length; j = i++) {
			hitLines[i] = new Line2D.Float(shapex[i], shapey[i], shapex[j],
					shapey[j]);
		}

		hitLinesVector = new Point2D.Float[4];
		hitLinesVector[0] = new Point2D.Float(MathUtils.cos(radians + 1.5f),
				MathUtils.sin(radians + 1.5f));
		hitLinesVector[1] = new Point2D.Float(MathUtils.cos(radians - 1.5f),
				MathUtils.sin(radians - 1.5f));
		hitLinesVector[2] = new Point2D.Float(MathUtils.cos(radians + 2.8f),
				MathUtils.sin(radians + 2.8f));
		hitLinesVector[3] = new Point2D.Float(MathUtils.cos(radians - 2.8f),
				MathUtils.sin(radians - 2.8f));
	}

	public boolean isHit() {
		return hit;
	}
}
