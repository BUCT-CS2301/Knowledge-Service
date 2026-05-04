import Vue from 'vue'
import Router from 'vue-router'
import personpage from '../views/personpage/personpage'
import changeimg from '../views/personpage/changeimg'
import changeinfo from '../views/personpage/changeinfo'
import info from '../views/personpage/info'
import favourite from '../views/personpage/favourite'
import comment from '../components/comment/comment'
import IndexView from '../views/common/IndexView'
import personpageHello from '../views/personpage/personpage_hello'
import LoginView from '../views/login/LoginView'
import register from '../views/login/register'

Vue.use(Router)

/**
 * constantRoutes:
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/',
    redirect: '/index',
    component: IndexView,
    hidden: true,
    name: 'Index',
    meta: {
      title: '首页'
    }
  },
  {
    path: '/index',
    component: IndexView
  },
  {
    path: '/login',
    component: LoginView
  },
  {
    path: '/register',
    component: register
  },
  {
    path: '/personpage',
    component: personpage,
    children: [
      {
        path: '',
        component: personpageHello
      },
      {
        path: 'changeimg',
        component: changeimg
      },
      {
        path: 'changeinfo',
        component: changeinfo
      },
      {
        path: 'favourite',
        component: favourite
      },
      {
        path: 'info',
        component: info
      },
      {
        path: 'comment',
        component: comment
      }
    ]
  }
  /* {
    path: '/login',
    component: () => import('@/views/common/LoginView.vue'),
    hidden: true,
    name: 'Login',
    meta: {
      title: '登录'
    }
  },
  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  } */
]

/**
 * asyncRoutes:
 * the routes that need to be dynamically loaded based on user roles
 */
export const asyncRoutes = [
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()
// router.beforeEach(function (to, from, next) {})

export default router
