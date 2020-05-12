import Vue from 'vue'
import Router from 'vue-router'
import syncpage from '@/components/syncpage'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'syncpage',
      component: syncpage
    }
  ]
})
