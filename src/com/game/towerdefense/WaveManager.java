package com.game.towerdefense;

import java.util.LinkedList;
import java.util.List;

import com.game.towerdefense.creeps.*;

public class WaveManager {
	
	private List<Wave>waveList = new LinkedList<Wave>();
	private int waveNumber = 0;
	
	public WaveManager(TowerDefenseView towerDefenseView) {
		
		Route route = RouteGenerator.GenericRoute(towerDefenseView);
		
		Wave wave = new Wave();
		for (int i = 0; i < 20; i++) {
			GenericCreep creep = new GenericCreep(10, 100, 10, route);
			wave.addCreeps(creep, towerDefenseView.getContext().getResources().getDrawable(R.drawable.creep1));
		}
		addWave(wave);
		
		wave = new Wave();
		for (int i = 0; i < 20; i++) {
			GenericCreep creep = new GenericCreep(10, 100, 10, route);
			wave.addCreeps(creep, towerDefenseView.getContext().getResources().getDrawable(R.drawable.creep2));
		}
		addWave(wave);
		
		wave = new Wave();
		for (int i = 0; i < 20; i++) {
			GenericCreep creep = new GenericCreep(10, 100, 10, route);
			wave.addCreeps(creep, towerDefenseView.getContext().getResources().getDrawable(R.drawable.creep3));
		}
		addWave(wave);
		
	}
	
	void addWave(Wave w) {
		waveList.add(w);
		waveNumber++;
	}

}
