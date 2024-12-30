import { createStore as _createStore } from "vuex";
import axios from "axios";
import CollectionService from "../services/CollectionService";

export function createStore(currentToken, currentUser) {
  let store = _createStore({
    state: {
      token: currentToken || "",
      user: currentUser || {},
      request: {
        card: {
          cardId: "",
          cardName: "",
          cardColor: "",
          manaCost: "",
          rarity: "",
          price: "",
          setName: "",
          thumbnailUrl: "",
          imageUrl: "",
        },
        userId: 0,
        quantity: 0,
        isFromShuffle: false
      }
    },
    mutations: {
      BUILD_REQUEST(state, payload) {
        const { card, cardColor, quantity } = payload;
        state.request.userId = state.user.id;
        state.request.quantity = quantity;
        //Set card properties
        state.request.card.cardId = card.id;
        state.request.card.cardName = card.name;
        state.request.card.cardType = card.type_line;
        state.request.card.cardColor = cardColor;
        state.request.card.manaCost = card.mana_cost
          ? card.mana_cost
          : card.card_faces
          ? card.card_faces[0].mana_cost || "" || card.card_faces[1].mana_cost
          : "";
        state.request.card.rarity =
          card.rarity === "bonus" ? "mythic" : card.rarity;
        state.request.card.price = card.prices.usd ? card.prices.usd : -1.0;
        state.request.card.setName = card.set_name;
        state.request.card.thumbnailUrl = card.image_uris
          ? card.image_uris?.art_crop
          : card.card_faces
          ? card.card_faces[0].image_uris?.art_crop
          : "";
        state.request.card.imageUrl = card.image_uris
          ? card.image_uris?.large
          : card.card_faces
          ? card.card_faces[0].image_uris?.large
          : "";
      },
      SET_AUTH_TOKEN(state, token) {
        state.token = token;
        localStorage.setItem("token", token);
        axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
      },
      SET_USER(state, user) {
        state.user = user;
        localStorage.setItem("user", JSON.stringify(user));
      },
      SET_COLLECTION_ID(state, collectionId) {
        state.collectionId = collectionId;
      },
      LOGOUT(state) {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        state.token = "";
        state.user = {};
        axios.defaults.headers.common = {};
      },
      SET_FROM_SHUFFLE(state, value) {
        state.isFromShuffle = value;
      }
    },
    actions: {
      async addToCollection({ commit }, payload) {
        this.commit("BUILD_REQUEST", payload);
        try {
          await CollectionService.addCardToCollection(this.state.request);
        } catch (error) {
          console.error("Error fetching data:", error);
        }
      },
      async removeFromCollection({ commit }, payload) {
        this.commit("BUILD_REQUEST", payload);
        try {
          await CollectionService.removeCardFromCollection(this.state.request);
        } catch (error) {
          console.error("Error fetching data:", error);
        }
      },
    },
  });
  return store;
}
