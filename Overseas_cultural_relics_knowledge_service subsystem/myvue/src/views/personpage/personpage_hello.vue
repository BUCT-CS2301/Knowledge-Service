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
import axios from 'axios'

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
      recentActivities: []
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
      const users = JSON.parse(storage.getItem('users') || '[]')
      const user = users.find(u => u.username === username)

      if (user) {
        this.username = user.user_name || username
        this.userAvatar = user.avatar && user.avatar !== '' ? user.avatar : defaultAvatar
      } else {
        this.username = username || '用户'
      }

      const userId = storage.getItem('user_id')
      const collections = JSON.parse(storage.getItem('collections') || '[]')
      const comments = JSON.parse(storage.getItem('comments') || '[]')

      this.collectionsCount = collections.filter(c => c.userId === userId).length
      this.commentsCount = comments.filter(c => c.userId === userId).length

      this.loginTime = new Date().toLocaleString('zh-CN')

      await this.fetchUserLogs(userId)
    },
    async fetchUserLogs (userId) {
      const accessToken = storage.getItem('accessToken')
      if (!userId) {
        this.setDefaultActivities()
        return
      }

      try {
        const response = await axios.get(`/api/v1/users/${userId}/logs?page=1&pageSize=4`, {
          headers: {
            'Authorization': `Bearer ${accessToken}`
          }
        })

        if (response.data.code === 200 && response.data.data && response.data.data.length > 0) {
          this.recentActivities = response.data.data.map(log => {
            const typeMap = {
              'collect': { icon: 'el-icon-star-on', type: 'collect' },
              'comment': { icon: 'el-icon-message', type: 'comment' },
              'visit': { icon: 'el-icon-eye', type: 'visit' },
              'update': { icon: 'el-icon-edit', type: 'update' },
              'login': { icon: 'el-icon-user', type: 'update' }
            }
            const info = typeMap[log.type] || { icon: 'el-icon-info', type: 'update' }
            return {
              type: info.type,
              icon: info.icon,
              text: log.description || log.action,
              time: log.time || '刚刚'
            }
          })
        } else {
          this.setDefaultActivities()
        }
      } catch (error) {
        console.log('获取用户日志失败:', error)
        this.setDefaultActivities()
      }
    },
    setDefaultActivities () {
      this.recentActivities = [
        { type: 'collect', icon: 'el-icon-star-on', text: '收藏了青铜鼎', time: '5分钟前' },
        { type: 'comment', icon: 'el-icon-message', text: '评论了青花瓷', time: '30分钟前' },
        { type: 'visit', icon: 'el-icon-eye', text: '浏览了文物详情', time: '1小时前' },
        { type: 'update', icon: 'el-icon-edit', text: '更新了个人资料', time: '2小时前' }
      ]
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

.recent-activity,
.quick-stats {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.activity-icon.collect {
  background: linear-gradient(135deg, #FF6B6B, #FF8E53);
}

.activity-icon.comment {
  background: linear-gradient(135deg, #4ECDC4, #44A08D);
}

.activity-icon.visit {
  background: linear-gradient(135deg, #45B7D1, #2980B9);
}

.activity-icon.update {
  background: linear-gradient(135deg, #9B59B6, #8E44AD);
}

.activity-content {
  flex: 1;
}

.activity-text {
  font-size: 14px;
  color: #333;
  margin: 0;
}

.activity-time {
  font-size: 12px;
  color: #999;
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