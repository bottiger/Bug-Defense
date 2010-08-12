package com.game.towerdefense;

import java.util.LinkedList;
import java.util.List;

import android.graphics.drawable.Drawable;

import com.game.towerdefense.towers.*;

public class TowerManager {

	private List<Tower>towerList = new LinkedList<Tower>();
	private int towerNumber = 0;
	private TileMap tileMap;
	
	public TowerManager(TileMap tileMap) {
		this.tileMap = tileMap;
	}
	
	void addTower(Tower t) {
		towerList.add(t);
		towerNumber++;
	}
	
	void removeTower(Tower t) {
		towerList.remove(t);
		towerNumber--;
	}
	
	List<Tower> towers() {
		return towerList;
	}
	
	boolean createGenericTower(int x, int y, Drawable towerImage, Bank bank) {
		Tile tile = tileMap.getTile(x, y);
		return createGenericTower(tile, towerImage, bank);
	}
	
	boolean createGenericTower(Tile tile, Drawable towerImage, Bank bank) {
		if (tile.isBlocked())
			return false;
		
		Tower t = new GenericTower(tile);
		
		if (t.getPrice() > bank.getAmount())
			return false;
			
		t.setImage(towerImage);
		addTower(t);
		bank.decreaseAmount(t.getPrice());
		return true;
	}
	
}
