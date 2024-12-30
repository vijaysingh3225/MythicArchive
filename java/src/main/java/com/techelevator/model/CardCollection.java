package com.techelevator.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CardCollection {

    private int collectionId;
    private int ownerId;
    private String ownerName;
    private Map<UUID, Integer> cards;
    private boolean isPublic;
    private String thumbnailUrl;
    private String collectionName;
    private int totalCards;

    public CardCollection() {

    }

    public CardCollection(int collectionId, int ownerId, Map<UUID, Integer> cards, String collectionName, boolean isPublic) {
        this.collectionId = collectionId;
        this.ownerId = ownerId;
        this.cards = cards;
        this.collectionName = collectionName;
        this.isPublic = isPublic;
    }

    public int getTotalCards() {
        int sum = 0;
        for (Integer count : cards.values()) {
            sum+= count;
        }
        return sum;
    }

    public void setTotalCards(Integer totalCards) {
        this.totalCards = totalCards;
    }

    public int getCollectionId() {
        return collectionId;
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
    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public Map<UUID, Integer> getCards() {
        return cards;
    }

    public void setCards(Map<UUID, Integer> cards) {
        this.cards = cards;
    }

    public Integer getCardCount(UUID cardId) {
        Integer cardCount = 0;
        if (cards.get(cardId) != null) {
            cardCount = cards.get(cardId);
        }
        return cardCount;
    }

    public void setCardCount(UUID cardId, int quantity) {
        this.cards.put(cardId, quantity);
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
