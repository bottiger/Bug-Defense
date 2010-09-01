package com.game.towerdefense;

import java.util.ArrayList;

public class Route {

	private Tile start;
	private Tile end;
	private float tileSize;
	private float tileWidth = 1;
	
	private ArrayList<Tile> checkPoints = new ArrayList<Tile>();

	private ArrayList<Tile> path = new ArrayList<Tile>();
	private ArrayList<Integer> pixelPath = new ArrayList<Integer>();
	//private ArrayList<Tile> checkPoints;

	public Route(Tile startPos, Tile endPos, float tileSize,
			ArrayList<Tile> checkPoints) {
		this.checkPoints = checkPoints;
		this.start = startPos;
		this.end = endPos;
		this.tileSize = tileSize;
		
		calculateRoute(startPos, endPos, checkPoints);
	}
	
	public Route(Tile startPos, Tile endPos, float tileSize) {
		this.start = startPos;
		this.end = endPos;
		this.tileSize = tileSize;
		
		//calculateRoute(startPos, endPos, checkpoints);
	}
	
	public Tile getPosition(int index) {
		return path.get(index);
	}
	
	public Tile getNextPosition(int index) {
		return path.get(index+1);
	}

	public Tile getLastPos() {
		return path.get(path.size() - 1);
	}
	
	public ArrayList<Tile> getPath() {
		return path;
	}
	
	public ArrayList<Tile> getCheckPoints() {
		return checkPoints;
	}
	
	public Tile getStart() {
		return start;
	}
	
	public Tile getEnd() {
		return end;
	}
	
	public int length() {
		return path.size();
	}
	
	public float getWidth() {
		return tileWidth;
	}
	
	public void setWidth(float pixelWidth) {
		int tileWidthNew = (int)(pixelWidth / TileView.mTileSize);
		if (tileWidthNew != tileWidth) {
			this.blockRoad();
			tileWidth = tileWidthNew;
		}
	}

	private void calculateRoute(Tile startPos, Tile endPos,
			ArrayList<Tile> checkPoints) {
		
		Tile start = null;
		Tile end = null;
		
		checkPoints.add(endPos);

		for (Tile cp : checkPoints) {

			start = (cp == checkPoints.get(0)) ? startPos : end;
			end   = cp;
			
			for (int i = start.x; i != end.x; i = (int) (i + Math.signum(end.x - start.x)))
				this.path.add(new Tile(i, start.y));

			for (int j = start.y; j != end.y; j = (int) (j + Math.signum(end.y - start.y)))
				this.path.add(new Tile(end.x, j));
		}
	}
	
	// Lazy path blocker
	public void blockRoad() {
		for (Tile t : path) {
			for (int i = t.x-(int)tileWidth; i <= t.x+tileWidth; i++) {
				for (int j = t.y-(int)tileWidth; j <= t.y+tileWidth; j++) {
					TileMap.getTile(i, j).block();
				}
			}
		}
	}

}
