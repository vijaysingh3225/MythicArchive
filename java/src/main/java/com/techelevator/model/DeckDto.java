package com.techelevator.model;

public class DeckDto {

    private int ownerId;

    private String deckName;

    public DeckDto() {
    }

    public DeckDto(int ownerId, String deckName) {
        this.ownerId = ownerId;
        this.deckName = deckName;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }
}
