package com.game.towerdefense;

import java.util.ArrayList;

public class Route {

	private Tile start;
	private Tile end;
	private TowerDefenseView view;
	
	private ArrayList<Tile> checkPoints = new ArrayList<Tile>();

	private ArrayList<Tile> path = new ArrayList<Tile>();
	private ArrayList<Integer> pixelPath = new ArrayList<Integer>();
	//private ArrayList<Tile> checkPoints;

	public Route(Tile startPos, Tile endPos, TowerDefenseView view,
			ArrayList<Tile> checkPoints) {
		this.checkPoints = checkPoints;
		this.start = startPos;
		this.end = endPos;
		this.view = view;
		
		calculateRoute(startPos, endPos, checkPoints);
	}
	
	public Route(Tile startPos, Tile endPos, TowerDefenseView view) {
		this.start = startPos;
		this.end = endPos;
		this.view = view;
		
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

	private void calculateRoute(Tile startPos, Tile endPos,
			ArrayList<Tile> checkPoints) {
		
		Tile start = null;
		Tile end = null;
		
		checkPoints.add(endPos);

		for (Tile cp : checkPoints) {

			start = (cp == checkPoints.get(0)) ? startPos : end;
			end   = cp;
			
			for (int i = start.x; i != end.x; i = (int) (i + Math.signum(end.x - start.x)))
				this.path.add(new Tile(i, start.y, view));

			for (int j = start.y; j != end.y; j = (int) (j + Math.signum(end.y - start.y)))
				this.path.add(new Tile(end.x, j, view));
		}
	}

}
