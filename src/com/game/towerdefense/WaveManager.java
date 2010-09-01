package com.game.towerdefense;

import java.util.LinkedList;
import java.util.List;

import android.graphics.drawable.Drawable;

import com.game.towerdefense.creeps.*;

public class WaveManager {

	private List<Wave> waveList = new LinkedList<Wave>();
	private int waveNumber = 1;
	private int waveAmount = 0;
	private int waveDelay = 20000; // in milliseconds
	private long lastRelease;

	void addWave(Wave w) {
		waveList.add(w);
		waveAmount++;
	}
	
	Wave nextWave() {
		waveList.remove(0);
		waveAmount--;
		waveNumber++;
		return waveList.get(0);
	}

	Wave getNextWave() {
		return waveList.get(1);
	}

	Wave getCurrentWave() {
		if (!waveList.isEmpty()) {
			Wave currentWave = waveList.get(0);
			if (currentWave.hasCreeps())
				lastRelease = System.currentTimeMillis();
			else if (lastRelease + waveDelay < System.currentTimeMillis())
				currentWave = nextWave();

			return currentWave;
		}
		return null;
	}
	
	int getWaveNumber() {
		return this.waveNumber;
	}

}
