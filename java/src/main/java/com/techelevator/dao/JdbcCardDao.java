package com.techelevator.dao;

import com.techelevator.exception.DaoException;
import com.techelevator.model.Card;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class JdbcCardDao implements CardDao{
    private final String CARDS_SELECT = "SELECT cards.card_id, cards.card_name, cards.card_type, cards.card_color, cards.mana_cost," +
                                        " cards.rarity, cards.price, cards.set_name, cards.image_url, cards.thumbnail_url" +
                                        " FROM public.cards";
    private final JdbcTemplate jdbcTemplate;
    public JdbcCardDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private Card mapRowToCard(SqlRowSet rs){
        Card card = new Card();
        String cardIdString = rs.getString("card_id");
        UUID cardId = UUID.fromString(cardIdString);
        card.setCardId(cardId);
        card.setCardName(rs.getString("card_name"));
        card.setCardColor(rs.getString("card_color"));
        card.setCardType(rs.getString("card_type"));
        card.setManaCost(rs.getString("mana_cost"));
        card.setRarity(rs.getString("rarity"));
        card.setPrice(rs.getBigDecimal("price"));
        card.setSetName(rs.getString("set_name"));
        card.setImageUrl(rs.getString("image_url"));
        card.setThumbnailUrl(rs.getString("thumbnail_url"));
        return card;
    }
    @Override
    public Map<UUID, Integer> getCardMapForCollection(int collectionId) {
        Map<UUID, Integer> cardsInCollection = new HashMap<>();
        String sql = "SELECT cards.card_id, cards.card_name, cards.card_type, cards.card_color, cards.mana_cost," +
                     " cards.rarity, cards.price, cards.set_name, cards.image_url, cards.thumbnail_url, cards_collections.quantity FROM public.cards" +
                     " JOIN cards_collections ON cards.card_id = cards_collections.card_id" +
                     " JOIN collections ON cards_collections.collection_id = collections.collection_id" +
                     " WHERE collections.collection_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, collectionId);
            while (results.next()) {
               Card card = mapRowToCard(results);
               cardsInCollection.put(card.getCardId(), results.getInt("quantity"));
           }
        } catch (CannotGetJdbcConnectionException e) {
           throw new DaoException("Unable to connect to server or database", e);
        } return cardsInCollection;
    }

    @Override
    public Map<UUID, Integer> getCardMapForDeck(int deckId) {
        Map<UUID, Integer> cardsInDeck = new HashMap<>();
        String sql = "SELECT cards.card_id, cards.card_name, cards.card_type, cards.card_color, cards.mana_cost," +
                     " cards.rarity, cards.price, cards.set_name, cards.image_url, cards.thumbnail_url, cards_decks.quantity FROM public.cards" +
                     " JOIN cards_decks ON cards.card_id = cards_decks.card_id" +
                     " JOIN decks ON cards_decks.deck_id = decks.deck_id" +
                     " WHERE decks.deck_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, deckId);
            while (results.next()) {
                Card card = mapRowToCard(results);
                cardsInDeck.put(card.getCardId(), results.getInt("quantity"));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } return cardsInDeck;
    }

    @Override
    public Card getCardById(UUID cardId) {
        Card card = null;
        String sql = CARDS_SELECT +  " WHERE card_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, cardId);
            if (results.next()) {
                card = mapRowToCard(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return card;
    }

    private String getCardType(String type) {
        String cardType = type;
        if (type.toLowerCase().contains(" planeswalker ")) {
            cardType = "Planeswalker";
        } else if (type.toLowerCase().contains(" creature ") && type.toLowerCase().contains(" artifact ")) {
            cardType = "Artifact, Creature";
        } else if (type.toLowerCase().contains(" creature ") && type.toLowerCase().contains(" enchantment ")) {
            cardType = "Enchantment, Creature";
        } else if (type.toLowerCase().contains(" creature ")) {
            cardType = "Creature";
        } else if (type.toLowerCase().contains(" enchantment ")) {
            cardType = "Enchantment";
        } else if (type.toLowerCase().contains(" artifact ")) {
            cardType = "Artifact";
        } else if (type.toLowerCase().contains(" sorcery ")) {
            cardType = "Sorcery";
        } else if (type.toLowerCase().contains(" instant ")) {
            cardType = "Instant";
        } else if (type.toLowerCase().contains(" kindred ")) {
            cardType = "Kindred";
        } else if (type.toLowerCase().contains(" tribal ")) {
            cardType = "Tribal";
        } else if (type.toLowerCase().contains(" battle ")) {
            cardType = "Battle";
        } else if (type.toLowerCase().contains(" land ")) {
            cardType = "Land";
        } else if (type.toLowerCase().contains(" dungeon ")) {
            cardType = "Dungeon";
        }
        return cardType;
    }
    @Override
    public Card createNewCard(Card card) {
        Card newCard = null;
        String cardType = getCardType(card.getCardType());
        String cardSql = "INSERT INTO public.cards(card_id, card_name, card_color, card_type, mana_cost," +
                " rarity, price, set_name, image_url, thumbnail_url)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING card_id";
        try {
            UUID newCardId = jdbcTemplate.queryForObject(cardSql, UUID.class, card.getCardId(), card.getCardName(), card.getCardColor(), cardType, card.getManaCost(),
                                                        card.getRarity(), card.getPrice(), card.getSetName(), card.getImageUrl(), card.getThumbnailUrl());
            newCard = getCardById(newCardId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return newCard;
    }
}
