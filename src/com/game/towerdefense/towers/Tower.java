package com.game.towerdefense.towers;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import com.game.towerdefense.Tile;
import com.game.towerdefense.creeps.Creep;

public interface Tower {
	
	public int getRange();
	
	public int getPrice();

	public void setRange(int range);
	
	public int getDamage();

	public void setDamage(int damage);

	public int getPenetration();

	public void setPenetration(int penetration);
	
	public boolean inRange(int x, int y);

	public Tile getPosition();
	
	public int getLowerBound();
	
	public int getUpperBound();
	
	public int getLeftBound();
	
	public int getRightBound();
	
	public void shoot(ArrayList<Creep> creep);

	void setImage(Drawable img);

	Drawable getImage();

	int getImageID();
	
	public int getSize();

	public void addExperience(int xp);
	
	public int getExperience();
}
