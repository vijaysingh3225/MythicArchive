<template>
  <div class="collections-details">
    <h1 class="collection-name" style="color:var(--onyx)">{{ collection.collectionName }}</h1>
    <div class="collection-controls" v-if="this.$route.params.id == this.$store.state.user.id">
      <button id="btn-show-name-input" @click="showNameInput = true">Rename Collection</button>
      <button id="btn-publish" @click="setPublic" v-if="!collection.isPublic">Publish Collection</button>
      <button id="btn-unpublish" @click="setPrivate" v-if="collection.isPublic">Unpublish Collection</button>
    </div>
    <div class="collection-name-input" v-if="showNameInput === true">
      <button id="btn-close-name-input" @click="showNameInput = false">x</button>
      <div class="name-input">
        <input type="text" id="txt-nameInput" placeholder="Enter new collection name">
        <button id="btn-name-submit" @click="rename">Submit</button>
      </div>
    </div>
    <div class="searchBox">
      <div class="field">
        <input type="text" id="searchTerm" name="searchTerm" @keyup="searchCollection()" />
        <button @click="searchCollection()">
          Search<img src="/images/SearchIcon.png" class="search-icon" />
        </button>
      </div>
    </div>
    <div class="content-container">
      <div class="left-container">
        <div class="header-stats">
          <div class="pie-chart-container stat-item">
            <canvas id="pieChart"></canvas>
          </div>
          <div class="stat-item value">
            <h3>Total Value</h3>
            <h3>${{ this.collectionStats.totalCollectionPrice }}</h3>
            <p>There {{ this.collectionStats.cardTypesWithoutPrice == 1 ? 'is' : 'are' }} <strong>{{
              this.collectionStats.cardTypesWithoutPrice }}</strong> {{ this.collectionStats.cardTypesWithoutPrice == 1
    ? 'card' : 'cards' }} in this
              collection without price data</p>
          </div>
          <div class="stat-item">
            <h2>Total Cards: {{ this.collection.totalCards }}</h2>
          </div>
        </div>
        <div class="card-container" v-if="this.displayedCards.length > 0">
          <div class="card" v-for="card in this.displayedCards" v-bind:key="card.cardId">
            <router-link class="router-link" v-bind:to="{ name: 'cardDetails', params: { id: card.cardId } }">
              <img :src="card.imageUrl" />
            </router-link>
          </div>
        </div>
      </div>
      <div class="right-container">
        <h2 class="collection-stats">Collection Statistics</h2>
        <div class="">
          <h3>Sets</h3>
          <ul>
            <li v-for="stat in this.collectionStats.setNameCounts" :key="stat.setName">
              {{ stat.setName }}: {{ stat.count }}
            </li>
          </ul>
        </div>
        <div class="stats-row">
          <div class="">
            <h3>Card Types</h3>
            <ul>
              <li v-for="stat in this.collectionStats.cardTypeCounts" :key="stat.cardType">
                {{ stat.cardType }} : {{ stat.count }}
              </li>
            </ul>
          </div>
          <div class="">
            <h3>Colors</h3>
            <ul>
              <li v-for="stat in this.collectionStats.cardColorCounts" :key="stat.cardColor">
                {{ stat.cardColor }}: {{ stat.count }}
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Chart from "chart.js/auto";
import CollectionService from "../services/CollectionService";

export default {
  data() {
    return {
      showNameInput: false,
      displayedCards: [],
      collectionStats: {},
      collection: {
        cardCount: 0,
        collectionName: "",
        ownerId: 0,
        thumbnailUrl: "",
        username: "",
        cards: [],
      },
      chartInstance: null,
    };
  },

  methods: {
    rename() {
      let collectionName = document.getElementById('txt-nameInput').value;
      CollectionService.renameCollection(this.$route.params.id, collectionName)
        .then((response) => {
          this.collection.collectionName = collectionName;
          this.showNameInput = false;
        })
        .catch((error) => {
          console.error("Error fetching data:", error);
        });
    },
    getCollectionStats() {
      CollectionService.getCollectionStats(this.$route.params.id)
        .then((response) => {
          this.collectionStats = response.data;
        })
        .catch((error) => {
          console.error("Error fetching data:", error);
        });
    },
    setPrivate() {
      CollectionService.setCollectionPrivate(this.$route.params.id)
        .then((response) => {
          this.collection.isPublic = false;
        })
        .catch((error) => {
          console.error("Error fetching data:", error);
        });
    },
    setPublic() {
      CollectionService.setCollectionPublic(this.$route.params.id)
        .then((response) => {
          this.collection.isPublic = true;
        })
        .catch((error) => {
          console.error("Error fetching data:", error);
        });
    },
    getCollectionById() {
      CollectionService.getCollectionById(this.$route.params.id)
        .then((response) => {
          this.collection = response.data;
          this.getCardsInCollection();
        })
        .catch((error) => {
          console.error("Error fetching data:", error);
        });
    },
    getCardsInCollection() {
      CollectionService.getCardsInCollection(this.$route.params.id)
        .then((response) => {
          this.collection.cards = response.data;
          this.displayedCards = this.collection.cards;
        })
        .catch((error) => {
          console.error("Error fetching data:", error);
        });
    },
    searchCollection() {
      let search = document.getElementById('searchTerm').value;
      let tempArray = this.collection.cards.filter((card) => {
        return card.cardName.toLowerCase().includes(search.toLowerCase()) ||
          card.cardType.toLowerCase().includes(search.toLowerCase()) ||
          card.rarity.toLowerCase().includes(search.toLowerCase()) ||
          card.setName.toLowerCase().includes(search.toLowerCase());
      });
      this.displayedCards = tempArray;
    },
    createPieChart() {
      if (this.collectionStats && this.collectionStats.rarityCounts && this.collectionStats.rarityCounts.length > 0) {
        const labels = this.collectionStats.rarityCounts.map(stat => stat.rarity);
        const data = this.collectionStats.rarityCounts.map(stat => stat.count);
        const backgroundColors = this.collectionStats.rarityCounts.map(() => '#1e7a76');

        const ctx = document.getElementById('pieChart').getContext('2d');

        const canvas = document.getElementById('pieChart');
        const aspectRatio = canvas.width / canvas.height;
        canvas.width = 400;
        canvas.height = canvas.width / aspectRatio;

        const devicePixelRatio = window.devicePixelRatio || 1;
        canvas.style.width = '100%';
        canvas.style.height = 'auto';

        const width = canvas.offsetWidth * devicePixelRatio;
        const height = canvas.offsetHeight * devicePixelRatio;

        canvas.width = width;
        canvas.height = height;

        if (this.chartInstance) {
          this.chartInstance.destroy();
        }

        this.chartInstance = new Chart(ctx, {
          type: 'pie',
          data: {
            labels: labels,
            datasets: [{
              data: data,
              backgroundColor: [
                '#D5D5D5',
                "#000000",
                '#AF1616',
                '#C9C914',
              ],
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: true,
            layout: {
              padding: {
                bottom: 0,
              },
            },
            plugins: {
              legend: {
                position: 'left',
                align: 'center',
                labels: {
                  boxWidth: 10,
                  padding: 10,
                }
              }
            }
          },
        });
      } else {
        const ctx = document.getElementById('pieChart').getContext('2d');
        const canvas = document.getElementById('pieChart');
        const aspectRatio = canvas.width / canvas.height;
        canvas.width = 400;
        canvas.height = canvas.width / aspectRatio;

        const devicePixelRatio = window.devicePixelRatio || 1;
        const width = canvas.offsetWidth * devicePixelRatio;
        const height = canvas.offsetHeight * devicePixelRatio;

        canvas.width = width;
        canvas.height = height;

        if (this.chartInstance) {
          this.chartInstance.destroy();
        }
        this.chartInstance = new Chart(ctx, {
          type: 'pie',
          data: {
            labels: ['No data available'],
            datasets: [{
              data: [100],
              backgroundColor: ['#ddd'],
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                position: 'left',
                align: 'start',
                labels: {
                  boxWidth: 10,
                  padding: 10,
                }
              }
            }
          },
        });
      }
    }
  },
  watch: {
    'collectionStats.rarityCounts': function (newVal) {
      if (newVal && newVal.length > 0) {
        this.createPieChart();
      }
    },
  },

  created() {
    this.getCollectionById();
    this.getCollectionStats();
  },

  mounted() {
    this.$store.commit("SET_FROM_SHUFFLE", false);
    if (this.collectionStats.rarityCounts && this.collectionStats.rarityCounts.length > 0) {
      this.createPieChart();
    }
  },
};
</script>

<style scoped>
.collection-controls {
  display: flex;
  justify-content: space-evenly;
  width: 20%;
  margin: auto;
  gap: 20px;
  padding: 10px;
}

.content-container {
  display: flex;
  width: 100%;
  /* border: 2px solid green; */
}

button {
  padding: 10px;
}

.left-container {
  width: 75%;
  margin: 20px;
  /* border: 2px solid rgb(255, 0, 221); */

}

h2,
h1,
h3 {
  margin: 0;
}

.right-container {
  display: flex;
  flex-direction: column;
  /* border: 2px solid red; */
  align-items: left;
  width: 20%;
  margin: 20px;
  background-color: #444444;
  color: white;
  border-radius: 20px;
  padding-top: 40px;
  padding-left: 40px;
}

.header-stats {
  width: 100%;
  height: 200px;
  display: flex;
  /* border: 2px solid blue; */
  justify-content: space-evenly;
  align-items: center;
  background-color: #444444;
  color: white;
  border-radius: 20px;

}

.stat-item {
  width: 20%;
  height: 80%;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 0;
}

.pie-chart-container {
  height: 250px;
  min-height: 150px;
  min-width: 150px;
  margin: 0;
}

.card-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-evenly;
  align-items: center;
  row-gap: 20px;
  width: 100%;

}
.collection-name{
  margin-top: 20px;
}

.collection-name-input {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 30%;
  padding: 30px;
  background-color: var(--platinum);
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  z-index: 1000;
}

.name-input {
  display: flex;
  align-content: center;
  justify-content: space-between;
}

input[type="text"] {
  width: 80%;
}

#btn-close-name-input {
  position: absolute;
  top: 1%;
  right: 1%;
  font-weight: bold;
  color: var(--perry);
  background: transparent;
}

@media screen and (max-width: 1000px) {

  .stat-item,
  .right-container {
    font-size: 1.2vw;
    padding-left: 0;
  }
}</style>