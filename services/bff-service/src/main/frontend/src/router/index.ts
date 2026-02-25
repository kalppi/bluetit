import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import CreateClipView from '../views/CreateClipView.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/create-clip',
    name: 'CreateClip',
    component: CreateClipView,
  },
]

const router = createRouter({
  history: createWebHistory('/'),
  routes
})

export default router
