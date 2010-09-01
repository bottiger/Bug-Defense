package com.game.towerdefense.towers;

import com.game.towerdefense.Pixel;
import com.game.towerdefense.creeps.Creep;

public class Shot {

	private Tower shooter;
	private Creep target;
	private Pixel position;
	private int distPerIteration = 10;
	private int damage;
	private int penetration;
	private boolean hit = false;

	public Shot(Tower shooter, Creep target) {
		this.shooter = shooter;
		this.target = target;
		this.position = shooter.getPosition().getPixel();
		this.damage = shooter.getDamage();
		this.penetration = shooter.getPenetration();
	}

	public void move() {
		Pixel targetPosition = this.target.getPosition().getPixel();
		double dx = (this.position.x - targetPosition.x);
		double dy = (this.position.y - targetPosition.y);
		
		double dist = Math.sqrt(Math.pow(dx, 2.0) + Math.pow(dy, 2.0));
		if (dist < distPerIteration) {
			impact();
			return;
		}
		
		double theta = Math.atan2(dx, dy);
		double newX = this.position.x - distPerIteration * Math.sin(theta);
		double newY =  this.position.y - distPerIteration * Math.cos(theta);
		this.position = new Pixel((int)newX, (int)newY);
	}
	
	public Pixel getPosition() {
		return this.position;
	}
	
	public boolean isHit() {
		return this.hit;
	}
	
	public boolean isFinalShot() {
		return damage >= target.getHealth();
	}

	private void impact() {
		this.target.damage(this.damage, this.penetration);
		this.hit = true;
		return;
	}

}
