package com.game.towerdefense.towers;

import java.util.ArrayList;

import com.game.towerdefense.Bank;
import com.game.towerdefense.Tile;
import com.game.towerdefense.TileView;
import com.game.towerdefense.creeps.Creep;

public abstract class AbstractTower implements Tower {

	private final int IMAGE_SIZE = 24;
	private final Tile position;
	private final int MAX_LEVEL = 4;

	private int size = 10;

	private int damage;
	private int range;
	private int penetration;
	private int delay;
	private int price;

	private int experience = 0;
	private int level = 0;

	private int cooldown = 0;

	private Creep lastTarget;

	public AbstractTower(Tile position, int damage, int delay, int range,
			int penetration, int price) {
		this.position = position;
		this.damage = damage;
		this.range = range;
		this.penetration = penetration;
		this.delay = delay;
		this.price = price;
	}

	public int getRange() {
		return range;
	}

	public int getPrice() {
		return price;
	}

	public boolean inRange(int x, int y) {
		double distance = Math.sqrt(Math.pow(position.x - x, 2)
				+ Math.pow(position.y - y, 2));
		return distance <= range;
	}

	public Creep findTarget(ArrayList<Creep> creeps) {
		if ((lastTarget != null)
				&& inRange(lastTarget.getPosition().x,
						lastTarget.getPosition().y)
				&& (lastTarget.getPreDamageHealth() > 0))
			return lastTarget;
		else {
			Creep bestTarget = null;
			for (Creep c : creeps) {
				if (inRange(c.getPosition().x, c.getPosition().y)) {
					lastTarget = c;
					if (bestTarget == null
							|| ((lastTarget.getHealth() < bestTarget
									.getHealth()) && (lastTarget
									.getPreDamageHealth() > 0)))
						bestTarget = lastTarget;
				}
			}
			return lastTarget = bestTarget;
		}
	}

	public Shot shoot(ArrayList<Creep> creeps) {
		Shot shot = null;
		if (cool()) {
			Creep target = findTarget(creeps);
			if (target != null) {
				// target.damage(damage, penetration);
				shot = new Shot(this, target);
				cooldown = delay;
				if (shot.isFinalShot()) {
					this.addExperience(lastTarget.getValue());
					lastTarget = null;
				}
			}
		}
		return shot;
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
		return (int) (this.position.getPixel().y + size * TileView.mTileSize);
	}

	public int getUpperBound() {
		return (int) (this.position.getPixel().y - size * TileView.mTileSize);
	}

	public int getLeftBound() {
		return (int) (this.position.getPixel().x - size * TileView.mTileSize);
	}

	public int getRightBound() {
		return (int) (this.position.getPixel().x + size * TileView.mTileSize);
	}

	public int getSize() {
		return size;
	}

	private boolean cool() {
		if (cooldown > 0) {
			cooldown -= 1;
			return cooldown == 0;
		} else
			return true;
	}

	public void addExperience(int xp) {
		this.experience += xp;
	}

	public int getExperience() {
		return this.experience;
	}

	public boolean upgrade() {
		if (level <= MAX_LEVEL && Bank.getAmount() > price) {
			level++;
			range *= 1.2f;
			damage *= 1.5f;
			delay *= 0.8f;
			Bank.decreaseAmount(price);
			return true;
		}
		
		return false;
	}

}
