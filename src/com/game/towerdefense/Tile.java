package com.game.towerdefense;

public class Tile {
	
	private final int x;
	private final int y;
	
	private boolean blocked = false;
	
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public boolean isBlocked() {
		return this.blocked;
	}

}
