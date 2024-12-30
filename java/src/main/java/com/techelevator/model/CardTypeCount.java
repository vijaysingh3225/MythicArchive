package com.techelevator.model;

public class CardTypeCount {
    private String cardType;
    private int count;

    public CardTypeCount(String cardType, int count) {
        this.cardType = cardType;
        this.count = count;
    }

    public String getCardType() {return cardType;}

    public void setCardType(String cardType) {this.cardType = cardType;}

    public int getCount() {return count;}

    public void setCount(int count) {this.count = count;}
}
