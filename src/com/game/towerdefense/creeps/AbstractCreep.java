package com.game.towerdefense.creeps;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.game.towerdefense.*;

public abstract class AbstractCreep implements Creep {
	
	private final int IMAGE_SIZE = 24;
	
	private Drawable image;
	
	private int iniSpeed;
	private int iniHealth;
	private int iniArmour;
	private final int value;

	private int health;
	private int preDamageHealth;
	private int speed;
	private Route route;
	private int level = 1;
	private float levelHealthBonus = 1.3f;
	
	private int positionIndex = 0;
	private int lastPositionIndex;

	public AbstractCreep(int speed, int health, int armour, int value, Route route) {
		this.iniSpeed = speed;
		this.iniHealth = health;
		this.iniArmour = armour;

		this.value = value;
		this.speed = speed;
		this.health = health;
		this.route = route;
		this.lastPositionIndex = route.length()-1;
		
		this.preDamageHealth = health;

	}
	
	public AbstractCreep(int speed, int health, int armour, int value) {
		this.iniSpeed = speed;
		this.iniHealth = health;
		this.iniArmour = armour;

		this.value = value;
		this.speed = speed;
		this.health = health;
		
		this.preDamageHealth = health;
	}

	public boolean isLastPos() {
		return positionIndex == lastPositionIndex;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
		this.lastPositionIndex = route.length()-1;
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
	
	public void setLevel(int level) {
		this.level = level;
		this.health = (int)(this.health * Math.pow((double)this.levelHealthBonus, (double)level));
		this.iniHealth = this.health;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getValue() {
		return value;
	}

	public int getHealth() {
		return health;
	}
	
	public int getPreDamageHealth() {
		return preDamageHealth;
	}
	
	public int getHealthPercentage() {
		return (health * 100) / iniHealth;
	}

	public void setHealth(int healt) {
		this.health = healt;
	}
	
	public void preDamage(int damage, int penetration) {
		preDamageHealth -= this.calcDamage(damage, penetration);
	}
	
	public void damage(int damage, int penetration) {
		health -= this.calcDamage(damage, penetration);
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

	public Drawable getImage() {
		return image;
	}

	public void setImage(Drawable img) {
		image = img;

	}
	
	private int calcDamage(int damage, int penetration) {
		return damage;
	}

}
