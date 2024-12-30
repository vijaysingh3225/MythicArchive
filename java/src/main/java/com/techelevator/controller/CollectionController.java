package com.techelevator.controller;


import com.techelevator.dao.CardDao;
import com.techelevator.dao.CollectionDao;
import com.techelevator.dao.UserDao;
import com.techelevator.exception.DaoException;
import com.techelevator.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/collections")
//@PreAuthorize("isAuthenticated()")
public class CollectionController {

    private final CardDao cardDao;
    private final UserDao userDao;
    private final CollectionDao collectionDao;

    public CollectionController(UserDao userDao, CollectionDao collectionDao, CardDao cardDao) {
        this.userDao = userDao;
        this.collectionDao = collectionDao;
        this.cardDao = cardDao;
    }

//    @PreAuthorize("permitAll()")
    @RequestMapping(value = "/all-public", method = RequestMethod.GET)
    public List<CardCollection> getAllPublicCollections() {
        return collectionDao.getAllPublicCollections();
    }
//    @PreAuthorize("permitAll()")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public CardCollection getCollectionById(@PathVariable int id) {
        try {
            CardCollection collection;
            collection =  collectionDao.getCollectionById(id);
            if (collection == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find specified collection.");
            }
            return collection;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("permitAll()")
    @GetMapping("/{id}/cards")
    public List<Card> getCardsInCollection(@PathVariable int id) {
        try {
            List<Card> cards = collectionDao.getCardsInCollection(id);
            return cards;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("permitAll()")
    @GetMapping("/user/{id}")
    public int getCollectionIdByUser(@PathVariable int id) {
        try {
            int collectionId = collectionDao.getCollectionByUserId(id).getCollectionId();
            return collectionId;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("permitAll()")
    @GetMapping("/{id}/total-cards")
    public Integer getTotalCardsInCollection(@PathVariable int id) {
        try {

            Integer totalCards = collectionDao.getCollectionByUserId(id).getTotalCards();
            return totalCards;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("permitAll()")
    @GetMapping("/{id}/card-count")
    public Integer cardCount(@PathVariable int id, @RequestParam UUID cardId) {
        try {

            Integer cardCount = collectionDao.getCollectionByUserId(id).getCardCount(cardId);
            return cardCount;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("permitAll()")
    @GetMapping("/{collectionId}/stats")
    public CollectionStats getCollectionStats(@PathVariable int collectionId) {
        try {
            CollectionStats collectionStats = collectionDao.getCollectionStats(collectionId);
            return collectionStats;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/create")
    public ResponseEntity<CardCollection> createNewCollection(@RequestBody CardCollectionDto collection) {
        try {
            CardCollection createdCollection = collectionDao.createNewCollection(collection);

            return new ResponseEntity<>(createdCollection, HttpStatus.CREATED);
        } catch (DaoException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/add")
    public ResponseEntity<CardCollection> addCardToCollection(@RequestBody AdjustCardRequestDto addCard) {
        try {

            CardCollection collection = collectionDao.getCollectionByUserId(addCard.getUserId());
            Card cardToAdd = cardDao.getCardById(addCard.getCard().getCardId());
            //If SELECT statement returns null, add card to cards database
            if (cardToAdd == null) {
                cardToAdd = cardDao.createNewCard(addCard.getCard());
            }
            //If card is already in collection, call function to update database
            if (collectionDao.isCardInCollection(addCard.getCard().getCardId(), collection.getCollectionId())) {
                collectionDao.addExistingCardToCollection(cardToAdd, collection, addCard.getQuantity());
            } //else call function to insert new record
            else {
                collectionDao.addCardsToCollection(cardToAdd, collection, addCard.getQuantity());
            }
            collection = collectionDao.getCollectionByUserId(addCard.getUserId());
            return new ResponseEntity<>(collection, HttpStatus.OK);
        } catch (DaoException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/remove-all")
    public ResponseEntity<Integer> removeAllCardsOfTypeFromCollection(@RequestBody AdjustCardRequestDto removeAll) {
        try {
            CardCollection collection = collectionDao.getCollectionByUserId(removeAll.getUserId());
            collectionDao.removeAllCardsOfTypeFromCollection(removeAll.getCard().getCardId(), collection.getCollectionId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (DaoException e) {
            return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/remove")
    public ResponseEntity<Integer> removeCardsFromCollection(@RequestBody AdjustCardRequestDto removeCard) {
        try {
            collectionDao.removeCardsFromCollection(removeCard.getCard(), collectionDao.getCollectionByUserId(removeCard.getUserId()), removeCard.getQuantity());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (DaoException e) {
            return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}/set-public")
    public ResponseEntity<Integer> setCollectionPublic(@PathVariable int id) {
        try {
            int numberOfRows = collectionDao.setCollectionPublic(id);
            return new ResponseEntity<>(numberOfRows, HttpStatus.OK);
        } catch (DaoException e) {
            return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}/set-private")
    public ResponseEntity<Integer> setCollectionPrivate(@PathVariable int id) {
        try {
            int numberOfRows = collectionDao.setCollectionPrivate(id);
            return new ResponseEntity<>(numberOfRows, HttpStatus.OK);
        } catch (DaoException e) {
            return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}/rename")
    public ResponseEntity<Integer> renameCollection(@PathVariable int id,  @RequestParam String collectionName) {
        try {
            int numberOfRows = collectionDao.renameCollection(id, collectionName);
            return new ResponseEntity<>(numberOfRows, HttpStatus.OK);
        } catch (DaoException e) {
            return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}/set-thumbnail")
    public ResponseEntity<Integer> setCollectionThumbnail(@PathVariable int id, @RequestBody ThumbnailUrlDto thumbnailUrl) {
        try {
            int numberOfRows = collectionDao.setCollectionThumbnail(id, thumbnailUrl.getThumbnailUrl());
            return new ResponseEntity<>(numberOfRows, HttpStatus.OK);
        } catch (DaoException e) {
            return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("permitAll()")
    @GetMapping("{id}/get-thumbnail")
    public ResponseEntity<String> getCollectionThumbnail(@PathVariable int id) {
        try {
            return new ResponseEntity<>(collectionDao.getCollectionById(id).getThumbnailUrl(), HttpStatus.OK);
        } catch (DaoException e) {
            return new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}








