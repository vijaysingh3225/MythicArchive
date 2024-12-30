<template>
    <!-- Display the index of the hovered card -->
    <div class="index">{{ hoveredIndex }}</div>
    
    <div class="advanced-card-container">
      <div 
        class="card" 
        v-for="(card, index) in cards" 
        :key="index" 
        @mouseover="moveCards(index, 150)"
        :style="{ top: card.top + 'px' }"
      ></div>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        // Array to hold the card's positions
        cards: Array.from({ length: 20 }, (_, index) => ({ top: index * 20 })),
        // Store the hovered card index
        hoveredIndex: 0,
      };
    },
    methods: {
      moveCards(hoveredIndex, offset) {
        // Update the hovered index when a card is hovered
        this.hoveredIndex = hoveredIndex;
  
        // Update card positions
        this.cards = this.cards.map((card, index) => {
          if (index < hoveredIndex) {
            // Cards above the hovered card remain in their default positions
            return { ...card, top: index * 20 };
          } else if (index === hoveredIndex) {
            // The hovered card remains in its default position
            return { ...card, top: hoveredIndex * 20 };
          } else {
            // Cards below the hovered card move down by the offset
            return { ...card, top: index * 20 + offset };
          }
        });
      },
    },
    mounted() {
      this.$store.commit("SET_FROM_SHUFFLE", false);
    }
  };
  </script>
  
  <style scoped>
  .advanced-card-container {
    width: 80%;
    height: 1000px;
    background-color: var(--onyx);
    margin: 50px auto;
    position: relative;
  }
  
  .card {
    background-image: url();
    height: 180px;
    width: 120px;
    background-color: var(--perry);
    border: 2px solid var(--platinum);
    border-radius: 5px;
    position: absolute;
    transition: top 0.3s ease;
  }
  
  .index {
    font-size: 3rem;
    color: red;
    margin: 10px;
  }
  </style>