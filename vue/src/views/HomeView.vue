<template>
  <div class="banner">
    <div class="banner-text">
      <div>Collect, Organize, Dominate<br>-<br>The Archive Awaits</div>
      <div class="banner-buttons">
        <router-link class="nav-item banner-link" v-bind:to="{ name: 'search' }">Search Cards</router-link>
        <router-link class="nav-item banner-link" v-bind:to="{ name: 'collections' }">Collections</router-link>
        <router-link class="nav-item banner-link" v-bind:to="{ name: 'login' }" v-if="this.$store.state.token === ''">Sign
          In</router-link>
      </div>
    </div>
    <div class="banner-image">
      <div
        class="homepage-card"
        v-for="(card, index) in cards"
        :key="index"
        :style="{ animationDelay: `${index * 0.1}s` }"
        @animationiteration="resetCard(card)"
      >
        <div class="card-front">
          <div class="card-image"></div>
          <div class="card-lines" v-for="n in 5" :key="n"></div>
        </div>
        <div
          class="card-back"
          :style="{
            backgroundImage: card.isAltImage
              ? `url(${card.altImageUrl})`
              : `url('/images/CardBack.jpg')`,
          }"
        ></div>
      </div>
    </div>
  </div>


  <div class="content-1-container">
    <div class="content-text">Search Through 1000s of Cards!
    </div>
    <img src="\images\ScreenShot\Cards.png" alt="" class="content-img">
  </div>
  <div class="content-1-container">
    <img src="\images\ScreenShot\CardDetails.png" alt="" class="content-img-2">
    <div class="content-text">
      Analyze Card Stats!
    </div>
  </div>
  <div class="content-1-container">
    <div class="content-text">Store Cards In Your Personal Collection!
    </div>
    <img src="\images\ScreenShot\Collections.png" alt="" class="content-img">
  </div>
  <div class="content-1-container">
    <img src="\images\ScreenShot\CollectionView.png" alt="" class="content-img-4">
    <div class="content-text">Analyze and Dominate!
    </div>
  </div>
  <footer></footer>
</template>

<script>
export default {
  data() {
    return {
      cards: Array.from({ length: 20 }, () => ({
        isAltImage: false, 
        altImageUrl: '/images/amongUS.png', 
      })),
    };
  },
  methods: {
    resetCard(card) {
      card.isAltImage = Math.random() < 0.02; 
    },
  },
  mounted() {
    this.$store.commit("SET_FROM_SHUFFLE", false);
  }
};
</script>

<style scoped>
.banner {
  width: 100%;
  height: 550px;
  background-color: var(--onyx);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 50px;
}

.banner-text,
.banner-image {
  height: 85%;
  width: 30%;
  margin: auto 20px;
}

.banner-image {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-evenly;
  overflow: hidden;
}

.banner-text {
  text-align: center;
  font-size: 3rem;
  display: flex;
  align-items: center;
  flex-direction: column;
  justify-content: center;
  gap: 100px;
}

.banner-buttons {
  display: flex;
  gap: 20px;
}

.banner-link {
  background-color: var(--affron);
  border-radius: 5px;
  text-align: center;
  padding: 15px;
  font-size: 1rem;
  white-space: nowrap;
  text-decoration: none;
  color: var(--onyx);
}

.banner-link:nth-child(2) {
  background-color: var(--platinum);
  color: var(--onyx);
}

.banner-link:hover {
  background-color: #927830;
}

.banner-link:nth-child(2):hover {
  background-color: #94928e;
}

.homepage-card {
  height: 24%;
  width: 80px;
  margin: auto 10px;
  border-radius: 5px;
  animation: rotateY 4s linear infinite;
  transform-style: preserve-3d;
  position: relative;
  perspective: 1000px;
  border:1px solid var(--affron);
}

.card-front,
.card-back {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  border-radius: 5px;
  background-color: var(--perry);
}

.card-front {
  background-color: #24a7a1;
}

.card-back {
  background-image: url('/images/CardBack.jpg');
  background-size: cover;
  transform: rotateY(180deg);
}

.card-image {
  background-color: var(--perry);
  width: 80%;
  height: 40%;
  margin: auto;
  margin-top: 10px;
  border-radius: 3px;
  margin-bottom: 12px;
}

.card-text {
  display: flex;
  height: 30%;
  width: 80%;
  margin: auto;
  justify-content: space-evenly;

}
.card-line-container{
  display: flex;
  flex-direction: column;
  height:100%;
  width: 100%;
  justify-content: space-evenly;
}
.card-lines {
  width: 80%;
  height: 3%;
  background-color: var(--perry);
  margin: 4px auto;
}
.homepage-card:hover {
  animation: rotateX 3s linear infinite;
  transform-style: preserve-3d;
}

.content-1-container {
  display: flex;
  justify-content: center;
  height: 600px;
  align-items: center;
  gap: 40px;
}
.content-1-container:nth-child(odd){
  background-color: #9c9c9c;
}

.content-text {
  color: var(--onyx);
  font-size: 4rem;
  width: 30%;
  height: 40%;
  text-align: center;

}

.content-img {
  width: 40%;
  background-color: var(--perry);
  height: 80%;
  border-radius: 15px;
  border: 20px solid  var(--perry);
}
.content-img-4{
  width: 60%;
  background-color: var(--perry);
  height: 90%;
  border-radius: 15px;
  border: 20px solid  var(--perry);
}
.content-img-2{
  width: 50%;
  background-color: var(--perry);
  height: 90%;
  border-radius: 15px;
  border: 20px solid  var(--perry);
}


.card-back {
  background-size: cover;
  background-repeat: no-repeat;
  transform: rotateY(180deg);
}

@media(max-width:1000px) {
  .banner-image {
    display: none;
  }

}

@keyframes rotateY {
  0% {
    transform: rotateY(0deg);
  }

  100% {
    transform: rotateY(360deg);
  }
}
</style>