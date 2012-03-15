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
		checkPoints.add(new Tile(180, 230));
		checkPoints.add(new Tile(270, 230));

		Tile creepStart = new Tile(0, 100);
		Tile creepEnd = new Tile(270, 460);

		return new Route(creepStart, creepEnd, TileView.mTileSize, checkPoints);
	}

	public static WaveManager defaultWaves(TowerDefenseView towerDefenseView) {

		int level = 0;
		Route route = genericRoute();
		WaveManager wm = new WaveManager();
		
		Drawable genericImage = towerDefenseView.getResources().getDrawable(
				R.drawable.creeps);
		
		Drawable genericImage2 = towerDefenseView.getResources().getDrawable(
				R.drawable.creep1);
		
		Drawable genericImage3 = towerDefenseView.getResources().getDrawable(
				R.drawable.creep2);

		Drawable genericImage4 = towerDefenseView.getResources().getDrawable(
				R.drawable.creep3);
		
		Drawable genericImage5 = towerDefenseView.getResources().getDrawable(
				R.drawable.creep4);
		
		Drawable genericImage6 = towerDefenseView.getResources().getDrawable(
				R.drawable.creep5);
		
		Drawable genericImage7 = towerDefenseView.getResources().getDrawable(
				R.drawable.creep6);

		Drawable genericImage8 = towerDefenseView.getResources().getDrawable(
				R.drawable.creep7);
		
		Drawable genericImage9 = towerDefenseView.getResources().getDrawable(
				R.drawable.creep8);
		
		Wave wave = new Wave(GenericCreep.class, genericImage, route, 20, level);
		wm.addWave(wave);
		level++;

		wave = new Wave(GenericCreep.class, genericImage2, route, 20, level);
		wm.addWave(wave);
		level++;

		wave = new Wave(GenericCreep.class, genericImage3, route, 20, level);
		wm.addWave(wave);
		level++;
		
		wave = new Wave(GenericCreep.class, genericImage4, route, 20, level);
		wm.addWave(wave);
		level++;
		
		wave = new Wave(GenericCreep.class, genericImage5, route, 20, level);
		wm.addWave(wave);
		level++;
		
		wave = new Wave(GenericCreep.class, genericImage6, route, 20, level);
		wm.addWave(wave);
		level++;
		
		wave = new Wave(GenericCreep.class, genericImage7, route, 20, level);
		wm.addWave(wave);
		level++;
		
		wave = new Wave(GenericCreep.class, genericImage8, route, 20, level);
		wm.addWave(wave);
		level++;
		
		wave = new Wave(GenericCreep.class, genericImage9, route, 20, level);
		wm.addWave(wave);
		level++;
		
		return wm;
	}
}
