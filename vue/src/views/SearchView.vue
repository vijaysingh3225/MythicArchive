<template>
  <div class="home">
    <h1 class="search-title">Search</h1>
    <div class="searchBox">
      <div class="field">
        <input
          type="text"
          id="searchTerm"
          name="searchTerm"
          v-model="searchTerm"
        />
        <button @click="search">
          Search<img src="/images/SearchIcon.png" class="search-icon" />
        </button>
      </div>
    </div>
    <div class="page-control" v-if="totalPages > 1">
      <img
        class="page-buttons"
        :style="{ opacity: backButtonOpacity }"
        @click="prevPage"
        src="/images/BackArrowPerry.png"
      />
      <div>Page {{ currentPage }} of {{ totalPages }}</div>
      <img
        class="page-buttons"
        :style="{ opacity: forwardButtonOpacity }"
        @click="nextPage"
        src="/images/ForwardArrowPerry.png"
      />
    </div>
    <div class="card-container-search" v-if="paginatedCards.length > 0">
      <div class="card" v-for="card in paginatedCards" :key="card.id">
        <router-link
          class="router-link"
          :to="{ name: 'cardDetails', params: { id: card.id } }"
        >
          <img :src="card.imageUrl" />
        </router-link>
      </div>
    </div>
  </div>
  <div class="page-control" v-if="totalPages > 1">
    <img
      class="page-buttons"
      :style="{ opacity: backButtonOpacity }"
      @click="prevPage"
      src="/images/BackArrowPerry.png"
    />
    <div id="txt-page-control">Page {{ currentPage }} of {{ totalPages }}</div>
    <img
      class="page-buttons"
      :style="{ opacity: forwardButtonOpacity }"
      @click="nextPage"
      src="/images/ForwardArrowPerry.png"
    />
  </div>
</template>

<script>
import SearchService from "../services/SearchService";

export default {
  data() {
    return {
      searchTerm: "",
      cards: [],
      currentPage: 1,
      itemsPerPage: 40,
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.cards.length / this.itemsPerPage);
    },
    paginatedCards() {
      const startIndex = (this.currentPage - 1) * this.itemsPerPage;
      return this.cards.slice(startIndex, startIndex + this.itemsPerPage);
    },
    backButtonOpacity() {
      if (this.currentPage === 1) {
        return 0.3;
      } else {
        return 0.6;
      }
    },
    forwardButtonOpacity() {
      if (this.currentPage === this.totalPages) {
        return 0.3;
      } else {
        return 0.6;
      }
    }
  },
  methods: {
    search() {
      SearchService.search(this.searchTerm)
        .then((response) => {
          this.cards = response.data.data.map((card) => ({
            id: card.id,
            name: card.name,
            imageUrl: card.image_uris
              ? card.image_uris.normal || ""
              : card.card_faces
              ? card.card_faces[0].image_uris.normal || ""
              : "",
          }));
          this.currentPage = 1;
        })
        .catch((error) => {
          console.error("Error fetching data:", error);
        });
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage -= 1;
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage += 1;
      }
    },
  },
  mounted() {
    this.$store.commit("SET_FROM_SHUFFLE", false);
    this.search();
  },
};
</script>

<style>
.router-link {
  text-decoration: none;
}

.card-container-search {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-evenly;
  align-items: center;
  row-gap: 20px;
  width: 80%;
  margin: 50px auto;
}

.card {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 18%;
  margin: 10px;
  min-width: 100px;
  text-decoration: none;
  transition: transform 0.1s ease-in-out;
}

.card p {
  width: 80%;
  height: 20px;
  margin: 5px auto;
  text-align: center;
  font-size: 1.2vw;
}

.card img {
  border-radius: 13px;
  width: 100%;
  transition: transform 0.1s ease-in-out;
  transition: box-shadow 0.5s ease;
}

.card img:hover {
  transform: scale(1.02);
  transition: transform 0.1s ease-in-out;
  border-radius: 13px;
  box-shadow: 0px 0px 20px 10px #0e0e0e;
  transition: box-shadow 0.3s ease;
  cursor: pointer;
}

.field {
  margin: 40px auto;
  width: 30%;
  display: flex;
  justify-content: space-evenly;
}

input {
  width: 80%;
  min-width: 200px;
  height: 30px;
  border-radius: 2px;
  border: 0px;
  background-color: var(--platinum);
  border: 1px solid var(--perry);
}

button {
  border: 0px;
  border-radius: 2px;
  background-color: var(--perry);
  display: flex;
  align-items: center;
  color: var(--platinum);
}

button:hover {
  cursor: pointer;
}

button:active {
  background-color: var(--darker-perry);
}

.search-icon {
  width: 13px;
  margin-left: 5px;
}
.page-control {
  width: 90%;
  height: 60px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 30px;
  color: var(--onyx);
  font-size:1.3rem;
}
.page-buttons {
  height: 25px;
}

.page-buttons:hover {
  height: 25px;
  cursor: pointer;
  transform: scale(1.1);
}

.search-title {
  color: var(--onyx);
}
</style>
