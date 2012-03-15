package com.game.towerdefense;

public class Pixel {
	
	public final int x;
	public final int y;
	
	public Pixel(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Pixel(float x, float y) {
		this.x = (int)x;
		this.y = (int)y;
	}
	
	public Tile toTile() {
		int tileX = (int) ((x*TileView.mTileSize)/TileView.mWidth);
		int tileY = (int) ((y*TileView.mTileSize)/TileView.mHeight);
		return TileMap.getTile(tileX, tileY);
	}
	
	public static Tile toTile(float x, float y) {
		int tileX = (int) (x/TileView.mTileSize);
		int tileY = (int) (y/TileView.mTileSize);
		return TileMap.getTile(tileX, tileY);
	}
}
