<template>
  <div class="home">
    <h2>Welcome to Bluetit BFF</h2>
    <p>Backend for Frontend service with Vue 3</p>
    <div v-if="health" class="health-status">
      <p>API Status: <strong>{{ health.status }}</strong></p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

const health = ref<{ status: string } | null>(null)

onMounted(async () => {
  try {
    const response = await axios.get('/api/health')
    health.value = response.data
  } catch (error) {
    console.error('Failed to fetch health status:', error)
  }
})
</script>

<style scoped>
.home {
  text-align: center;
}

.health-status {
  margin-top: 2rem;
  padding: 1rem;
  background-color: #e8f5e9;
  border-radius: 4px;
  display: inline-block;
}
</style>

