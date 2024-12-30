<template>
  <div id="register" class="text-center">
    <form v-on:submit.prevent="registrationValidation">
      <h1>Create Account</h1>
      <div role="alert" v-if="registrationErrors">
        {{ registrationErrorMsg }}
      </div>
      <div class="form-input-group">
        <label for="username">Username</label>
        <input type="text" id="username" v-model="user.username" required autofocus />
      </div>
      <div class="form-input-group">
        <label for="email">Email</label>
        <input type="email" id="email" v-model="user.email" required />
      </div>
      <div class="form-input-group">
        <label for="password">Password</label>
        <input type="password" id="password" v-model="user.password" required />
      </div>
      <div class="form-input-group">
        <label for="confirmPassword">Confirm Password</label>
        <input type="password" id="confirmPassword" v-model="user.confirmPassword" required />
      </div>
      <button id="btn-create-accnt" type="submit">Create Account</button>
      <p>
        <router-link class="sign-up-link" v-bind:to="{ name: 'login' }">Already have an account? Log in.</router-link>
      </p>
    </form>
  </div>
</template>

<script>
import authService from '../services/AuthService';

export default {
  data() {
    return {
      user: {
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        role: 'user',
      },
      registrationErrors: false,
      registrationErrorMsg: 'There were problems registering this user.',
    };
  },
  methods: {
    register() {
      authService
        .register(this.user)
        .then((response) => {
          if (response.status == 201) {
            this.$router.push({
              path: '/login',
              query: { registration: 'success' },
            });
          }
        })
        .catch((error) => {
          const response = error.response;
          this.registrationErrors = true;
          if (response.status === 400) {
            this.registrationErrorMsg = 'Bad Request: Validation Errors';
          }
        });
    },

    registrationValidation() {
      this.registrationErrors = false;
      if (this.user.password.length < 8) {
        this.registrationErrors = true;
        this.registrationErrorMsg = 'Password is not long enough.';
      } else if (this.user.password != this.user.confirmPassword) {
        this.registrationErrors = true;
        this.registrationErrorMsg = 'Password & Confirm Password do not match.';
      } else if (!(/[A-Z]/.test(this.user.password))) {
        this.registrationErrors = true;
        this.registrationErrorMsg = 'Password must contain an uppercase letter.';
      } else if (!(/[a-z]/.test(this.user.password))) {
        this.registrationErrors = true;
        this.registrationErrorMsg = 'Password must contain a lowercase letter.';
      } else if (!(/[0-9]/.test(this.user.password))) {
        this.registrationErrors = true;
        this.registrationErrorMsg = 'Password must contain a number.';
      } else if (!(/[#?!@$%^&*-]/.test(this.user.password))) {
        this.registrationErrors = true;
        this.registrationErrorMsg = 'Password must contain a special character.';
      }
      if (this.registrationErrors === false) {
        this.register();
      }
    },
    clearErrors() {
      this.registrationErrors = false;
      this.registrationErrorMsg = 'There were problems registering this user.';
    },
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

#register {
  background-color: var(--onyx);
  width: 25%;
  min-width: 400px;
  margin: auto;
  display: flex;
  height: 600px;
  margin-top: 100px;
  border-radius: 10px;
}

form {
  display: flex;
  flex-direction: column;
  margin: auto;
}

input {
  width: 100%;
  box-sizing: border-box;
}

button {
  width: 100%;
  margin-top: 15px;
}

#btn-create-accnt {
  height: 25px;
}
</style>
