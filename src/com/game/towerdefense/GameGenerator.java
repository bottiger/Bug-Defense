package com.game.towerdefense;

import java.util.ArrayList;

import com.game.towerdefense.creeps.*;
import android.graphics.drawable.Drawable;

public class GameGenerator {

	public static Route genericRoute() {

		ArrayList<Tile> checkPoints = new ArrayList<Tile>();
		checkPoints.add(new Tile(100, 100));
		checkPoints.add(new Tile(100, 350));
		checkPoints.add(new Tile(180, 350));
		checkPoints.add(new Tile(180, 200));
		checkPoints.add(new Tile(300, 200));

		Tile creepStart = new Tile(0, 100);
		Tile creepEnd = new Tile(300, 400);

		return new Route(creepStart, creepEnd, TileView.mTileSize, checkPoints);
	}

	public static WaveManager defaultWaves(TowerDefenseView towerDefenseView) {

		Route route = genericRoute();
		WaveManager wm = new WaveManager();
		
		Drawable genericImage = towerDefenseView.getResources().getDrawable(
				R.drawable.creeps);
		
		Drawable genericImage2 = towerDefenseView.getResources().getDrawable(
				R.drawable.creep1);
		
		Drawable genericImage3 = towerDefenseView.getResources().getDrawable(
				R.drawable.creep2);

		Wave wave = new Wave(GenericCreep.class, genericImage, route, 20);
		wm.addWave(wave);

		wave = new Wave(GenericCreep.class, genericImage2, route, 20);
		wm.addWave(wave);

		wave = new Wave(GenericCreep.class, genericImage3, route, 20);
		wm.addWave(wave);
		
		return wm;
	}
}
