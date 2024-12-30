package com.techelevator.model;

public class ManaCostCount {
    private String manaCost;
    private int count;

    public ManaCostCount(String manaCost, int count) {
        this.manaCost = manaCost;
        this.count = count;
    }

    public String getManaCost() {
        return manaCost;
    }

    public void setManaCost(String manaCost) {
        this.manaCost = manaCost;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
