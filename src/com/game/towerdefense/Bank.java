package com.game.towerdefense;

public class Bank {

	private int money;
	
	public Bank(int amount) {
		this.money = amount;
	}
	
	void setAmount(int amount) {
		this.money = amount;
	}
	
	int getAmount() {
		return this.money;
	}
	
	void decreaseAmount(int amount) {
		this.money -= amount;
	}
	
	void increaseMoney(int amount) {
		this.money += amount;
	}
}
