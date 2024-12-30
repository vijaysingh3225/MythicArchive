package com.techelevator.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Deck {

    private String deckName;

    private String deckFormat;
    private Map<UUID, Integer> cards;

    private int deckId;
    private int ownerId;
    private String ownerName;

    private boolean isPublic;
    private String thumbnailUrl;
    private int totalCards;
    public Deck(){}

    public Deck(int deckId, int ownerId, Map<UUID, Integer> cards, String deckName, boolean isPublic, String deckFormat) {
        this.deckId = deckId;
        this.ownerId = ownerId;
        this.cards = cards;
        this.deckName = deckName;
        this.isPublic = isPublic;
        this.deckFormat = deckFormat;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public String getDeckFormat() {
        return deckFormat;
    }

    public void setDeckFormat(String deckFormat) {
        this.deckFormat = deckFormat;
    }

    public Map<UUID, Integer> getCards() {
        return cards;
    }

    public void setCards(Map<UUID, Integer> cards) {
        this.cards = cards;
    }

    public int getDeckId() {
        return deckId;
    }

    public void setDeckId(int deckId) {
        this.deckId = deckId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getTotalCards() {
        return totalCards;
    }

    public void setTotalCards(int totalCards) {
        this.totalCards = totalCards;
    }
    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setCardCount(UUID cardId, int quantity) {
    }
    public Integer getCardCount(UUID cardId) {
        Integer cardCount = 0;
        if (cards.get(cardId) != null) {
            cardCount = cards.get(cardId);
        }
        return cardCount;
    }
}
