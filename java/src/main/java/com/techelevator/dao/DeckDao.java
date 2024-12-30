package com.techelevator.dao;

import com.techelevator.model.*;

import java.util.List;
import java.util.UUID;

public interface DeckDao {
    List<Deck> getAllPublicDecks();
    Deck addCardsToDeck(Card card, Deck deck, int quantity);
    void addExistingCardToDeck(Card card, Deck deck, int quantity);
    void removeAllCardsOfTypeFromDeck(UUID cardId, int deckId);
    void removeCardsFromDeck(Card card, Deck deck, int quantity);
    List<Card> getCardsInDeck(int deckId);
    Deck createNewDeck(DeckDto deck);
    Deck getDeckById(int deckId);
    Deck getDeckByUserId(int userId);
    int setDeckPublic(int deckId);
    int setDeckPrivate(int deckId);
    int renameDeck(int deckId, String deckName);
    int setDeckThumbnail(int deckId, String thumbnail);
    boolean isCardInDeck(UUID cardId, int deckId);
}
