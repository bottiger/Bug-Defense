package com.game.towerdefense.creeps;

import java.util.ArrayList;

import com.game.towerdefense.*;

public abstract class AbstractCreep implements Creep {

	private final int iniSpeed;
	private final int iniHealth;
	private final int iniArmour;

	private int health;
	private int speed;
	private Route route;

	public AbstractCreep(int speed, int health, int armour, Coordinate start,
			Coordinate goal, ArrayList<Coordinate> checkPoints) {
		this.iniSpeed = speed;
		this.iniHealth = health;
		this.iniArmour = armour;

		this.speed = speed;
		this.health = health;
		this.route = new Route(start, goal, checkPoints);

	}

	public void move() {
		route.move();
	}

	public boolean isEndPos() {
		return route.isEndPos();
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public Coordinate getPosition() {
		return route.getCurrentPos();
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

	public void setHealth(int healt) {
		this.health = healt;
	}

	public int x() {
		return route.getCurrentPos().x;
	}

	public int y() {
		return route.getCurrentPos().y;
	}

}
