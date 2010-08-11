package com.game.towerdefense.creeps;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.game.towerdefense.*;

public abstract class AbstractCreep implements Creep {
	
	private final int IMAGE_SIZE = 24;
	
	private final int iniSpeed;
	private final int iniHealth;
	private final int iniArmour;
	private final int lastPositionIndex;

	private int health;
	private int speed;
	private Route route;
	
	private int positionIndex = 0;

	public AbstractCreep(int speed, int health, int armour, Route route) {
		this.iniSpeed = speed;
		this.iniHealth = health;
		this.iniArmour = armour;

		this.speed = speed;
		this.health = health;
		this.route = route;
		this.lastPositionIndex = route.length()-1;

	}

	public boolean isLastPos() {
		return positionIndex == lastPositionIndex;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public int getArmour() {
		return iniArmour;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed, int milliSeconds) {
		this.speed = speed;
	}

	public int getHealth() {
		return health;
	}
	
	public int getHealthPercentage() {
		return (health * 100) / iniHealth;
	}

	public void setHealth(int healt) {
		this.health = healt;
	}
	
	public void damage(int damage, int penetration) {
		health -= damage;
	}

	public int x() {
		return route.getPosition(positionIndex).getPixel().x;
	}

	public int y() {
		return route.getPosition(positionIndex).getPixel().y;
	}
	
	public Tile getPosition() {
		return route.getPosition(positionIndex);
	}
	
	public int getLowerBound() {
		return this.y() + IMAGE_SIZE/2;
	}
	
	public int getUpperBound() {
		return this.y() - IMAGE_SIZE/2;
	}
	
	public int getLeftBound() {
		return this.x() - IMAGE_SIZE/2;
	}
	
	public int getRightBound() {
		return this.x() + IMAGE_SIZE/2;
	}
	
	public Tile getNextPos() {
		if (isLastPos()) {
			return getPosition();
		} else {
			return route.getPosition(positionIndex+1);
		}
	}
	
	public void move() {
		positionIndex++;
	}
	
	public abstract int getImageID();

}
