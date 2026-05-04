import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/index',
    name: 'Index',
    meta: {
      title: '首页'
    }
  },
  {
    path: '/antiqueDetail',
    name: 'antiqueDetail',
    component: () => import('../views/antiqueDetail/antiqueDetail.vue')
  },
  {
    path: '/index',
    name: 'IndexView',
    component: () => import('../views/common/IndexView.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/LoginView.vue')
  },
  {
    path: '/classify',
    name: 'classify',
    component: () => import('../views/fore/classify.vue')
  },
  {
    path: '/keyword',
    name: 'keyword',
    component: () => import('../views/fore/keyword_sear.vue')
  },
  {
    path: '/result',
    name: 'result',
    component: () => import('../views/fore/result.vue')
  },
  {
    path: '/classify_muse',
    name: 'classify_muse',
    component: () => import('../views/fore/classify_muse.vue')
  },
  {
    path: '/classify_dynasty',
    name: 'classify_dynasty',
    component: () => import('../views/fore/classify_dynasty.vue')
  },
  {
    path: '/classify_mart',
    name: 'classify_mart',
    component: () => import('../views/fore/classify_mart.vue')
  },
  {
    path: '/classify_usea',
    name: 'classify_usea',
    component: () => import('../views/fore/classify_usea.vue')
  },
  {
    path: '/pro_search',
    name: 'search_det',
    component: () => import('../views/fore/search_bar.vue')
  },

  {
    path: '/visualization',
    name: 'visualization',
    component: () => import('../views/visualization/Index.vue')
  },
  {
    path: '/visualization/knowledge-graph',
    name: 'knowledgeGraph',
    component: () => import('../views/visualization/KnowledgeGraph.vue')
  },
  {
    path: '/visualization/timeline',
    name: 'timeline',
    component: () => import('../views/visualization/Timeline.vue')
  },
  {
    path: '/visualization/map',
    name: 'map',
    component: () => import('../views/visualization/Map.vue')
  },
  {
    path: '/visualization/dashboard',
    name: 'dashboard',
    component: () => import('../views/visualization/Dashboard.vue')
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/login/register.vue')
  },
  {
    path: '/personpage',
    name: 'personpage',
    component: () => import('../views/personpage/personpage.vue'),
    children: [
      {
        path: '',
        name: 'personpageHello',
        component: () => import('../views/personpage/personpage_hello.vue')
      },
      {
        path: 'myComment',
        name: 'myComment',
        component: () => import('../views/personpage/myComment.vue')
      },
      {
        path: 'changeimg',
        name: 'changeimg',
        component: () => import('../views/personpage/changeimg.vue')
      },
      {
        path: 'changeinfo',
        name: 'changeinfo',
        component: () => import('../views/personpage/changeinfo.vue')
      },
      {
        path: 'favourite',
        name: 'favourite',
        component: () => import('../views/personpage/favourite.vue')
      },
      {
        path: 'info',
        name: 'info',
        component: () => import('../views/personpage/info.vue')
      },
      {
        path: 'comment',
        name: 'comment',
        component: () => import('../components/comment/comment.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  scrollBehavior: () => ({ top: 0 }),
  routes
})

export default router
