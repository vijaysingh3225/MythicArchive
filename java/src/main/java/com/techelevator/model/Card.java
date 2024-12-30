package com.techelevator.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Card {

    private UUID cardId;
    private String cardName;
    private String cardType;
    private String cardColor;
    private String manaCost;
    private String rarity;
    private BigDecimal price;
    private String setName;
    private String imageUrl;
    private String thumbnailUrl;

    public Card(UUID cardId, String cardName, String cardType, String cardColor, String manaCost, String rarity, BigDecimal price, String setName, String imageUrl, String thumbnailUrl) {
        this.cardId = cardId;
        this.cardName = cardName;
        this.cardType = cardType;
        this.cardColor = cardColor;
        this.manaCost = manaCost;
        this.rarity = rarity;
        this.price = price;
        this.setName = setName;
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public Card(){};

    public UUID getCardId() {
        return cardId;
    }

    public void setCardId(UUID cardId) {
        this.cardId = cardId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardType() {return cardType;}

    public void setCardType(String cardType) {this.cardType = cardType;}

    public String getManaCost() {return manaCost;}

    public void setManaCost(String manaCost) {this.manaCost = manaCost;}

    public String getRarity() {return rarity;}

    public void setRarity(String rarity) {this.rarity = rarity;}

    public BigDecimal getPrice() {return price;}

    public void setPrice(BigDecimal price) {this.price = price;}

    public String getSetName() {return setName;}

    public void setSetName(String setName) {this.setName = setName;}

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getCardColor() {
        return cardColor;
    }

    public void setCardColor(String cardColor) {
        this.cardColor = cardColor;
    }
}
