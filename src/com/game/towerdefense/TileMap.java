package com.game.towerdefense;

import java.util.HashMap;

public class TileMap {

	private TowerDefenseView towerDefenseView;
	private HashMap tileList = new HashMap<String, Tile>();
	
	public TileMap(TowerDefenseView v) {
		this.towerDefenseView = v;
	}
	
	Tile getTile(int x, int y) {
		Tile tile = (Tile) tileList.get(coordinateToString(x, y));
		if (tile == null)
			tile = new Tile(x,y, towerDefenseView);
	
		return tile;
	}
	
	private String coordinateToString(int x, int y) {
		return x + "+" + y;
	}
}
