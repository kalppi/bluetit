<template>
  <div class="home">
    <h2>Welcome to Bluetit BFF</h2>
    <p>Backend for Frontend service with Vue 3</p>

    <div v-if="health" class="health-status">
      <p>BFF Status: <strong>{{ health.status }}</strong></p>
    </div>

    <div class="clips-section">
      <h3>Clips</h3>
      <div v-if="loading" class="loading">Loading clips...</div>
      <div v-else-if="error" class="error">{{ error }}</div>
      <div v-else-if="clips.length === 0" class="no-clips">
        No clips found. Create one to get started!
      </div>
      <ul v-else class="clips-list">
        <li v-for="clip in clips" :key="clip.id" class="clip-item">
          {{ clip.title || clip.content || 'Untitled Clip' }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { clipService, type Clip } from '../api/clipService'

const health = ref<{ status: string } | null>(null)
const clips = ref<Clip[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

onMounted(async () => {
  // Fetch health status
  try {
    const response = await axios.get('/api/health')
    health.value = response.data
  } catch (err) {
    console.error('Failed to fetch health status:', err)
  }

  // Fetch clips from clip-service via BFF proxy
  loading.value = true
  try {
    clips.value = await clipService.getClips()
  } catch (err: any) {
    console.error('Failed to fetch clips:', err)
    error.value = err.response?.data?.message || 'Failed to load clips'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.home {
  text-align: center;
  max-width: 800px;
  margin: 0 auto;
}

.health-status {
  margin-top: 2rem;
  padding: 1rem;
  background-color: #e8f5e9;
  border-radius: 4px;
  display: inline-block;
}

.clips-section {
  margin-top: 3rem;
  padding: 2rem;
  background-color: #f5f5f5;
  border-radius: 8px;
}

.clips-section h3 {
  margin-bottom: 1.5rem;
  color: #333;
}

.loading {
  color: #666;
  font-style: italic;
}

.error {
  color: #d32f2f;
  padding: 1rem;
  background-color: #ffebee;
  border-radius: 4px;
}

.no-clips {
  color: #666;
  font-style: italic;
}

.clips-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.clip-item {
  padding: 1rem;
  margin-bottom: 0.5rem;
  background-color: white;
  border-radius: 4px;
  text-align: left;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  transition: box-shadow 0.2s;
}

.clip-item:hover {
  box-shadow: 0 2px 6px rgba(0,0,0,0.15);
}
</style>


