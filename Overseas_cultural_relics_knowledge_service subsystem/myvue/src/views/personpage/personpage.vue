<template>
  <div class="personpage-container">
    <MainHeader></MainHeader>

    <div class="main-content">
      <aside class="sidebar">
        <div class="user-card">
          <div class="avatar-wrapper">
            <img :src="user.userpic" class="avatar" @error="handleImgError">
            <div class="avatar-border"></div>
          </div>
          <h2 class="username">{{user.username}}</h2>
          <p class="user-role">普通用户</p>
        </div>

        <nav class="nav-menu">
          <el-menu
            mode="vertical"
            background-color="transparent"
            text-color="#666"
            active-text-color="#8B4513"
            :default-active="activeMenu">
            <div class="menu-section">
              <div class="section-title">
                <i class="el-icon-user"></i>
                <span>个人中心</span>
              </div>
              <router-link class="menu-link" to="/personpage/info">
                <el-menu-item index="/personpage/info">
                  <i class="el-icon-user-solid"></i>
                  个人信息
                </el-menu-item>
              </router-link>
              <router-link class="menu-link" to="/personpage/changeimg">
                <el-menu-item index="/personpage/changeimg">
                  <i class="el-icon-camera"></i>
                  修改头像
                </el-menu-item>
              </router-link>
              <router-link class="menu-link" to="/personpage/changeinfo">
                <el-menu-item index="/personpage/changeinfo">
                  <i class="el-icon-edit"></i>
                  修改信息
                </el-menu-item>
              </router-link>
            </div>

            <div class="menu-section">
              <router-link class="menu-link" to="/personpage/favourite">
                <el-menu-item index="/personpage/favourite">
                  <i class="el-icon-star-on"></i>
                  <span>我的收藏</span>
                </el-menu-item>
              </router-link>

              <router-link class="menu-link" to="/personpage/myComment">
                <el-menu-item index="/personpage/myComment">
                  <i class="el-icon-message"></i>
                  <span>我的评论</span>
                </el-menu-item>
              </router-link>
            </div>
          </el-menu>
        </nav>
      </aside>

      <main class="content-area">
        <div class="content-card">
          <router-view></router-view>
        </div>
      </main>
    </div>

    <MainFooter></MainFooter>
  </div>
</template>

<script>
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'
import defaultAvatar from '../../assets/timg.jpeg'
import request from '../../api/request'

export default {
  name: 'personpage',
  created () {
    this.pageInit()
  },
  data () {
    return {
      user: {
        userpic: defaultAvatar,
        username: '游客'
      }
    }
  },
  components: {
    MainFooter,
    MainHeader
  },
  computed: {
    activeMenu () {
      return this.$route.path
    }
  },
  methods: {
    handleImgError () {
      this.user.userpic = defaultAvatar
    },
    async pageInit () {
      if (!localStorage.getItem('username')) {
        this.$message.warning('请先登录')
        setTimeout(() => {
          this.$router.push('/login')
        }, 1500)
        return
      }

      const accessToken = localStorage.getItem('accessToken')
      const username = localStorage.getItem('username')
      
      // 优先从 localStorage 读取本地头像
      const users = JSON.parse(localStorage.getItem('users') || '[]')
      const user = users.find(u => u.username === username)
      
      if (user && user.avatar && user.avatar !== '') {
        this.user.userpic = user.avatar
      }
      
      // 然后从后端获取用户信息
      if (accessToken) {
        try {
          const response = await request.get('/api/v1/auth/current-user', {
            headers: {
              Authorization: `Bearer ${accessToken}`
            }
          })

          if (response.data.code === 200) {
            const data = response.data.data
            this.user.username = data.nickname || data.username || username
            // 只有当后端有头像且本地没有头像时，才使用后端头像
            if (!this.user.userpic || this.user.userpic === defaultAvatar) {
              if (data.avatar && data.avatar !== '') {
                this.user.userpic = data.avatar
              }
            }
            return
          }
        } catch (error) {
          console.error('获取用户信息失败:', error)
        }
      }
      
      // 如果 localStorage 和后端都没有头像，才使用默认头像
      if (!this.user.userpic || this.user.userpic === defaultAvatar) {
        if (user) {
          this.user.username = user.user_name || username
        } else {
          this.user.username = username
        }
      }
    }
  }
}
</script>

<style scoped>
.personpage-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
}

.main-content {
  display: flex;
  gap: 24px;
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.sidebar {
  width: 260px;
  flex-shrink: 0;
}

.user-card {
  background: white;
  border-radius: 16px;
  padding: 32px 24px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
}

.avatar-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  margin: 0 auto 20px;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  position: relative;
  z-index: 2;
  border: 4px solid white;
}

.avatar-border {
  position: absolute;
  top: -4px;
  left: -4px;
  right: -4px;
  bottom: -4px;
  border-radius: 50%;
  background: linear-gradient(135deg, #8B4513 0%, #CD853F 100%);
  z-index: 1;
}

.username {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px;
}

.user-role {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.nav-menu {
  background: white;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.menu-link {
  text-decoration: none;
}

.content-area {
  flex: 1;
  min-width: 0;
}

.content-card {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  min-height: 600px;
}

:deep(.el-menu) {
  border: none;
}

:deep(.el-menu-item) {
  margin: 4px 0;
  border-radius: 8px;
  transition: all 0.3s ease;

  &:hover {
    background: rgba(139, 69, 19, 0.08);
  }
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(139, 69, 19, 0.15) 0%, rgba(205, 133, 63, 0.1) 100%);
}

.menu-section {
  margin-bottom: 16px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  font-size: 14px;
  font-weight: 600;
  color: #666;
  margin-bottom: 8px;
}

.section-title i {
  font-size: 16px;
}
</style>
