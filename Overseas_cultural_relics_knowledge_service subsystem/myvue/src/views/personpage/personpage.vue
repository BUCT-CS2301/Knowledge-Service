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
            :default-openeds="['1']"
            mode="vertical"
            background-color="transparent"
            text-color="#666"
            active-text-color="#8B4513">

            <el-submenu index="1">
              <template slot="title">
                <i class="el-icon-user"></i>
                <span>个人中心</span>
              </template>
              <el-menu-item-group>
                <router-link class="menu-link" to="/personpage/info">
                  <el-menu-item index="1-1">
                    <i class="el-icon-user-solid"></i>
                    个人信息
                  </el-menu-item>
                </router-link>
                <router-link class="menu-link" to="/personpage/changeimg">
                  <el-menu-item index="1-2">
                    <i class="el-icon-camera"></i>
                    修改头像
                  </el-menu-item>
                </router-link>
                <router-link class="menu-link" to="/personpage/changeinfo">
                  <el-menu-item index="1-3">
                    <i class="el-icon-edit"></i>
                    修改信息
                  </el-menu-item>
                </router-link>
              </el-menu-item-group>
            </el-submenu>

            <router-link class="menu-link" to="/personpage/favourite">
              <el-menu-item index="2">
                <i class="el-icon-star-on"></i>
                <span>我的收藏</span>
              </el-menu-item>
            </router-link>

            <router-link class="menu-link" to="/personpage/myComment">
              <el-menu-item index="3">
                <i class="el-icon-message"></i>
                <span>我的评论</span>
              </el-menu-item>
            </router-link>
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
  methods: {
    handleImgError () {
      this.user.userpic = defaultAvatar
    },
    pageInit () {
      if (!localStorage.getItem('username')) {
        this.$message.warning('请先登录')
        setTimeout(() => {
          this.$router.push('/login')
        }, 1500)
        return
      }

      const username = localStorage.getItem('username')
      const users = JSON.parse(localStorage.getItem('users') || '[]')
      const user = users.find(u => u.username === username)

      if (user) {
        this.user.username = user.user_name || username
        this.user.userpic = user.avatar && user.avatar !== '' ? user.avatar : this.user.userpic
      } else {
        this.user.username = username
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

:deep(.el-submenu__title) {
  margin: 4px 0;
  border-radius: 8px;
  transition: all 0.3s ease;

  &:hover {
    background: rgba(139, 69, 19, 0.08);
  }
}
</style>
