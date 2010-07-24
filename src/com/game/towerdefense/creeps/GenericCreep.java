package com.game.towerdefense.creeps;

import java.util.ArrayList;

import com.game.towerdefense.Coordinate;

public class GenericCreep extends AbstractCreep {

	public GenericCreep(int speed, int health, int armour, Coordinate position,
			Coordinate goal, ArrayList<Coordinate> checkPoints) {
		super(speed, health, armour, position, goal, checkPoints);
	}

}
