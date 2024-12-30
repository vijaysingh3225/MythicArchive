package com.techelevator.model;

public class CardColorCount {
    private String cardColor;
    private int count;

    public CardColorCount(String cardColor, int count) {
        this.cardColor = cardColor;
        this.count = count;
    }

    public String getCardColor() {
        return cardColor;
    }

    public void setCardColor(String cardColor) {
        this.cardColor = cardColor;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
