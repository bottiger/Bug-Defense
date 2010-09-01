package com.game.towerdefense;

import java.util.HashMap;

public class TileMap {

	static private HashMap<Integer, Tile> tileList = new HashMap<Integer, Tile>();
	
	public static Tile getTile(int x, int y) {
		int lookupConstant = 10000;
		Tile tile = tileList.get(new Integer(x*lookupConstant+y));
		if (tile == null) {
			tile = new Tile(x,y);
			tileList.put(new Integer(x*lookupConstant+y), tile);
		}
	
		return tile;
	}
	
	//FIXME this method should be removed
	public static Tile getTile(int x, int y, float tileSize) {
		return getTile(x, y);
	}
	
	public static void clearTiles() {
		tileList = new HashMap<Integer, Tile>();
	}
}
