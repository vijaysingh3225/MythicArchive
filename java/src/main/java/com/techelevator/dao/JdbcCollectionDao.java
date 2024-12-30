package com.techelevator.dao;

import com.techelevator.exception.DaoException;
import com.techelevator.model.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class JdbcCollectionDao implements CollectionDao{
    private UserDao userDao;
    private CardDao cardDao;
    private final JdbcTemplate jdbcTemplate;
    private final String COLLECTIONS_SELECT = "SELECT collection_id, collection_name, users.user_id, users.username, is_public, thumbnail_url FROM public.collections\n" +
            " INNER JOIN users ON collections.user_id = users.user_id";

    public JdbcCollectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = new JdbcUserDao(jdbcTemplate);
        this.cardDao = new JdbcCardDao(jdbcTemplate);
    }
    @Override
    public List<CardCollection> getAllPublicCollections() {
        List<CardCollection> cardCollections = new ArrayList<>();
        String sql = COLLECTIONS_SELECT + " WHERE is_public";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                CardCollection cardCollection = mapRowToCollection(results);
                cardCollections.add(cardCollection);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return cardCollections;
    }
    @Override
    public CardCollection getCollectionByUserId(int userId) {
        CardCollection collection = null;
        String sql = COLLECTIONS_SELECT +  " WHERE users.user_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()) {
                collection = mapRowToCollection(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return collection;
    }
    @Override
    public CardCollection getCollectionById(int collectionId) {
        CardCollection collection = null;
        String sql = COLLECTIONS_SELECT +  " WHERE collection_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, collectionId);
            if (results.next()) {
                collection = mapRowToCollection(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return collection;
    }
    @Override
    public List<Card> getCardsInCollection(int collectionId) {
        List<Card> cardsList = new ArrayList<>();
        for (UUID cardId : getCollectionById(collectionId).getCards().keySet()) {
            cardsList.add(cardDao.getCardById(cardId));
        }
        return cardsList;
    }
    @Override
    public CardCollection createNewCollection(CardCollectionDto collection){
        CardCollection newCollection = null;
        String collectionSql = "INSERT INTO public.collections(collection_name, user_id) VALUES (?, ?, ?) RETURNING collection_id";
        try {
            int newCollectionId = jdbcTemplate.queryForObject(collectionSql, int.class, collection.getCollectionName(), collection.getOwnerId());
            newCollection = getCollectionById(newCollectionId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return newCollection;
    };

    //ADD CARDS METHODS
    @Override
    public CardCollection addCardsToCollection(Card card, CardCollection collection, int quantity) {
        CardCollection updatedCollection = null;
        String collectionSql = "INSERT INTO public.cards_collections(card_id, collection_id, quantity) VALUES (?, ?, ?) RETURNING collection_id";
        try {
            int updatedCollectionId = jdbcTemplate.queryForObject(collectionSql, int.class, card.getCardId(), collection.getCollectionId(), quantity);
            updatedCollection = getCollectionById(updatedCollectionId);
            if (updatedCollection != null) {
                updatedCollection.setCardCount(card.getCardId(), quantity);
                if (updatedCollection.getCards().size() == 1) {
                    int updatedThumbailValue = setCollectionThumbnail(updatedCollectionId, card.getThumbnailUrl());
                }
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return updatedCollection;
    }
    @Override
    public void addExistingCardToCollection(Card card, CardCollection collection, int quantity) {
        int numberOfRows = 0;
        String updateQuantitySql = "UPDATE public.cards_collections" +
                " SET quantity=?" +
                " WHERE card_id=? AND collection_id=?";
        try {
            int currentAmount = collection.getCardCount(card.getCardId());
            numberOfRows = jdbcTemplate.update(updateQuantitySql, (currentAmount + quantity), card.getCardId(), collection.getCollectionId());
            if (numberOfRows == 1) {
                collection.setCardCount(card.getCardId(), (currentAmount + quantity));
            } else {
                throw new DaoException("Unexpected number of rows affected");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    //REMOVE CARDS METHODS
    @Override
    public void removeAllCardsOfTypeFromCollection(UUID cardId, int collectionId){
        int numberOfRows = 0;
        String removeCollectionSql = "DELETE FROM public.cards_collections WHERE card_id = ? AND collection_id = ?";
        try {
            numberOfRows = jdbcTemplate.update(removeCollectionSql, cardId, collectionId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }
    @Override
    public void removeCardsFromCollection(Card card, CardCollection collection, int quantity) {
        int numberOfRows = 0;
        String updateQuantitySql = "UPDATE public.cards_collections" +
                " SET quantity=?" +
                " WHERE card_id=? AND collection_id=?";
        try {
            int currentAmount = collection.getCardCount(card.getCardId());
            if (currentAmount - quantity <= 0) {
                removeAllCardsOfTypeFromCollection(card.getCardId(), collection.getCollectionId());
            } else {
                numberOfRows = jdbcTemplate.update(updateQuantitySql, (currentAmount - quantity), card.getCardId(), collection.getCollectionId());
                if (numberOfRows == 1) {
                    collection.setCardCount(card.getCardId(), (currentAmount - quantity));
                } else {
                    throw new DaoException("Unexpected number of rows affected");
                }
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }
    @Override
    public int setCollectionPublic(int collectionId) {
        int numberOfRows = 0;
        String setPublicSql = "UPDATE public.collections" +
                " SET is_public= true" +
                " WHERE collection_id = ?";
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
    public int setCollectionPrivate(int collectionId) {
        int numberOfRows = 0;
        String setPrivateSql = "UPDATE public.collections" +
                " SET is_public= false" +
                " WHERE collection_id = ?";
        try {
            numberOfRows = jdbcTemplate.update(setPrivateSql, collectionId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return numberOfRows;
    }
    @Override
    public int renameCollection(int collectionId, String collectionName) {
        int numberOfRows = 0;
        String renameSql = "UPDATE public.collections" +
                " SET collection_name = ?" +
                " WHERE collection_id = ?";
        try {
            numberOfRows = jdbcTemplate.update(renameSql, collectionName, collectionId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return numberOfRows;
    }
    @Override
    public int setCollectionThumbnail(int collectionId, String thumbnailUrl) {
        int numberOfRows = 0;
        String setThumbnailSql = "UPDATE public.collections" +
                " SET thumbnail_url = ?" +
                " WHERE collection_id = ?";
        try {
            numberOfRows = jdbcTemplate.update(setThumbnailSql, thumbnailUrl, collectionId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return numberOfRows;
    }
    @Override
    public boolean isCardInCollection(UUID cardId, int collectionId) {
        String isCardInCollectionSql = "SELECT COUNT(card_id) FROM cards_collections WHERE card_id = ? AND collection_id = ?";
        Integer count = jdbcTemplate.queryForObject(isCardInCollectionSql, Integer.class, cardId, collectionId);
        return count != null && count > 0;
    }
    private CardCollection mapRowToCollection(SqlRowSet rs) {
        CardCollection cardCollection = new CardCollection();
        cardCollection.setCollectionId(rs.getInt("collection_id"));
        cardCollection.setCollectionName(rs.getString("collection_name"));
        cardCollection.setOwnerId(rs.getInt("user_id"));
        cardCollection.setOwnerName(rs.getString("username"));
        cardCollection.setIsPublic(rs.getBoolean("is_public"));
        //Get the cards in the collection
        Map<UUID, Integer> cardsInCollection = cardDao.getCardMapForCollection(cardCollection.getCollectionId());
        cardCollection.setTotalCards(cardsInCollection.size());
        //Check if the thumbnail image has been set
        if (rs.getString("thumbnail_url").isEmpty()) {
            //If the collection has no thumbnail set, set to a default image
            cardCollection.setThumbnailUrl("");
        } else {
            cardCollection.setThumbnailUrl(rs.getString("thumbnail_url"));
        }
        //Set the list of cards in the collection
        cardCollection.setCards(cardsInCollection);
        return cardCollection;
    }
    @Override
    public CollectionStats getCollectionStats(int collectionId) {
        CollectionStats stats = new CollectionStats();

        // SQL to get card type counts
        String cardTypeSql = "SELECT card_type, COUNT(*) AS count FROM cards_collections " +
                "INNER JOIN cards ON cards.card_id = cards_collections.card_id " +
                "WHERE cards_collections.collection_id = ? GROUP BY card_type";
        List<CardTypeCount> cardTypeCounts = jdbcTemplate.query(cardTypeSql, (rs, rowNumber) -> {
            String cardType = rs.getString("card_type");
            int count = rs.getInt("count");
            return new CardTypeCount(cardType, count);
        }, collectionId);
        stats.setCardTypeCounts(cardTypeCounts);

        //SQL to get card colors
        String cardColorSql = "SELECT card_color, COUNT(*) AS count FROM cards_collections " +
                "INNER JOIN cards ON cards.card_id = cards_collections.card_id " +
                "WHERE cards_collections.collection_id = ? GROUP BY card_color";
        List<CardColorCount> cardColorCounts = jdbcTemplate.query(cardColorSql, (rs, rowNumber) -> {
            String cardColor = rs.getString("card_color");
            int count = rs.getInt("count");
            return new CardColorCount(cardColor, count);
        }, collectionId);
        stats.setCardColorCounts(cardColorCounts);

        // SQL to get mana cost counts
        String manaCostSql = "SELECT mana_cost, COUNT(*) AS count FROM cards_collections " +
                "INNER JOIN cards ON cards.card_id = cards_collections.card_id " +
                "WHERE cards_collections.collection_id = ? GROUP BY mana_cost";
        List<ManaCostCount> manaCostCounts = jdbcTemplate.query(manaCostSql, (rs, rowNumber) -> {
            String manaCost = rs.getString("mana_cost");
            int count = rs.getInt("count");
            return new ManaCostCount(manaCost, count);
        }, collectionId);
        stats.setManaCostCounts(manaCostCounts);

        // SQL to get rarity counts
        String raritySql = "SELECT rarity, COUNT(*) AS count FROM cards_collections " +
                "INNER JOIN cards ON cards.card_id = cards_collections.card_id " +
                "WHERE cards_collections.collection_id = ? GROUP BY rarity";
        List<RarityCount> rarityCounts = jdbcTemplate.query(raritySql, (rs, rowNumber) -> {
            String rarity = rs.getString("rarity");
            int count = rs.getInt("count");
            return new RarityCount(rarity, count);
        }, collectionId);
        stats.setRarityCounts(rarityCounts);

        // SQL to get total collection price
        String collectionPriceSql = "SELECT SUM(price * quantity) FROM cards_collections " +
                "INNER JOIN cards ON cards.card_id = cards_collections.card_id " +
                "WHERE cards_collections.collection_id = ? AND price != -1";
        BigDecimal collectionPrice = jdbcTemplate.queryForObject(collectionPriceSql, BigDecimal.class, collectionId);
        stats.setTotalCollectionPrice(collectionPrice != null ? collectionPrice : new BigDecimal("0.00"));

        // SQL to get cards without a price
        String withoutPriceSql = "SELECT SUM(quantity) FROM cards_collections " +
                "INNER JOIN cards ON cards.card_id = cards_collections.card_id " +
                "WHERE cards_collections.collection_id = ? AND price = -1";
        Integer withoutPrice = jdbcTemplate.queryForObject(withoutPriceSql, Integer.class, collectionId);
        if (withoutPrice == null) {
            withoutPrice = 0;
        }
        stats.setCardsWithoutPrice(withoutPrice);

        // SQL to get cards types without a price
        String withoutPriceTypeSql = "SELECT COUNT(*) FROM cards_collections " +
                "INNER JOIN cards ON cards.card_id = cards_collections.card_id " +
                "WHERE cards_collections.collection_id = ? AND price = -1";
        int withoutPriceType = jdbcTemplate.queryForObject(withoutPriceTypeSql, int.class, collectionId);
        stats.setCardTypesWithoutPrice(withoutPriceType);

        // SQL to get set name counts
        String setNameSql = "SELECT set_name, COUNT(*) AS count FROM cards_collections " +
                "INNER JOIN cards ON cards.card_id = cards_collections.card_id " +
                "WHERE cards_collections.collection_id = ? GROUP BY set_name";
        List<SetNameCount> setNameCounts = jdbcTemplate.query(setNameSql, (rs, rowNumber) -> {
            String setName = rs.getString("set_name");
            int count = rs.getInt("count");
            return new SetNameCount(setName, count);
        }, collectionId);
        stats.setSetNameCounts(setNameCounts);

        return stats;
    }
};
