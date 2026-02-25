import axios from 'axios'

export interface Clip {
  id?: string
  title?: string
  content?: string
  createdAt?: string
  updatedAt?: string
  // Add other clip properties as needed
}

export const clipService = {
  /**
   * Get all clips
   */
  async getClips(): Promise<Clip[]> {
    const response = await axios.get('/api/clips')
    return response.data
  },

  /**
   * Get a single clip by ID
   */
  async getClip(id: string): Promise<Clip> {
    const response = await axios.get(`/api/clips/${id}`)
    return response.data
  },

  /**
   * Create a new clip
   */
  async createClip(clip: Partial<Clip>): Promise<Clip> {
    const response = await axios.post('/api/clips', clip)
    return response.data
  },

  /**
   * Update an existing clip
   */
  async updateClip(id: string, clip: Partial<Clip>): Promise<Clip> {
    const response = await axios.put(`/api/clips/${id}`, clip)
    return response.data
  },

  /**
   * Delete a clip
   */
  async deleteClip(id: string): Promise<void> {
    await axios.delete(`/api/clips/${id}`)
  }
}

