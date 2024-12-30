<template>
  <h1>Logout</h1>
</template>

<script>
export default {
  created() {
    this.$store.commit("SET_FROM_SHUFFLE", false);
    this.$store.commit("LOGOUT");
    this.$router.push("/login");
  }
};
</script>
