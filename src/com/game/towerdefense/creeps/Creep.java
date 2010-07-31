package com.game.towerdefense.creeps;

import android.graphics.drawable.Drawable;

import com.game.towerdefense.Coordinate;
import com.game.towerdefense.Route;

public interface Creep {
	
	public int getArmour();
	
	public int getSpeed();
	
	public void setSpeed(int speed, int milliSeconds);
	
	public int getHealth();
	
	public int getHealthPercentage();
	
	public void setHealth(int healt);
	
	public void damage(int damage, int penetration);
	
	public Coordinate getPosition();
	
	public Route getRoute();

	public void setRoute(Route route);
	
	public boolean isLastPos();
	
	public void move();
	
	public int x();
	
	public int y();
	
	public int getImageID();
	
	public void setImage(Drawable image);
	
	public Drawable getImage();
}
