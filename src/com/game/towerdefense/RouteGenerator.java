package com.game.towerdefense;

import java.util.ArrayList;
import android.view.View;

public class RouteGenerator {

	public static Route GenericRoute(TowerDefenseView v) {
		
		ArrayList<Tile> checkPoints = new ArrayList<Tile>();
		checkPoints.add(new Tile(100, 100, v));
		checkPoints.add(new Tile(100, 350, v));
		checkPoints.add(new Tile(180, 350, v));
		checkPoints.add(new Tile(180, 200, v));
		checkPoints.add(new Tile(300, 200, v));

		Tile creepStart = new Tile(0, 100, v);
		Tile creepEnd = new Tile(300, 400, v);
		
		return new Route(creepStart, creepEnd, v, checkPoints);
	}
}
