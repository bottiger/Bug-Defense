package com.game.towerdefense.towers;

import com.game.towerdefense.Coordinate;

public abstract class AbstractTower implements Tower {
	
	private final Coordinate[] position;
	
	private int damage;
	private int range;
	private int penetration;
	
	public AbstractTower(Coordinate[] position, int damage, int range, int penetration) {
		this.position = position;
		this.damage = damage;
		this.range = range;
		this.penetration = penetration;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getPenetration() {
		return penetration;
	}

	public void setPenetration(int penetration) {
		this.penetration = penetration;
	}

	public Coordinate[] getPosition() {
		return position;
	}

}
