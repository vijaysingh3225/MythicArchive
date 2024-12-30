package com.techelevator.model;

public class CardCollectionDto {


    private int ownerId;
    private String username;

    private String collectionName;

    public CardCollectionDto() {
    }

    public CardCollectionDto(int ownerId, String collectionName) {
        this.ownerId = ownerId;
        this.collectionName = collectionName;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
}
