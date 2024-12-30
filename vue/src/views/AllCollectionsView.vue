<template>
  <div>
    <h1>Collections</h1>
    <div class="collection-container">
      <div
        class="collection-object"
        v-for="collection in collections"
        v-bind:key="collection.ownerId"
      >
        <router-link
          class="router-link"
          v-bind:to="{
            name: 'collectionDetails',
            params: { id: collection.collectionId },
          }"
        >
          <img class="thumbnail" :src="collection.thumbnailUrl" />
          <div class="collection-data">
            <p id="collection-data-name">{{ collection.collectionName }}</p>
            <p id="collection-data-owner">{{ collection.ownerName }}</p>
            <p id="collection-data-card-total">Total Cards: {{ collection.totalCards }}</p>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import AuthService from "../services/AuthService";
import CollectionService from "../services/CollectionService";

export default {
  data() {
    return {
      collections: [],
    };
  },

  methods: {
    getAllPublicCollections() {
      CollectionService.getAllPublicCollections()
        .then((response) => {
          console.log(response.data);
          this.collections = response.data;
        })
        .catch((error) => {
          console.error("Error fetching data:", error);
      });
    },
    getUsernameById(id) {
      AuthService.getUsernameByUserId(id)
        .then((response) => {
          return response.data;
        })
        .catch((error) => {
          console.error("Error fetching data:", error);
        });
    },
  },
  mounted() {
    this.$store.commit("SET_FROM_SHUFFLE", false);
    this.getAllPublicCollections();
  },
};
</script>

<style scoped>
.collection-container {
  display: flex;
  margin: auto;
  width: 70%;
  justify-content: space-evenly;
  flex-wrap: wrap;
  row-gap: 20px;
}

.collection-object {
  width: 18%;
  min-width: 200px;
  background-color: var(--perry);
}

.thumbnail {
  width: 100%;
  height: 200px;
}

.collection-data {
  text-align: center;
  display: flex;
  flex-direction: column;
  height: 70px;
  justify-content: space-evenly;
  background-color: var(--darker-perry);
  padding-bottom: 10px;
  text-decoration: none;
  color: var(--platinum);
}

#collection-data-owner,
#collection-data-card-total {
  font-size:.9rem;
}
p {
  margin: 0;
}
</style>
