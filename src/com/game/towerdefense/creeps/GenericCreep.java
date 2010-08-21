package com.game.towerdefense.creeps;

import com.game.towerdefense.R;
import com.game.towerdefense.Route;

public class GenericCreep extends AbstractCreep {

	static private int speed = 2;
	static private int health = 100;
	static private int armour = 10;
	static private int value = 1;
	
	public GenericCreep() {
		super(speed, health, armour, value);
	}

	public GenericCreep(Route route) {
		super(speed, health, armour, value, route);
	}
	
	public GenericCreep(int speed, int health, int armour, Route route) {
		super(speed, health, armour, value, route);
	}

	@Override
	public int getImageID() {
		return R.drawable.creeps;
	}

}
