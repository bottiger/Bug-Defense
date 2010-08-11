package com.game.towerdefense;

public class OldTile {
	
	private final int x;
	private final int y;
	
	private boolean blocked = false;
	
	public OldTile(int x, int y) {
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
