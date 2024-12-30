<template>
    <h1>duh</h1>
    <h2>{{ this.$route.params.id }}</h2>
</template>
<script>
export default {
    created() {
        this.$router.push({ name: 'cardDetails', params: { id: this.$route.params.id } });
    }
}
</script>
<style></style>