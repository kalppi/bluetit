<template>
  <section class="create-clip">
    <h2>Create clip request</h2>

    <form @submit.prevent="onSubmit" class="form">
      <div class="field">
        <label for="episodeId">Episode ID</label>
        <input
          id="episodeId"
          v-model="episodeId"
          type="text"
          required
        />
      </div>

      <div class="field">
        <label for="startSeconds">Start (seconds)</label>
        <input
          id="startSeconds"
          v-model.number="startSeconds"
          type="number"
          step="0.1"
          min="0"
          required
        />
      </div>

      <div class="field">
        <label for="endSeconds">End (seconds)</label>
        <input
          id="endSeconds"
          v-model.number="endSeconds"
          type="number"
          step="0.1"
          min="0"
          required
        />
      </div>

      <p v-if="error" class="error">{{ error }}</p>
      <p v-if="createdId" class="success">
        Created clip request: {{ createdId }} (status: {{ createdStatus }})
      </p>

      <button type="submit" :disabled="submitting">
        {{ submitting ? 'Creating…' : 'Create clip' }}
      </button>
    </form>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { clipService } from '../api/clipService'

const episodeId = ref('')
const startSeconds = ref<number | null>(null)
const endSeconds = ref<number | null>(null)
const submitting = ref(false)
const error = ref<string | null>(null)
const createdId = ref<string | null>(null)
const createdStatus = ref<string | null>(null)

const onSubmit = async () => {
  error.value = null
  createdId.value = null
  createdStatus.value = null

  if (startSeconds.value == null || endSeconds.value == null) {
    error.value = 'Start and end seconds are required'
    return
  }
  if (startSeconds.value >= endSeconds.value) {
    error.value = 'Start must be before end'
    return
  }

  submitting.value = true
  try {
    const res = await clipService.createClip({
      episodeId: episodeId.value,
      startSeconds: startSeconds.value,
      endSeconds: endSeconds.value,
    })
    createdId.value = res.clipRequestId
    createdStatus.value = res.status
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Failed to create clip request'
  } finally {
    submitting.value = false
  }
}
</script>
<style scoped>
.create-clip {
  max-width: 420px;
  margin: 2rem auto;
}
.form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.field {
  display: flex;
  flex-direction: column;
}
.error {
  color: #c00;
}
.success {
  color: #060;
}
button {
  align-self: flex-start;
}
</style>
