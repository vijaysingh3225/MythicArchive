package com.techelevator.dao;

import com.techelevator.model.Card;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CardDao {

    Map<UUID, Integer> getCardMapForCollection(int collectionId);
    Map<UUID, Integer> getCardMapForDeck(int deckId);
    Card getCardById(UUID cardId);
    Card createNewCard(Card card);
}
