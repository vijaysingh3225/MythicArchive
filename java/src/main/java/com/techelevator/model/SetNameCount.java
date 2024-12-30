package com.techelevator.model;

public class SetNameCount {
    private String setName;
    private int count;

    public SetNameCount(String setName, int count) {
        this.setName = setName;
        this.count = count;
    }
    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
