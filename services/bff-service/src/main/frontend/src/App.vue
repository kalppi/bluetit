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
import './styles/app.css'
</script>


