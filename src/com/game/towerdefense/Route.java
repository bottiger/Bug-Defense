package com.game.towerdefense;

import java.util.ArrayList;

public class Route {

	private Coordinate start;
	private Coordinate end;
	
	private ArrayList<Coordinate> checkPoints = new ArrayList<Coordinate>();

	private ArrayList<Coordinate> path = new ArrayList<Coordinate>();
	private ArrayList<Integer> pixelPath = new ArrayList<Integer>();
	//private ArrayList<Coordinate> checkPoints;

	public Route(Coordinate startPos, Coordinate endPos,
			ArrayList<Coordinate> checkPoints) {
		this.checkPoints = checkPoints;
		this.start = startPos;
		this.end = endPos;
		
		calculateRoute(startPos, endPos, checkPoints);
	}
	
	public Route(Coordinate startPos, Coordinate endPos) {
		this.start = startPos;
		this.end = endPos;
		
		//calculateRoute(startPos, endPos, checkpoints);
	}
	
	public Coordinate getPosition(int index) {
		return path.get(index);
	}
	
	public Coordinate getNextPosition(int index) {
		return path.get(index+1);
	}

	public Coordinate getLastPos() {
		return path.get(path.size() - 1);
	}
	
	public ArrayList<Coordinate> getPath() {
		return path;
	}
	
	public ArrayList<Coordinate> getCheckPoints() {
		return checkPoints;
	}
	
	public Coordinate getStart() {
		return start;
	}
	
	public Coordinate getEnd() {
		return end;
	}
	
	public int length() {
		return path.size();
	}

	private void calculateRoute(Coordinate startPos, Coordinate endPos,
			ArrayList<Coordinate> checkPoints) {
		
		Coordinate start = null;
		Coordinate end = null;
		
		checkPoints.add(endPos);

		for (Coordinate cp : checkPoints) {

			start = (cp == checkPoints.get(0)) ? startPos : end;
			end   = cp;
			
			for (int i = start.x; i != end.x; i = (int) (i + Math.signum(end.x - start.x)))
				this.path.add(new Coordinate(i, start.y));

			for (int j = start.y; j != end.y; j = (int) (j + Math.signum(end.y - start.y)))
				this.path.add(new Coordinate(end.x, j));
		}
	}

}
