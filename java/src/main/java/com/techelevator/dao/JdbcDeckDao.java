package com.techelevator.dao;

import com.techelevator.exception.DaoException;
import com.techelevator.model.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class JdbcDeckDao implements DeckDao{
    private UserDao userDao;
    private CardDao cardDao;
    private final JdbcTemplate jdbcTemplate;
    private final String DECKS_SELECT = "SELECT deck_id, deck_name, users.user_id, users.username, is_public, thumbnail_url, deckformat FROM public.decks\n" +
            " INNER JOIN users ON decks.user_id = users.user_id";

    public JdbcDeckDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = new JdbcUserDao(jdbcTemplate);
        this.cardDao = new JdbcCardDao(jdbcTemplate);
    }
    @Override
    public List<Deck> getAllPublicDecks() {
        List<Deck> decks = new ArrayList<>();
        String sql = DECKS_SELECT + " WHERE is_public";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                 Deck deck = mapRowToDecks(results);
                decks.add(deck);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return decks;
    }

    private Deck mapRowToDecks(SqlRowSet rs) {

        Deck decks = new Deck();
        decks.setDeckId(rs.getInt("deck_id"));
        decks.setDeckName(rs.getString("deck_name"));
        decks.setOwnerId(rs.getInt("user_id"));
        decks.setOwnerName(rs.getString("username"));
        decks.setPublic(rs.getBoolean("is_public"));
        //Get the cards in the collection
        Map<UUID, Integer> cardsInDeck = cardDao.getCardMapForDeck(decks.getDeckId());
        decks.setTotalCards(cardsInDeck.size());
        //Check if the thumbnail image has been set
        if (rs.getString("thumbnail_url").isEmpty()) {
            //If the collection has no thumbnail set, set to a default image
            decks.setThumbnailUrl("");
        } else {
            decks.setThumbnailUrl(rs.getString("thumbnail_url"));
        }
        //Set the list of cards in the collection
        decks.setCards(cardsInDeck);
        return decks;
    }
    @Override

    public Deck getDeckByUserId(int userId) {
        Deck deck = null;
        String sql = DECKS_SELECT +  " WHERE users.user_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()) {
                deck = mapRowToDecks(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return deck;
    }
    @Override
    public Deck getDeckById(int deckId) {
        Deck deck = null;
        String sql = DECKS_SELECT +  " WHERE decks.deck_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, deckId);
            if (results.next()) {
                deck = mapRowToDecks(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return deck;
    }
    @Override
    public List<Card> getCardsInDeck(int deckId) {
        List<Card> cardsList = new ArrayList<>();
        for (UUID cardId : getDeckById(deckId).getCards().keySet()) {
            cardsList.add(cardDao.getCardById(cardId));
        }
        return cardsList;
    }

    @Override
    public Deck createNewDeck(DeckDto deck){
        Deck newDeck = null;
        String deckSql = "INSERT INTO public.decks(deck_name, user_id) VALUES (?, ?) RETURNING deck_id";
        try {
            int newDeckId = jdbcTemplate.queryForObject(deckSql, int.class, deck.getDeckName(), deck.getOwnerId());
            newDeck = getDeckById(newDeckId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return newDeck;
    }
    //ADD CARDS METHOD
    @Override
    public Deck addCardsToDeck(Card card, Deck deck, int quantity) {
        Deck updatedDeck = null;
        String deckSql = "INSERT INTO public.cards_decks(card_id, deck_id, quantity) VALUES (?, ?, ?) RETURNING deck_id";
        try {
            int updatedDeckId = jdbcTemplate.queryForObject(deckSql, int.class, card.getCardId(), deck.getDeckId(), quantity);
            updatedDeck = getDeckById(updatedDeckId);
            if (updatedDeck!= null) {
                updatedDeck.setCardCount(card.getCardId(), quantity);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return updatedDeck;
    }
    @Override
    public void addExistingCardToDeck(Card card, Deck deck, int quantity) {
        int numberOfRows = 0;
        String updateQuantitySql = "UPDATE public.cards_decks" +
                " SET quantity=?" +
                " WHERE card_id=? AND deck_id=?";
        try {
            int currentAmount = deck.getCardCount(card.getCardId());
            numberOfRows = jdbcTemplate.update(updateQuantitySql, (currentAmount + quantity), card.getCardId(), deck.getDeckId());
            if (numberOfRows == 1) {
                deck.setCardCount(card.getCardId(), (currentAmount + quantity));
            } else {
                throw new DaoException("Unexpected number of rows affected");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }
    @Override
    public void removeAllCardsOfTypeFromDeck(UUID cardId, int deckId){
        int numberOfRows = 0;
        String removeAllFromDeckSql = "DELETE FROM public.cards_decks WHERE card_id = ? AND deck_id = ?";
        try {
            numberOfRows = jdbcTemplate.update(removeAllFromDeckSql, cardId, deckId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }
    @Override
    public void removeCardsFromDeck(Card card, Deck deck, int quantity) {
        int numberOfRows = 0;
        String updateQuantitySql = "UPDATE public.cards_decks" +
                " SET quantity=?" +
                " WHERE card_id=? AND deck_id=?";
        try {
            int currentAmount = deck.getCardCount(card.getCardId());
            if (currentAmount - quantity <= 0) {
                removeAllCardsOfTypeFromDeck(card.getCardId(), deck.getDeckId());
            }
            numberOfRows = jdbcTemplate.update(updateQuantitySql, (currentAmount - quantity), card.getCardId(), deck.getDeckId());
            if (numberOfRows == 1) {
                deck.setCardCount(card.getCardId(), (currentAmount - quantity));
            } else {
                throw new DaoException("Unexpected number of rows affected");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }
    @Override
    public int setDeckPublic(int collectionId) {
        int numberOfRows = 0;
        String setPublicSql = "UPDATE public.decks" +
                " SET is_public= true" +
                " WHERE deck_id = ?";
        try {
            numberOfRows = jdbcTemplate.update(setPublicSql, collectionId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return numberOfRows;
    }
    @Override
    public int setDeckThumbnail(int deckId, String thumbnail_url) {
        int numberOfRows = 0;
        String setThumbnailSql = "UPDATE public.decks" +
                " SET thumbnail_url = ?" +
                " WHERE deck_id = ?";
        try {
            numberOfRows = jdbcTemplate.update(setThumbnailSql, thumbnail_url, deckId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return numberOfRows;
    }

    @Override
    public int renameDeck(int deckId, String deckName) {
        int numberOfRows = 0;
        String renameSql = "UPDATE decks" +
                " SET deck_name = ?" +
                " WHERE deck_id = ?";
        try {
            numberOfRows = jdbcTemplate.update(renameSql, deckName, deckId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return numberOfRows;
    }
    @Override
    public int setDeckPrivate(int deckId) {
        int numberOfRows = 0;
        String setPrivateSql = "UPDATE public.decks" +
                " SET is_public= true" +
                " WHERE deck_id = ?";
        try {
            numberOfRows = jdbcTemplate.update(setPrivateSql, deckId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return numberOfRows;
    }
    @Override
    public boolean isCardInDeck(UUID cardId, int deckId) {
        String isCardInCollectionSql = "SELECT COUNT(card_id) FROM cards_decks WHERE card_id = ? AND deck_id = ?";
        Integer count = jdbcTemplate.queryForObject(isCardInCollectionSql, Integer.class, cardId, deckId);
        return count != null && count > 0;
    }
    }
