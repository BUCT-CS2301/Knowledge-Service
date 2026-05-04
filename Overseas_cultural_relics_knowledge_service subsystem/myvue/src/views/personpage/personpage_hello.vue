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
          <p class="welcome-desc">欢迎来到您的个人中心，管理您的收藏、评论和个人信息</p>
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
      <div class="recent-activity">
        <h3 class="section-title">
          <i class="el-icon-timer"></i>
          最近动态
        </h3>
        <div class="activity-list">
          <div class="activity-item" v-for="(activity, index) in recentActivities" :key="index">
            <div class="activity-icon" :class="activity.type">
              <i :class="activity.icon"></i>
            </div>
            <div class="activity-content">
              <p class="activity-text">{{ activity.text }}</p>
              <p class="activity-time">{{ activity.time }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="quick-stats">
        <h3 class="section-title">
          <i class="el-icon-info"></i>
          系统信息
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
            <span class="info-label">当前版本</span>
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

export default {
  name: 'personpage_hello',
  data () {
    return {
      username: '游客',
      userAvatar: defaultAvatar,
      collectionsCount: 0,
      commentsCount: 0,
      loginTime: '',
      onlineDuration: '0分钟'
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
    },

    recentActivities () {
      return [
        { type: 'collect', icon: 'el-icon-star-on', text: '收藏了文物 "青铜镜"', time: '5分钟前' },
        { type: 'comment', icon: 'el-icon-message', text: '评论了文物 "玉器"', time: '30分钟前' },
        { type: 'visit', icon: 'el-icon-eye', text: '浏览了文物详情页', time: '1小时前' },
        { type: 'update', icon: 'el-icon-edit', text: '更新了个人资料', time: '2小时前' }
      ]
    }
  },
  mounted () {
    this.initData()
    this.startOnlineTimer()
  },
  methods: {
    initData () {
      const username = storage.getItem('username')
      const users = JSON.parse(storage.getItem('users') || '[]')
      const user = users.find(u => u.username === username)

      if (user) {
        this.username = user.user_name || username
        this.userAvatar = user.avatar && user.avatar !== '' ? user.avatar : defaultAvatar
      } else {
        this.username = username || '游客'
      }

      const userId = storage.getItem('user_id')
      const collections = JSON.parse(storage.getItem('collections') || '[]')
      const comments = JSON.parse(storage.getItem('comments') || '[]')

      this.collectionsCount = collections.filter(c => c.userId === userId).length
      this.commentsCount = comments.filter(c => c.userId === userId).length

      this.loginTime = new Date().toLocaleString('zh-CN')
    },
    handleAvatarError () {
      this.userAvatar = defaultAvatar
    },
    startOnlineTimer () {
      let minutes = 0
      setInterval(() => {
        minutes++
        if (minutes < 60) {
          this.onlineDuration = `${minutes}分钟`
        } else {
          const hours = Math.floor(minutes / 60)
          const mins = minutes % 60
          this.onlineDuration = mins > 0 ? `${hours}小时${mins}分钟` : `${hours}小时`
        }
      }, 60000)
    }
  }
}
</script>

<style scoped>
.hello-page {
  padding: 0;
  min-height: 100%;
}

.welcome-section {
  position: relative;
  background: linear-gradient(135deg, #8B4513 0%, #A0522D 50%, #CD853F 100%);
  border-radius: 0 0 32px 32px;
  padding: 48px 32px;
  overflow: hidden;
  margin-bottom: 24px;
}

.welcome-bg {
  position: absolute;
  top: -50%;
  right: -20%;
  width: 400px;
  height: 400px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
  filter: blur(40px);
}

.welcome-content {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 1;
}

.welcome-left {
  flex: 1;
}

.greeting {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.greeting-text {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.85);
  background: rgba(255, 255, 255, 0.15);
  padding: 6px 16px;
  border-radius: 20px;
  backdrop-filter: blur(10px);
}

.welcome-title {
  font-size: 42px;
  font-weight: 700;
  color: white;
  margin: 0 0 16px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.welcome-title i {
  font-size: 36px;
}

.welcome-desc {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.85);
  margin: 0 0 24px;
  max-width: 400px;
}

.welcome-stats {
  display: flex;
  align-items: center;
  gap: 24px;
}

.mini-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.mini-value {
  font-size: 28px;
  font-weight: 700;
  color: white;
}

.mini-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
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
  width: 140px;
  height: 140px;
}

.user-avatar {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid rgba(255, 255, 255, 0.4);
  position: relative;
  z-index: 2;
}

.avatar-glow {
  position: absolute;
  top: -8px;
  left: -8px;
  right: -8px;
  bottom: -8px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(255, 215, 0, 0.4) 0%, rgba(255, 165, 0, 0.2) 100%);
  z-index: 1;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.6;
  }
  50% {
    transform: scale(1.05);
    opacity: 1;
  }
}

.main-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding-left: 8px;
  border-left: 4px solid #8B4513;
}

.recent-activity {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  border-radius: 10px;
  transition: background 0.3s ease;
}

.activity-item:hover {
  background: #f8f9fa;
}

.activity-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.activity-icon.collect {
  background: rgba(255, 215, 0, 0.15);
  color: #CD8500;
}

.activity-icon.comment {
  background: rgba(78, 205, 196, 0.15);
  color: #44A08D;
}

.activity-icon.visit {
  background: rgba(99, 102, 241, 0.15);
  color: #6366F1;
}

.activity-icon.update {
  background: rgba(52, 152, 219, 0.15);
  color: #3498DB;
}

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-text {
  font-size: 14px;
  color: #333;
  margin: 0 0 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.activity-time {
  font-size: 12px;
  color: #999;
  margin: 0;
}

.quick-stats {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 14px;
  color: #999;
}

.info-value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

@media (max-width: 992px) {
  .welcome-content {
    flex-direction: column;
    text-align: center;
    gap: 32px;
  }

  .welcome-title {
    justify-content: center;
  }

  .welcome-stats {
    justify-content: center;
  }
}

@media (max-width: 576px) {
  .welcome-title {
    font-size: 28px;
  }
}
</style>
