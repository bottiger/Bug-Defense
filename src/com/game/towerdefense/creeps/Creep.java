package com.game.towerdefense.creeps;

import com.game.towerdefense.Coordinate;
import com.game.towerdefense.Route;

public interface Creep {
	
	public int getArmour();
	
	public int getSpeed();
	
	public void setSpeed(int speed, int milliSeconds);
	
	public int getHealth();
	
	public void setHealth(int healt);
	
	public Coordinate getPosition();
	
	public Route getRoute();

	public void setRoute(Route route);
	
	public boolean isEndPos();
	
	public void move();
	
	public int x();
	
	public int y();
}
