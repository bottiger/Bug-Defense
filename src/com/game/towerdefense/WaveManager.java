package com.game.towerdefense;

import java.util.LinkedList;
import java.util.List;

import android.graphics.drawable.Drawable;

import com.game.towerdefense.creeps.*;

public class WaveManager {

	private List<Wave> waveList = new LinkedList<Wave>();
	private int waveNumber = 0;
	private int waveDelay = 20000; // in milliseconds
	private long lastRelease;

	public WaveManager(TowerDefenseView towerDefenseView) {

		Route route = RouteGenerator.GenericRoute(TileView.mTileSize);
		Drawable genericImage = towerDefenseView.getResources().getDrawable(
				R.drawable.creeps);

		Wave wave = new Wave(GenericCreep.class, genericImage, route, 20);
		addWave(wave);

		wave = new Wave(GenericCreep.class, genericImage, route, 20);
		addWave(wave);

		wave = new Wave(GenericCreep.class, genericImage, route, 20);
		addWave(wave);
	}

	void addWave(Wave w) {
		waveList.add(w);
		waveNumber++;
	}

	Wave nextWave() {
		waveList.remove(0);
		return waveList.get(0);
	}

	Wave getNextWave() {
		return waveList.get(1);
	}

	Wave getCurrentWave() {
		Wave currentWave = waveList.get(0);
		if (currentWave.hasCreeps())
			lastRelease = System.currentTimeMillis();
		else if (lastRelease+waveDelay < System.currentTimeMillis())
				currentWave = nextWave();

		return currentWave;
	}

}
