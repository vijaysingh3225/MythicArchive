BEGIN;

-- Drop tables in the correct order to avoid foreign key conflicts
DROP TABLE IF EXISTS cards_decks;
DROP TABLE IF EXISTS cards_collections;
DROP TABLE IF EXISTS decks;
DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS collections;
DROP TABLE IF EXISTS users;

-- Create the users table
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Create the collections table
CREATE TABLE collections (
    collection_id SERIAL PRIMARY KEY,
    collection_name VARCHAR(50) NOT NULL,
    user_id INT NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    thumbnail_url VARCHAR(2083) DEFAULT 'images/CardBack.jpg',
    CONSTRAINT FK_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Create the cards table
CREATE TABLE cards (
    card_id UUID PRIMARY KEY,
    card_name VARCHAR(250) NOT NULL,
    card_type VARCHAR(250) NOT NULL,
    card_color VARCHAR(100) NOT NULL DEFAULT 'Colorless',
    mana_cost VARCHAR(100) NOT NULL,
    rarity VARCHAR(100) NOT NULL,
    price DECIMAL DEFAULT 0.00,
    set_name VARCHAR (250) NOT NULL,
    image_url VARCHAR(2083) NOT NULL,
    thumbnail_url VARCHAR(2083) NOT NULL
);

-- Create the cards_collections table
CREATE TABLE cards_collections (
    card_id UUID NOT NULL,
    collection_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    PRIMARY KEY (card_id, collection_id),
    CONSTRAINT FK_cards_collections_cards FOREIGN KEY (card_id) REFERENCES cards(card_id),
    CONSTRAINT FK_cards_collections_collections FOREIGN KEY (collection_id) REFERENCES collections(collection_id),
    CONSTRAINT CK_quantity CHECK (quantity >= 0)
);

-- Create the decks table
CREATE TABLE decks (
    deck_id SERIAL PRIMARY KEY,
    deck_name VARCHAR(50) NOT NULL,
    user_id INT NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    thumbnail_url VARCHAR(2083) DEFAULT 'images/CollectionDefault.png',
    deckFormat VARCHAR(150) DEFAULT '',
    CONSTRAINT FK_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Create the cards_decks table
CREATE TABLE cards_decks (
    card_id UUID NOT NULL,
    deck_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    PRIMARY KEY (card_id, deck_id),
    CONSTRAINT FK_cards_decks_cards FOREIGN KEY (card_id) REFERENCES cards(card_id),
    CONSTRAINT FK_cards_decks_decks FOREIGN KEY (deck_id) REFERENCES decks(deck_id),
    CONSTRAINT CK_quantity CHECK (quantity >= 0)
);

-- Commit the transaction
COMMIT;

