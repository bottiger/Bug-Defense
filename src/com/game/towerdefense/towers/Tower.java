package com.game.towerdefense.towers;

import com.game.towerdefense.Coordinate;

public interface Tower {
	
	public int getRange();

	public void setRange(int range);
	
	public int getDamage();

	public void setDamage(int damage);

	public int getPenetration();

	public void setPenetration(int penetration);

	public Coordinate[] getPosition();
}
