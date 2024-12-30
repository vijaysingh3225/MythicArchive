<template>
    <section>
      <h1>{{ card.title }}</h1>
      
      <img v-if="card.id" :src="card.imageUrl" />
  
      <p>Number of Cards: {{ card.numPages }}</p>
      <a target="_blank" :href="card.moreInfoLink">More Information</a>
  
      <p v-if="card.added">It is in my collection</p>
      <p v-else>It is not in my collection</p>
    </section>
  </template>
  
  <script>
  export default {
    props: {
      card: Object,
      searchTerm: String
    }
  }
  </script>
  