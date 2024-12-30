<template>
  <div id="capstone-app">
    <div id="header">
      <router-link class="nav-item" v-bind:to="{ name: 'home' }">
        <div class="logo"><img src='/images/MALogo.png' class="website-logo"></div>
      </router-link>
      <div id="nav">
        <!-- <router-link class="nav-item" v-bind:to="{ name: 'advanced' }">Advanced</router-link> -->
        <router-link class="nav-item" v-bind:to="{ name: 'home' }">Home<img src='/images/HomeIcon.png'
            class="icon" /></router-link>
        <router-link class="nav-item" v-bind:to="{ name: 'search' }">Search<img src='/images/SearchIcon.png'
            class="icon-search" /></router-link>
        <router-link class="nav-item" v-bind:to="{ name: 'collections' }">Collections<img src='/images/CollectionIcon.png'
            class="icon" /></router-link>

        <span><img src="/images/shuffleIcon.png" class="shuffle-icon" @click="shuffle" /></span>

        <div class="dropdown" v-if="this.$store.state.token != ''">
          <button class="account-button" @click="toggleDropdown" >
            <img class="account" src='/images/accountIcon.png'/>
          </button>
          <div class="dropdown-menu" v-if="showDropdown">
            <router-link class="dropdown-item"
              v-bind:to="{ name: 'collectionDetails', params: { id: this.$store.state.user.collectionId } }"
              v-if="this.$store.state.token != ''">My Collection</router-link>
            <router-link class="dropdown-item" v-bind:to="{ name: 'logout' }"
              v-if="this.$store.state.token != ''">Logout</router-link>
          </div>
        </div>

        <router-link class="nav-item" v-bind:to="{ name: 'login' }" v-if="this.$store.state.token === ''">
          Sign In
        </router-link>
      </div>
    </div>
    <aside id="sidebar">
      <a href="https://www.youtube.com" target="_blank"><button type="button" id="sidebar-1"></button></a>
      <a href="https://www.google.com" target="_blank"><button type="button" id="sidebar-2"></button></a>
      <a href="https://www.twitter.com" target="_blank"><button type="button" id="sidebar-3"></button></a>
      <a href="https://www.facebook.com" target="_blank"><button type="button" id="sidebar-4"></button></a>
      <a href="https://www.instagram.com" target="_blank"><button type="button" id="sidebar-5"></button></a>
    </aside>
    <router-view />
  </div>
  <footer></footer>
</template>

<script>
import SearchService from "./services/SearchService";

export default {
  data() {
    return {
      randomCardUrl: null,
      showDropdown: false
    };
  },
  methods: {
    shuffle() {
      this.$store.commit("SET_FROM_SHUFFLE", true);
      SearchService.getRandomCard()
        .then((response) => {
          this.randomCardUrl = response.data.id;
          this.$router.push({ name: 'duh', params: { id: this.randomCardUrl } });
        })
        .catch((error) => {
          console.error("Error fetching data:", error);
        });
    },
    toggleDropdown() {
      this.showDropdown = !this.showDropdown;
    },
    handleClickOutside(event) {
      const dropdown = this.$el.querySelector('.dropdown');
      if (dropdown && !dropdown.contains(event.target)) {
        this.showDropdown = false;
      }
    }
  },
  mounted() {
    this.$store.commit("SET_FROM_SHUFFLE", false);
    document.addEventListener('click', this.handleClickOutside);
  },
  beforeDestroy() {
    document.removeEventListener('click', this.handleClickOutside);
  }
};
</script>
<style scoped>
.nav-item,
.nav-item:visited,
#header {
  color: var(--platinum);
  text-decoration: none;
  font-size: 1.4vw;
  box-shadow: 10px 4px 60px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  flex-wrap: nowrap;
}

#nav {
  display: flex;
  justify-content: flex-end;
  margin-right: 40px;
  gap: 40px;
  align-items: center;

}

.logo {
  padding-left: 30px;
}

#header {
  display: flex;
  justify-content: space-between;
  background-color: #282c2e;
  border-bottom: 2px solid var(--platinum);
  padding-top: 10px;
  padding-bottom: 5px;
}

.account-button {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
}

.account {
  width: 1.8vw;
}

.dropdown {
  position: relative;
}

.dropdown-menu {
  position: absolute;
  top: 110%;
  right: 0;
  background-color: #575757;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border-radius: 5px;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  width: 100px;
}

.dropdown-item{
  padding: 10px 15px;
  text-decoration: none;
  color: var(--platinum);
  font-size: 1rem;
}

.dropdown-item:hover {
  background-color: #8a8a8a;
  border-radius: 5px;
}

.icon {
  width: 1.2vw;
  height: 1vw;
  margin-left: 10px;
}

.icon-search {
  width: 1vw;
  margin-left: 7px;
}

.shuffle-icon {
  height: 1.4vw;
  padding-top: 10%;
  cursor: pointer;
}

.website-logo {
  width: 15vw;
}

#sidebar-1:hover,
#sidebar-2:hover,
#sidebar-3:hover,
#sidebar-4:hover,
#sidebar-5:hover {
  opacity: 1;
  cursor: pointer;
}

#sidebar-1,
#sidebar-2,
#sidebar-3,
#sidebar-4,
#sidebar-5 {
  height: 2vw;
  width: 2vw;
  min-width: 25px;
  min-height: 25px;
  background-size: cover;
  border: 0px;
  opacity: 0.7;
  margin: 1px 0;
}

#sidebar-1 {
  background-image: url("/Images/sidebar/sidebar-1.png");
}

#sidebar-2 {
  background-image: url("/Images/sidebar/sidebar-2.png");
}

#sidebar-3 {
  background-image: url("/Images/sidebar/sidebar-3.png");
}

#sidebar-4 {
  background-image: url("/Images/sidebar/sidebar-4.png");
}

#sidebar-5 {
  background-image: url("/Images/sidebar/sidebar-5.png");
}

#sidebar {
  display: flex;
  flex-direction: column;
  height: 300px;
  position: fixed;
  margin-top: 300px;
}
footer{
  height: 200px;
  background-color: var(--onyx);
  margin-top: 100px;
}</style>
