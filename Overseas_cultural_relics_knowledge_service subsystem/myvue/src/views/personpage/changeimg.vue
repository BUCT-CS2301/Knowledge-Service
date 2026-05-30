<template>
  <div class="changeimg-page">
    <div class="page-header">
      <h1 class="page-title">
        <i class="el-icon-camera"></i>
        修改头像
      </h1>
      <p class="page-desc">点击下方区域上传新头像</p>
    </div>
    
    <div class="upload-container">
      <div class="upload-card">
        <el-upload
          class="avatar-uploader"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload">
          
          <div class="avatar-preview">
            <img :src="imageUrl || defaultAvatar" class="avatar-img">
            <div class="upload-overlay">
              <i class="el-icon-plus"></i>
              <span>点击上传</span>
            </div>
          </div>
        </el-upload>
        
        <p class="upload-tip">支持 JPG、PNG 格式，大小不超过 2MB</p>
      </div>
    </div>
  </div>
</template>

<script>
import defaultAvatar from '../../assets/timg.jpeg'

export default {
  data () {
    return {
      imageUrl: '',
      defaultAvatar: defaultAvatar,
      user: {
        name: '',
        sex: '',
        tel: ''
      }
    }
  },
  mounted () {
    this.loadAvatar()
  },
  methods: {
    loadAvatar () {
      const username = localStorage.getItem('username')
      const users = JSON.parse(localStorage.getItem('users') || '[]')
      const user = users.find(u => u.username === username)
      
      if (user && user.avatar) {
        this.imageUrl = user.avatar
      }
    },
    handleAvatarSuccess (res, file) {
      this.imageUrl = URL.createObjectURL(file.raw)
      
      const username = localStorage.getItem('username')
      const users = JSON.parse(localStorage.getItem('users') || '[]')
      const index = users.findIndex(u => u.username === username)
      
      if (index !== -1) {
        users[index].avatar = this.imageUrl
        localStorage.setItem('users', JSON.stringify(users))
      }
      
      this.$message.success('头像上传成功!')
    },
    beforeAvatarUpload (file) {
      const isJPG = file.type === 'image/jpeg'
      const isPNG = file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2
      
      if (!isJPG && !isPNG) {
        this.$message.error('上传头像图片只能是 JPG 或 PNG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
      }
      
      return (isJPG || isPNG) && isLt2M
    }
  }
}
</script>

<style scoped>
.changeimg-page {
  min-height: 400px;
}

.page-header {
  margin-bottom: 32px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-desc {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.upload-container {
  display: flex;
  justify-content: center;
}

.upload-card {
  background: #fafafa;
  border-radius: 16px;
  padding: 48px;
  text-align: center;
}

.avatar-uploader {
  cursor: pointer;
}

.avatar-preview {
  position: relative;
  width: 200px;
  height: 200px;
  margin: 0 auto;
}

.avatar-img {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s ease;
  
  i {
    font-size: 32px;
    color: white;
    margin-bottom: 8px;
  }
  
  span {
    font-size: 14px;
    color: white;
  }
}

.avatar-preview:hover .upload-overlay {
  background: rgba(139, 69, 19, 0.7);
  opacity: 1;
}

.upload-tip {
  margin-top: 20px;
  font-size: 13px;
  color: #999;
}
</style>
