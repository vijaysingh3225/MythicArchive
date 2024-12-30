<template>
  <div class="card-container">
    <div class="card-image">
      <img :src="card.imageUrl" :alt="card.title" />
    </div>
    <div class="card-info">
      <h3> {{ card.title }}</h3>
      <p> {{ card.description }}</p>
    </div>
    <div class="card-actions">
      <button @click="addToCollection"> Add to Collection</button>
      <button @click="viewDetails"> View Details</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CardView',
  props: {
    card: {
      type: Object,
      required: true
    },
  }
}
//methods: {
//addToCollection() {
//this.$emit('add-to-collection', this.card);
//},
//viewDetails() {
//this.$emit('view-details', this.card);
//}
//}
//}
</script>
<style scoped>
.card-container {
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 16px;
  max-width: 300px;
  text-align: center;
}

.card-image img {
  width: 100%;
  height: auto;
  border-radius: 8px;
}

.card-info h3 {
  margin: 12px 0 8 px;
}


.card-actions button {
  margin: 8px;
  padding: 8px 16px;
  border: none;
  background-color: #007bff;
  color: white;
  cursor: pointer;
  border-radius: 4px;
}

.card-actions button:hover {
  background-color: #0056b3;
}
</style>
  
