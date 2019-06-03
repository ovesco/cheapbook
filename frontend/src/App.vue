<template>
    <div id="app">
        <router-view/>
    </div>
</template>

<script>
export default {
    computed: {
        token() {
            return this.$store.state.token;
        },
    },
    watch: {
        token(value) {
            if (value === null) this.$router.push({ name: 'home' });
            else this.$router.push({ name: 'editor' });
        },
    },
};
</script>
