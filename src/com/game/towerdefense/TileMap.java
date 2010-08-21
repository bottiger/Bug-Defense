package com.game.towerdefense;

import java.util.HashMap;

public class TileMap {

	static private HashMap<Integer, Tile> tileList = new HashMap<Integer, Tile>();
	
	static Tile getTile(int x, int y, float tileSize) {
		int lookupConstant = 10000;
		Tile tile = tileList.get(new Integer(x*lookupConstant+y));
		if (tile == null) {
			tile = new Tile(x,y);
			tileList.put(new Integer(x*lookupConstant+y), tile);
		}
	
		return tile;
	}
	
//	static private String coordinateToString(int x, int y) {
//		return x + "+" + y;
//	}
}
