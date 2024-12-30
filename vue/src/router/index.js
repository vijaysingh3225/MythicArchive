import { createRouter as createRouter, createWebHistory } from 'vue-router'
import { useStore } from 'vuex'


// Import components
import HomeView from '../views/HomeView.vue';
import LoginView from '../views/LoginView.vue';
import LogoutView from '../views/LogoutView.vue';
import RegisterView from '../views/RegisterView.vue';
import SearchView from '../views/SearchView.vue';
import CardDetailsView from '../views/CardDetailsView.vue';
import AccountView from '../views/AccountView.vue';
import AllCollectionsView from '../views/AllCollectionsView.vue';
import MyDecksView from '../views/MyDecksView.vue';
import CollectionDetailsView from '../views/CollectionDetailsView.vue';
import Duh from '../views/Duh.vue';
import AdvancedCardView from '../views/AdvancedCardView.vue';


/**
 * The Vue Router is used to "direct" the browser to render a specific view component
 * inside of App.vue depending on the URL.
 *
 * It also is used to detect whether or not a route requires the user to have first authenticated.
 * If the user has not yet authenticated (and needs to) they are redirected to /login
 * If they have (or don't need to) they're allowed to go about their way.
 */
const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: {
      requiresAuth: false
    }
  },
  {
    path: '/search',
    name: 'search',
    component: SearchView,
    meta: {
      requiresAuth: false
    }
  },
  {
    path: "/login",
    name: "login",
    component: LoginView,
    meta: {
      requiresAuth: false
    }
  },
  {
    path: "/logout",
    name: "logout",
    component: LogoutView,
    meta: {
      requiresAuth: false
    }
  },
  {
    path: "/register",
    name: "register",
    component: RegisterView,
    meta: {
      requiresAuth: false
    }
  },
  {
    path: "/card-details/:id",
    name: "cardDetails",
    component: CardDetailsView,
    meta: {
      requiresAuth: false
    }
  },
  {
    path: "/account",
    name: "account",
    component: AccountView,
    meta: {
      requiresAuth: true
    }
  }
  ,
  {
    path: "/collections",
    name: "collections",
    component: AllCollectionsView,
    meta: {
      requiresAuth: false
    }
  },
  {
    path: "/collection-details/:id",
    name: "collectionDetails",
    component: CollectionDetailsView,
    meta: {
      requiresAuth: false
    }
  },{
    path: "/duh/:id",
    name:"duh",
    component: Duh,
    meta:{
      requiresAuth: false
    }
  },{
    path: "/advanced",
    name:"advanced",
    component: AdvancedCardView,
    meta:{
      requiresAuth: false
    }
  },{
    path:"/collection-details/:id/stats",
    name:"collectionStats",
    component: CollectionDetailsView,
    meta:{
      requiresAuth: false
    }
  }
  
];


// Create the router
const router = createRouter({
  history: createWebHistory(),
  routes: routes
});


router.beforeEach((to) => {


  // Get the Vuex store
  const store = useStore();


  // Determine if the route requires Authentication
  const requiresAuth = to.matched.some(x => x.meta.requiresAuth);


  // If it does and they are not logged in, send the user to "/login"
  if (requiresAuth && store.state.token === '') {
    return {name: "login"};
  }
  // Otherwise, do nothing and they'll go to their next destination
});


export default router;
