package com.game.towerdefense.towers;

import java.util.ArrayList;

import com.game.towerdefense.Coordinate;
import com.game.towerdefense.creeps.Creep;

public abstract class AbstractTower implements Tower {

	private final Coordinate position;

	private int damage;
	private int range;
	private int penetration;
	private int delay;

	private int cooldown = 0;

	private Creep lastTarget;

	public AbstractTower(Coordinate position, int damage, int delay, int range,
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

	public Coordinate getPosition() {
		return position;
	}

	private boolean cool() {
		if (cooldown > 0) {
			cooldown -= 1;
			return cooldown == 0;
		} else
			return true;
	}

}
