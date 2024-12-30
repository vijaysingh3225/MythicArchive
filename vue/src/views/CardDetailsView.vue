<template>
  <div class="page-container">
    <div class="card-container">
      <img
        :src="
          card.image_uris
            ? card.image_uris?.large
            : card.card_faces
            ? card.card_faces[0].image_uris?.large
            : ''
        "
        :alt="card.name"
      />
      <div class="card-price">
        ${{ card.hasOwnProperty("prices.usd") ? card.prices.usd : "---" }}
      </div>
      <div class="card-footer">
        <button
          @click="removeFromCollection"
          class="plus-minus"
          :title="minusButtonText"
          :disabled="this.$store.state.token == ''"
        >
          -
        </button>
        <div class="card-count">{{ this.$store.state.token != '' ? this.cardCount : 0 }}</div>
        <button
          @click="addToCollection"
          class="plus-minus"
          :title="plusButtonText"
          :disabled="this.$store.state.token == ''"
        >
          +
        </button>
      </div>
      <div class="collection-thumbnail">
        <button
          @click="setCollectionThumbnail"
          id="btn-set-thumbnail"
          class="set-image"
          title="Set as thumbnail"
          v-if="this.$store.state.token != '' && !this.isCollectionThumbnail"
        >
          Set collection thumbnail
        </button>
        <p id="current-thumbnail-message" v-if="this.$store.state.token != '' && this.isCollectionThumbnail">
          This card is your collection's current thumbnail
        </p>
      </div>
    </div>

    <div class="details-container">
      <div class="details-header">
        <h2>{{ card.name }}</h2>
        <img @click="closeDetails" class="close" src="/images/BackArrow.png"/>
      </div>
      <div class="stat-container">
        <div class="stat">
          <h3>Oracle Text</h3>
          <span v-html="formattedOracleText"></span>
        </div>
        <div class="stat">
          <h3>Set</h3>
          <span>{{ card.set_name }}</span>
        </div>
        <div class="stat">
          <h3>Mana Cost</h3>
          <div v-html="formattedManaCost"></div>
        </div>
        <div class="stat">
          <h3>Type</h3>
          <span>{{ card.type_line }}</span>
        </div>
        <div class="stat">
          <h3>Rarity</h3>
          <span>{{ card.rarity }}</span>
        </div>
        <div class="stat">
          <h3>Flavor Text</h3>
          <span>{{ card.flavor_text || "----" }}</span>
        </div>
      </div>
      <div class="format-container">
        <div
          v-for="(status, format) in card.legalities"
          :key="format"
          :class="{
            'legal legality': status === 'legal',
            'not-legal legality': status === 'not_legal',
            'banned legality': status === 'banned',
          }"
        >
          {{ format }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import SearchService from "../services/SearchService";
import CollectionService from "../services/CollectionService";

export default {
  name: "CardDetailsView",
  data() {
    return {
      card: {},
      cardCount: "0",
      collectionThumbnail: "",
      cardThumbnail: ""
    };
  },
  computed: {
    isCollectionThumbnail() {
      return this.cardThumbnail.toLowerCase() === this.collectionThumbnail.toLowerCase();
    },
    plusButtonText() {
      if (this.$store.state.token == "") {
        return "You must be logged in to add a card to your collection";
      } else {
        return "Add to collection";
      }
    },
    minusButtonText() {
      if (this.$store.state.token == "") {
        return "You must be logged in to remove a card from your collection";
      } else {
        return "Remove from collection";
      }
    },
    formattedManaCost() {
      let manaCost = "";
      if (!this.card.mana_cost) {
        if (this.card.card_faces) {
          manaCost = this.card.card_faces[0].mana_cost
            .concat(" , ")
            .concat(this.card.card_faces[1].mana_cost);
        }
      } else {
        manaCost = this.card.mana_cost;
      }
      if (manaCost.includes("//")) {
        manaCost = manaCost.replace(/\/\//g, " & ");
      } else if (manaCost.includes("/")) {
        manaCost = manaCost.replace(/\//g, "} / {");
      }
      return manaCost.replace(/{(\w)}/g, (match, symbol) => {
        return `<img src="/Images/MTGMana/{${symbol}}.svg" alt="${symbol}" style="height:30px; margin:0 2px;" />`;
      });
    },
    formattedOracleText() {
      if (!this.card.oracle_text) {
        return "";
      }
      return this.card.oracle_text.replace(/{(\w)}/g, (match, symbol) => {
        return `<img src="/Images/MTGMana/{${symbol.replace(
          "/",
          ""
        )}}.svg" alt="${symbol}" style="height:16px; margin:0 2px;" />`;
      });
    },
  },
  methods: {
    changeText() {
      if (this.$store.state.token == "") {
        this.buttonText = "Not Available"; // Change text on hover when disabled
      }
    },
    resetText() {
      if (this.isDisabled) {
        this.buttonText = "+/-"; // Reset text when hover ends
      }
    },
    closeDetails() {
      if(this.$store.state.isFromShuffle) {
        this.$router.go(-2);
      } else {
        this.$router.go(-1);
      }
    },
    getCardColors(card) {
      const colors = card.colors
        ? card.colors
        : card.card_faces
        ? card.card_faces[0].colors.concat(card.card_faces[1].colors)
        : [];
      let colorString = "";
      for (const color of colors) {
        colorString = colorString.concat(color);
      }
      if (colorString.length == 0) {
        colorString = "Colorless";
      }
      return colorString;
    },
    async addToCollection() {
      await this.$store.dispatch("addToCollection", {
        card: this.card,
        cardColor: this.getCardColors(this.card),
        quantity: 1,
      });
      this.refreshCardCount();
    },
    async removeFromCollection() {
      await this.$store.dispatch("removeFromCollection", {
        card: this.card,
        cardColor: this.getCardColors(this.card),
        quantity: 1,
      });
      this.refreshCardCount();
    },
    getCollectionThumbnail() {
      CollectionService.getCollectionThumbnail(this.$store.state.user.collectionId)
        .then((response) => {
          this.collectionThumbnail = response.data;
        })
        .catch((error) => {
          console.error("Error fetching data:", error);
        });
    },
    refreshCardCount() {
      CollectionService.getCardCount(
        this.$store.state.user.collectionId,
        this.$route.params.id
      )
        .then((response) => {
          this.cardCount = response.data;
        })
        .catch((error) => {
          console.error("Error fetching data:", error);
        });
    },
    setCollectionThumbnail() {
        const thumbnailUrlDto = { thumbnailUrl: this.cardThumbnail };
        CollectionService.setCollectionThumbnail(
          this.$store.state.user.collectionId,
          thumbnailUrlDto
        )
        .then((response) => {
          this.collectionThumbnail = this.cardThumbnail;
        })
        .catch ((error) => {
          console.error("Error fetching data:", error);
        });
    },
    setCardThumbnail() {
      this.cardThumbnail = this.card.image_uris
        ? this.card.image_uris?.art_crop
        : this.card.card_faces
        ? this.card.card_faces[0].image_uris?.art_crop
        : "";
    }
  },
  created() {
    SearchService.searchById(this.$route.params.id).then((response) => {
      this.card = response.data;
      this.cardThumbnail = this.card.image_uris
        ? this.card.image_uris?.art_crop
        : this.card.card_faces
        ? this.card.card_faces[0].image_uris?.art_crop
        : "";
    });
    if (this.$store.state.token != '') {
      this.refreshCardCount();
      this.getCollectionThumbnail();
    }
  }
};
</script>

<style scoped>
.page-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  height: 100%;
  margin-top: 50px;
  background-color: var();
}

.card-container,
.details-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  height: 90%;
  margin: 0;
  gap: 20px;
}

.card-container {
  min-width: 400px;
  width: 30%;
  max-width: 700px;
  margin-bottom: 20px;
}

.details-container {
  min-width: 700px;
  width: 45%;
  max-width: 1100px;
  height: auto;
  border-radius: 10px;
  display: flex;
  justify-content: space-between;
  color: var(--platinum);
  background-color: var(--onyx);
  padding: 30px;
}

.card-container img {
  width: 70%;
  height: auto;
  border-radius: 15px;
  margin: 0;
}

.card-footer {
  display: flex;
  align-items: center;
}

#current-thumbnail-message {
  color: var(--perry);
  font-style: italic;
}

.plus-minus {
  height: 40px;
  width: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  cursor: pointer;
  border-radius: 5px;
  background-color: var(--perry);
  color: var(--platinum);
  opacity: 0.6;
}

.plus-minus:hover {
  opacity: 1;
}

.plus-minus:disabled {
  background-color: var(--onyx);
  cursor: not-allowed;
}

.plus-minus:disabled:hover {
  opacity: 0.6;
}

#btn-set-thumbnail {
  color: var(--platinum);
  height: 30px;
  background-color: var(--perry);
  opacity: 0.6;
}

#btn-set-thumbnail:hover {
  opacity: 1;
}

.card-price,
.card-count {
  font-size: 1.8rem;
  color: var(--onyx);
  margin: 0 30px;
}

.details-header {
  display: flex;
  width: 90%;
  justify-content: space-between;
  align-items: center;
}

.format-container {
  height: auto;
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  justify-content: flex-start;
  align-items: flex-start;
  width: 95%;
  margin-bottom: 10px;
}

.legality {
  padding: 6px;
  height: 30px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  font-size: 1rem;
}

.legal {
  background-color: rgb(60, 117, 60);
}

.not-legal {
  background-color: rgb(109, 59, 59);
}

.banned {
  background-color: rgb(88, 88, 88);
}

h3 {
  margin: 0;
}

.stat {
  display: inline-flex;
  flex-direction: column;
  align-items: flex-start;
  border-radius: 8px;
  padding: 15px;
  gap: 5px;
  width: auto;
  height: auto;
  min-height: 70px;
  max-width: 400px;
  min-width: 150px;
  margin: 0;
}

.stat-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: space-between;
  align-items: flex-start;
  height: 60%;
  flex-wrap: wrap;
  max-width: 90%;
}
.close {
  height: 25px;
  cursor: pointer;
}
.close:hover {
  transform: scale(1.1);
}

@media (max-width: 768px) {
  .details-container {
    width: 90%;
    min-width: 0;
  }

  .stat-container {
    min-width: 0;
    width: 90%;
  }
}
</style>
