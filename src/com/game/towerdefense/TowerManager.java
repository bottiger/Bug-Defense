package com.game.towerdefense;

import java.util.LinkedList;
import java.util.List;

import android.graphics.drawable.Drawable;

import com.game.towerdefense.towers.*;

public class TowerManager {

	private List<Tower>towerList = new LinkedList<Tower>();
	private int towerNumber = 0;
	private float tileSize;
	
	public TowerManager(float tileSize) {
		this.tileSize = tileSize;
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
		Tile tile = TileMap.getTile(x, y, tileSize);
		return createGenericTower(tile, towerImage, bank);
	}
	
	boolean createGenericTower(Tile tile, Drawable towerImage, Bank bank) {
		if (tile.isBlocked())
			return false;
		
		Tower t = new GenericTower(tile);
		
		return createTower(t, towerImage, bank);
	}

	boolean createHeavyTower(Tile tile, Drawable towerImage, Bank bank) {
		if (tile.isBlocked())
			return false;
		Tower t = new HeavyTower(tile);
		return createTower(t, towerImage, bank);
	}
	
	boolean createSniperTower(Tile tile, Drawable towerImage, Bank bank) {
		if (tile.isBlocked())
			return false;
		Tower t = new SniperTower(tile);
		return createTower(t, towerImage, bank);
	}
	
	private boolean createTower(Tower t, Drawable towerImage, Bank bank) {
		
		if (t.getPrice() > bank.getAmount())
			return false;
		
		if (!blockTiles(t))
			return false;
			
		t.setImage(towerImage);
		addTower(t);
		bank.decreaseAmount(t.getPrice());
		return true;
	}
	
	private boolean blockTiles(Tower tower) {
		int tx = tower.getPosition().x;
		int ty = tower.getPosition().y;
		int ts = tower.getSize();
		
		for (int x = tx-ts; x <= tx+ts; x++) {
			for (int y = ty-ts; y <ty+ts; y++) {
				if (TileMap.getTile(x, y, TileView.mTileSize).isBlocked())
					return false;				
			}
		}
		
		for (int x = tx-ts; x <= tx+ts; x++) {
			for (int y = ty-ts; y <ty+ts; y++) {
				TileMap.getTile(x, y, TileView.mTileSize).block(tower);		
			}
		}
		return true;
	}
	
}
