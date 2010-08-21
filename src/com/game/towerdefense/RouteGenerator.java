package com.game.towerdefense;

import java.util.ArrayList;
import android.view.View;

public class RouteGenerator {

	public static Route GenericRoute(float tileSize) {
		
		ArrayList<Tile> checkPoints = new ArrayList<Tile>();
		checkPoints.add(new Tile(100, 100));
		checkPoints.add(new Tile(100, 350));
		checkPoints.add(new Tile(180, 350));
		checkPoints.add(new Tile(180, 200));
		checkPoints.add(new Tile(300, 200));

		Tile creepStart = new Tile(0, 100);
		Tile creepEnd = new Tile(300, 400);
		
		return new Route(creepStart, creepEnd, tileSize, checkPoints);
	}
}
