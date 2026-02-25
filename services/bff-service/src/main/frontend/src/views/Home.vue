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
import '../styles/home.css'

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



