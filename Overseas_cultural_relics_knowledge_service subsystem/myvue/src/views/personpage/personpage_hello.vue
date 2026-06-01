<template>
  <div class="hello-page">
    <div class="welcome-section">
      <div class="welcome-bg"></div>
      <div class="welcome-content">
        <div class="welcome-left">
          <div class="greeting">
            <span class="greeting-text">{{ greetingText }}</span>
            <h1 class="welcome-title">
              <i class="el-icon-user-solid"></i>
              {{ username }}
            </h1>
          </div>
          <p class="welcome-desc">欢迎来到您的个人中心，探索海外文物世界</p>
          <div class="welcome-stats">
            <div class="mini-stat">
              <span class="mini-value">{{ collectionsCount }}</span>
              <span class="mini-label">收藏</span>
            </div>
            <div class="divider"></div>
            <div class="mini-stat">
              <span class="mini-value">{{ commentsCount }}</span>
              <span class="mini-label">评论</span>
            </div>
          </div>
        </div>
        <div class="welcome-right">
          <div class="avatar-circle">
            <img :src="userAvatar" class="user-avatar" @error="handleAvatarError">
            <div class="avatar-glow"></div>
          </div>
        </div>
      </div>
    </div>

    <div class="main-content">
      <div class="quick-stats">
        <h3 class="section-title">
          <i class="el-icon-info"></i>
          账户信息
        </h3>
        <div class="info-list">
          <div class="info-item">
            <span class="info-label">登录时间</span>
            <span class="info-value">{{ loginTime }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">在线时长</span>
            <span class="info-value">{{ onlineDuration }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">版本号</span>
            <span class="info-value">v1.0.0</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
var storage = window.localStorage
import defaultAvatar from '../../assets/timg.jpeg'
import { fetchCurrentUserProfile, getCurrentUserId, getUserCollections, getUserComments, parseJsonResult } from '@/api/user'

export default {
  name: 'personpage_hello',
  data () {
    return {
      username: '用户',
      userAvatar: defaultAvatar,
      collectionsCount: 0,
      commentsCount: 0,
      loginTime: '',
      onlineDuration: '0分钟',
      totalOnlineMinutes: 0,
      sessionStartTime: 0,
      timerInterval: null
    }
  },
  computed: {
    greetingText () {
      const hour = new Date().getHours()
      if (hour < 6) return '夜深了'
      if (hour < 12) return '早上好'
      if (hour < 14) return '中午好'
      if (hour < 18) return '下午好'
      return '晚上好'
    }
  },
  mounted () {
    this.initData()
    this.startOnlineTimer()
  },
  methods: {
    async initData () {
      const username = storage.getItem('username')
      const userId = getCurrentUserId()

      // 优先从 localStorage 读取本地头像
      const users = JSON.parse(storage.getItem('users') || '[]')
      const localUser = users.find(u => u.username === username)
      if (localUser?.avatar) {
        this.userAvatar = localUser.avatar
      }

      if (userId) {
        try {
          const profile = await fetchCurrentUserProfile()
          this.username = profile.user_name || String(profile.user_id)
          storage.setItem('user_name', this.username)

          const [collectRes, commentRes] = await Promise.all([
            getUserCollections(userId),
            getUserComments(userId)
          ])
          this.collectionsCount = (parseJsonResult(collectRes) || []).length
          this.commentsCount = (parseJsonResult(commentRes) || []).length
        } catch (error) {
          console.error('获取用户信息失败:', error)
          this.username = storage.getItem('user_name') || username || '用户'
        }
      } else {
        this.username = username || '用户'
      }

      // 从 localStorage 读取登录时间，如果没有则记录当前时间
      if (localUser?.loginTime) {
        this.loginTime = localUser.loginTime
      } else {
        this.loginTime = new Date().toLocaleString('zh-CN')
        // 保存登录时间到 localStorage
        this.saveLoginTime()
      }
      
      // 加载历史在线时长
      this.loadOnlineDuration()
    },
    handleAvatarError () {
      this.userAvatar = defaultAvatar
    },
    loadOnlineDuration () {
      const username = storage.getItem('username')
      const users = JSON.parse(storage.getItem('users') || '[]')
      const user = users.find(u => u.username === username)
      
      if (user && user.onlineMinutes !== undefined) {
        this.totalOnlineMinutes = user.onlineMinutes
      } else {
        this.totalOnlineMinutes = 0
      }
      
      this.updateOnlineDurationDisplay()
    },
    startOnlineTimer () {
      // 记录本次会话开始时间
      this.sessionStartTime = Date.now()
      
      // 每分钟更新一次
      this.timerInterval = setInterval(() => {
        this.totalOnlineMinutes++
        this.updateOnlineDurationDisplay()
        this.saveOnlineDuration()
      }, 60000)
    },
    updateOnlineDurationDisplay () {
      if (this.totalOnlineMinutes < 60) {
        this.onlineDuration = `${this.totalOnlineMinutes}分钟`
      } else {
        const hours = Math.floor(this.totalOnlineMinutes / 60)
        const mins = this.totalOnlineMinutes % 60
        this.onlineDuration = mins > 0 ? `${hours}小时${mins}分钟` : `${hours}小时`
      }
    },
    saveOnlineDuration () {
      const username = storage.getItem('username')
      const users = JSON.parse(storage.getItem('users') || '[]')
      const index = users.findIndex(u => u.username === username)
      
      if (index !== -1) {
        users[index].onlineMinutes = this.totalOnlineMinutes
      } else {
        users.push({ username: username, onlineMinutes: this.totalOnlineMinutes })
      }
      
      storage.setItem('users', JSON.stringify(users))
    },
    saveLoginTime () {
      const username = storage.getItem('username')
      const users = JSON.parse(storage.getItem('users') || '[]')
      const index = users.findIndex(u => u.username === username)
      
      if (index !== -1) {
        users[index].loginTime = this.loginTime
      } else {
        users.push({ username: username, loginTime: this.loginTime })
      }
      
      storage.setItem('users', JSON.stringify(users))
    },
    beforeUnmount () {
      // 页面关闭时清除定时器
      if (this.timerInterval) {
        clearInterval(this.timerInterval)
      }
    }
  }
}
</script>

<style scoped>
.hello-page {
  padding: 20px;
}

.welcome-section {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 20px;
}

.welcome-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #8B4513 0%, #D2691E 100%);
}

.welcome-content {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30px;
  color: white;
}

.welcome-left {
  flex: 1;
}

.greeting-text {
  font-size: 16px;
  opacity: 0.9;
}

.welcome-title {
  font-size: 28px;
  margin: 10px 0;
}

.welcome-desc {
  font-size: 14px;
  opacity: 0.8;
  margin-bottom: 20px;
}

.welcome-stats {
  display: flex;
  align-items: center;
  gap: 30px;
}

.mini-stat {
  text-align: center;
}

.mini-value {
  display: block;
  font-size: 28px;
  font-weight: bold;
}

.mini-label {
  font-size: 14px;
  opacity: 0.8;
}

.divider {
  width: 1px;
  height: 40px;
  background: rgba(255, 255, 255, 0.3);
}

.welcome-right {
  flex-shrink: 0;
}

.avatar-circle {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid white;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.user-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-glow {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.3) 0%, transparent 70%);
}

.main-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #333;
}

.quick-stats {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  color: #999;
  font-size: 14px;
}

.info-value {
  color: #333;
  font-weight: 500;
}
</style>