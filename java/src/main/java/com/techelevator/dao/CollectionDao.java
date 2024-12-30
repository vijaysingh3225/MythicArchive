package com.techelevator.dao;

import com.techelevator.model.Card;
import com.techelevator.model.CardCollection;
import com.techelevator.model.CardCollectionDto;
import com.techelevator.model.CollectionStats;

import java.util.List;
import java.util.UUID;

public interface CollectionDao {

    List<CardCollection> getAllPublicCollections();
    CardCollection addCardsToCollection(Card card, CardCollection collection, int quantity);
    void addExistingCardToCollection(Card card, CardCollection collection, int quantity);
    void removeAllCardsOfTypeFromCollection(UUID cardId, int collectionId);
    void removeCardsFromCollection(Card card, CardCollection collection, int quantity);
    List<Card> getCardsInCollection(int collectionId);
    CardCollection createNewCollection(CardCollectionDto collection);
    CardCollection getCollectionById(int collectionId);
    CardCollection getCollectionByUserId(int userId);
    int setCollectionPublic(int collectionId);
    int setCollectionPrivate(int collectionId);
    int renameCollection(int collectionId, String collectionName);
    int setCollectionThumbnail(int collectionId, String thumbnail);
    boolean isCardInCollection(UUID cardId, int collectionId);
    CollectionStats getCollectionStats(int collectionId);
}

