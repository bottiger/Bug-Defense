package com.game.towerdefense.towers;

import java.util.ArrayList;

import com.game.towerdefense.Tile;
import com.game.towerdefense.creeps.Creep;

public abstract class AbstractTower implements Tower {

	private final int IMAGE_SIZE = 24;
	private final Tile position;

	private int damage;
	private int range;
	private int penetration;
	private int delay;

	private int cooldown = 0;

	private Creep lastTarget;

	public AbstractTower(Tile position, int damage, int delay, int range,
			int penetration) {
		this.position = position;
		this.damage = damage;
		this.range = range;
		this.penetration = penetration;
		this.delay = delay;
	}

	public int getRange() {
		return range;
	}

	public boolean inRange(int x, int y) {
		double distance = Math.sqrt(Math.pow(position.x - x, 2)
				+ Math.pow(position.y - y, 2));
		return distance <= range;
	}

	public Creep findTarget(ArrayList<Creep> creeps) {
		if ((lastTarget != null) && inRange(lastTarget.x(), lastTarget.y()))
			return lastTarget;
		else {
			for (Creep c : creeps) {
				if (inRange(c.x(), c.y())) {
					lastTarget = c;
					return c;
				}
			}
		}

		return null;
	}

	public void shoot(ArrayList<Creep> creeps) {
		if (cool()) {
			Creep target = findTarget(creeps);
			if (target != null)  {
				target.damage(damage, penetration);
				cooldown = delay;
				if (target.getHealth() <= 0) {
					lastTarget = null;
				}
			}
		}
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

	public Tile getPosition() {
		return position;
	}
	
	public int getLowerBound() {
		return this.position.getPixel().y + IMAGE_SIZE/2;
	}
	
	public int getUpperBound() {
		return this.position.getPixel().y - IMAGE_SIZE/2;
	}
	
	public int getLeftBound() {
		return this.position.getPixel().x - IMAGE_SIZE/2;
	}
	
	public int getRightBound() {
		return this.position.getPixel().x + IMAGE_SIZE/2;
	}

	private boolean cool() {
		if (cooldown > 0) {
			cooldown -= 1;
			return cooldown == 0;
		} else
			return true;
	}

}
