<template>
  <div id="login">
    <form v-on:submit.prevent="login">
      <h1 >Sign In</h1>
      <div role="alert" v-if="invalidCredentials">
        Invalid username and password!
      </div>
      <div role="alert" v-if="this.$route.query.registration">
        Thank you for registering, please sign in.
      </div>
      <div class="form-input-group">
        <label for="username">Username</label>
        <input type="text" id="username" v-model="user.username" required autofocus />
      </div>
      <div class="form-input-group">
        <label for="password">Password</label>
        <input type="password" id="password" v-model="user.password" required />
      </div>
      <button id="btn-sign-in" type="submit" class="submit">Sign in</button>
      <p>
      <router-link class="sign-up-link" v-bind:to="{ name: 'register' }">Need an account? Sign up.</router-link></p>
    </form>
  </div>
</template>

<script>
import authService from "../services/AuthService";

export default {
  components: {},
  data() {
    return {
      user: {
        username: "",
        password: ""
      },
      invalidCredentials: false
    };
  },
  methods: {
    login() {
      authService
        .login(this.user)
        .then(response => {
          if (response.status == 200) {
            this.$store.commit("SET_AUTH_TOKEN", response.data.token);
            this.$store.commit("SET_USER", response.data.user);
            this.$router.push("/");
          }
        })
        .catch(error => {
          const response = error.response;

          if (response.status === 401) {
            this.invalidCredentials = true;
          }
        });
    }
  },

  mounted() {
    this.$store.commit("SET_FROM_SHUFFLE", false);
  }
};
</script>

<style scoped>
.form-input-group {
  margin-bottom: 1rem;
}
label {
  margin-right: 0.5rem;
}
#login{
  background-color: var(--onyx);
  width:25%;
  min-width: 300px;
  margin: auto;
  display: flex;
  height: 450px;
  margin-top: 100px;
  border-radius: 10px;
}
form{
  display: flex;
  flex-direction: column;
  margin: auto;
}
input{
  width:100%;
  box-sizing: border-box;
}
button{
  width:100%;
  margin-top: 15px;
}

#btn-sign-in {
  height: 25px;
}

</style>