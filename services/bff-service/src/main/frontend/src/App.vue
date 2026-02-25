<template>
  <div id="app">
    <header>
      <h1>Bluetit - BFF</h1>
      <nav>
        <router-link to="/">Home</router-link>
      </nav>
    </header>
    <main>
      <router-view></router-view>
    </main>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
header {
  background-color: #f5f5f5;
  padding: 1rem;
  border-bottom: 1px solid #ddd;
}

header h1 {
  margin: 0;
  font-size: 1.5rem;
}

nav {
  margin-top: 1rem;
}

nav a {
  margin-right: 1rem;
  color: #333;
  text-decoration: none;
}

nav a:hover {
  text-decoration: underline;
}

main {
  padding: 2rem;
}
</style>

