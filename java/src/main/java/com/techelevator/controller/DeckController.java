package com.techelevator.controller;

import com.techelevator.dao.CardDao;
import com.techelevator.dao.CollectionDao;
import com.techelevator.dao.DeckDao;
import com.techelevator.dao.UserDao;
import com.techelevator.exception.DaoException;
import com.techelevator.model.AdjustCardRequestDto;
import com.techelevator.model.Card;
import com.techelevator.model.Deck;
import com.techelevator.model.DeckDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/decks")
//@PreAuthorize("isAuthenticated()")
@CrossOrigin
public class DeckController {
    private final CardDao cardDao;
    private final UserDao userDao;
    private final DeckDao deckDao;
    public DeckController(UserDao userDao, DeckDao deckDao, CardDao cardDao) {
        this.userDao = userDao;
        this.deckDao = deckDao;
        this.cardDao = cardDao;
    }

//    @PreAuthorize("permitAll()")
    @RequestMapping(value = "/all-public", method = RequestMethod.GET)
    public List<Deck> getAllPublicDecks() {
        return deckDao.getAllPublicDecks();
    }

//    @PreAuthorize("permitAll()")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Deck getDeckById(@PathVariable int id) {
        try {
            Deck deck;
            deck =  deckDao.getDeckById(id);
            if (deck == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find specified deck.");
            }
            return deck;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("permitAll()")
    @GetMapping("/{id}/cards")
    public List<Card> getCardsInDeck(@PathVariable int id) {
        try {
            List<Card> cards = deckDao.getCardsInDeck(id);
            return cards;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PreAuthorize("permitAll()")
    @GetMapping("/user/{id}")
    public int getDeckIdByUser(@PathVariable int id) {
        try {

            int deckId = deckDao.getDeckByUserId(id).getDeckId();
            return deckId;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/create")
    public ResponseEntity<Deck> createNewDeck(@RequestBody DeckDto deck) {
        try {
            Deck createdDeck = deckDao.createNewDeck(deck);
            return new ResponseEntity<>(createdDeck, HttpStatus.CREATED);
        } catch (DaoException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/add")
    public ResponseEntity<Deck> addCardToDeck(@RequestBody AdjustCardRequestDto addCard) {
        try {

            Deck deck = deckDao.getDeckByUserId(addCard.getUserId());
            Card cardToAdd = cardDao.getCardById(addCard.getCard().getCardId());
            //If SELECT statement returns null, add card to cards database
            if (cardToAdd == null) {
                cardToAdd = cardDao.createNewCard(addCard.getCard());
            }
            //If card is already in deck, call function to update database
            if (deckDao.isCardInDeck(addCard.getCard().getCardId(), deck.getDeckId())) {
                deckDao.addExistingCardToDeck(cardToAdd, deck, addCard.getQuantity());
            } //else call function to insert new record
            else {
                deckDao.addCardsToDeck(cardToAdd, deck, addCard.getQuantity());
            }
            deck = deckDao.getDeckByUserId(addCard.getUserId());
            return new ResponseEntity<>(deck, HttpStatus.OK);
        } catch (DaoException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/remove-all")
    public ResponseEntity<Integer> removeAllCardsOfTypeFromDeck(@RequestBody AdjustCardRequestDto removeAll) {
        try {
            Deck deck = deckDao.getDeckByUserId(removeAll.getUserId());
            deckDao.removeAllCardsOfTypeFromDeck(removeAll.getCard().getCardId(), deck.getDeckId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (DaoException e) {
            return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/remove")
    public ResponseEntity<Integer> removeCardsFromDeck(@RequestBody AdjustCardRequestDto removeCard) {
        try {
            deckDao.removeCardsFromDeck(removeCard.getCard(), deckDao.getDeckByUserId(removeCard.getUserId()), removeCard.getQuantity());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (DaoException e) {
            return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}/set-public")
    public ResponseEntity<Integer> setDeckPublic(@PathVariable int id) {
        try {
            int numberOfRows = deckDao.setDeckPublic(id);
            return new ResponseEntity<>(numberOfRows, HttpStatus.OK);
        } catch (DaoException e) {
            return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}/set-private")
    public ResponseEntity<Integer> setDeckPrivate(@PathVariable int id) {
        try {
            int numberOfRows = deckDao.setDeckPrivate(id);
            return new ResponseEntity<>(numberOfRows, HttpStatus.OK);
        } catch (DaoException e) {
            return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}/rename")
    public ResponseEntity<Integer> renameDeck(@PathVariable int id,  @RequestParam String deckName) {
        try {
            int numberOfRows = deckDao.renameDeck(id, deckName);
            return new ResponseEntity<>(numberOfRows, HttpStatus.OK);
        } catch (DaoException e) {
            return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}/set-thumbnail")
    public ResponseEntity<Integer> setDeckThumbnail(@PathVariable int id, String thumbnail_url) {
        try {
            int numberOfRows = deckDao.setDeckThumbnail(id, thumbnail_url);
            return new ResponseEntity<>(numberOfRows, HttpStatus.OK);
        } catch (DaoException e) {
            return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
