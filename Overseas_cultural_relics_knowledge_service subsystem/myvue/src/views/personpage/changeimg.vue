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
        <div
          class="avatar-uploader"
          @click="triggerUpload"
          @dragover.prevent
          @drop.prevent="handleDrop">

          <div class="avatar-preview">
            <img :src="imageUrl || defaultAvatar" class="avatar-img">
            <div class="upload-overlay">
              <i class="el-icon-plus"></i>
              <span>点击上传</span>
            </div>
          </div>
        </div>

        <input
          ref="fileInput"
          type="file"
          accept="image/jpeg,image/png"
          class="hidden-upload"
          @change="handleFileChange">

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
      defaultAvatar: defaultAvatar
    }
  },
  mounted () {
    this.loadAvatar()
  },
  methods: {
    loadAvatar () {
      const userId = localStorage.getItem('user_id')
      const users = JSON.parse(localStorage.getItem('users') || '[]')
      const user = users.find(u => String(u.id) === userId)

      if (user && user.avatar) {
        this.imageUrl = user.avatar
      }
    },
    triggerUpload () {
      this.$refs.fileInput.click()
    },
    handleDrop (e) {
      const files = e.dataTransfer.files
      if (files.length > 0) {
        this.processFile(files[0])
      }
    },
    handleFileChange (e) {
      const file = e.target.files[0]
      if (file) {
        this.processFile(file)
      }
      e.target.value = ''
    },
    processFile (file) {
      const isJPG = file.type === 'image/jpeg'
      const isPNG = file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG && !isPNG) {
        this.$message.error('上传头像图片只能是 JPG 或 PNG 格式!')
        return
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
        return
      }

      const reader = new FileReader()
      reader.onload = (e) => {
        this.imageUrl = e.target.result

        const userId = localStorage.getItem('user_id')
        const users = JSON.parse(localStorage.getItem('users') || '[]')
        const index = users.findIndex(u => String(u.id) === userId)

        if (index !== -1) {
          users[index].avatar = this.imageUrl
          localStorage.setItem('users', JSON.stringify(users))
        }

        this.$message.success('头像上传成功!')
        setTimeout(() => {
          window.location.reload()
        }, 1000)
      }
      reader.readAsDataURL(file)
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

.hidden-upload {
  display: none;
}

.avatar-uploader {
  cursor: pointer;
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
