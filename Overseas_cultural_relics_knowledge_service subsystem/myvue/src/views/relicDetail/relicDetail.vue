<template>
  <div class="relic-detail">
    <MainHeader></MainHeader>

    <div class="detail-container">
      <!-- 文物大图 -->
      <div class="relic-hero">
        <img :src="relic.image" :alt="relic.name" class="hero-image">
        <div class="hero-overlay">
          <h1 class="relic-title">{{ relic.name }}</h1>
          <p class="relic-period">{{ relic.period }}</p>
        </div>
      </div>

      <!-- 文物信息 -->
      <div class="relic-content">
        <!-- 基本信息卡片 -->
        <div class="info-card">
          <h2 class="card-title">基本信息</h2>
          <div class="info-row">
            <span class="info-label">文物名称</span>
            <span class="info-value">{{ relic.name }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">所属年代</span>
            <span class="info-value">{{ relic.period }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">收藏博物馆</span>
            <span class="info-value">{{ relic.museum }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">文物编号</span>
            <span class="info-value">#{{ relic.id }}</span>
          </div>
        </div>

        <!-- 详细描述 -->
        <div class="description-card">
          <h2 class="card-title">文物简介</h2>
          <p class="description-text">{{ relic.description }}</p>
        </div>

        <!-- 评论区 -->
        <div class="comment-section">
          <h2 class="card-title">用户评论</h2>

          <!-- 评论输入框 -->
          <div class="comment-input-box">
            <textarea
              v-model="newComment"
              class="comment-input"
              placeholder="写下你的评论..."
              rows="3"
            ></textarea>
            <button class="submit-btn" @click="submitComment">发表评论</button>
          </div>

          <!-- 评论列表 -->
          <div class="comment-list">
            <div class="comment-item" v-for="comment in comments" :key="comment.id">
              <div class="comment-header">
                <span class="comment-author">{{ comment.userName || '匿名用户' }}</span>
                <span class="comment-time">{{ comment.time }}</span>
              </div>
              <p class="comment-content">{{ comment.content }}</p>
            </div>

            <div class="no-comment" v-if="comments.length === 0">
              <p>暂无评论，快来发表第一条评论吧！</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <MainFooter></MainFooter>
  </div>
</template>

<script>
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'
import axios from 'axios'

export default {
  name: 'relicDetail',
  components: { MainHeader, MainFooter },
  data () {
    return {
      relic: {
        id: '',
        name: '',
        image: '',
        description: '',
        museum: '',
        period: ''
      },
      isCollected: false,
      newComment: '',
      comments: []
    }
  },
  created () {
    this.loadRelicDetail()
    this.loadComments()
    this.checkCollectionStatus()
  },
  methods: {
    async loadRelicDetail () {
      const relicId = this.$route.query.id
      const relicName = this.$route.query.name

      // 优先从后端获取数据
      try {
        const response = await axios.get(`/api/v1/data/relics/${relicId}`)
        if (response.data.code === 200) {
          const data = response.data.data
          this.relic = {
            id: data.objectId || relicId,
            name: data.title || relicName,
            image: data.imageUrl || 'https://via.placeholder.com/400/300?text=No+Image',
            description: data.description || '暂无详细描述',
            museum: data.museumId || '未知博物馆',
            period: data.period || '未知年代'
          }
        }
      } catch (error) {
        console.error('获取文物详情失败:', error)
        // 使用默认数据
        this.relic = this.getDefaultRelic(relicId, relicName)
      }
    },
    getDefaultRelic (id, name) {
      const relics = {
        149146: {
          id: 149146,
          name: 'Raft Cup',
          image: 'https://openaccess-cdn.clevelandart.org/1977.7/1977.7_web.jpg',
          description: 'The figure watching the stars is believed to be the messenger Zhang Qian (died 114 BCE). Legend says he lost his way in the Milky Way, where he met the Weaving Maid who gave him a stone from her loom.',
          museum: 'The Cleveland Museum of Art',
          period: '1300s-1400s'
        },
        137198: {
          id: 137198,
          name: 'Jar with Lion-Head Handles',
          image: 'https://openaccess-cdn.clevelandart.org/1962.154/1962.154_web.jpg',
          description: 'Appreciated for its strong profile, brilliant blue color, and firm delineation of the decorative patterns, this jar is a classic example of Yuan dynasty blue-and-white ware.',
          museum: 'The Cleveland Museum of Art',
          period: '1300s'
        },
        130130: {
          id: 130130,
          name: 'Cup with Daoist Figures',
          image: 'https://openaccess-cdn.clevelandart.org/1952.510/1952.510_web.jpg',
          description: 'During the Qing dynasty, Suzhou\'s best products were sent north to the capital. Those that met imperial approval were sometimes graced with Qianlong\'s mark.',
          museum: 'The Cleveland Museum of Art',
          period: '1736-95'
        },
        147084: {
          id: 147084,
          name: 'Virupa',
          image: 'https://openaccess-cdn.clevelandart.org/1972.96/1972.96_web.jpg',
          description: 'Virupa is one of the great teachers in the history of tantric Buddhism. His posture references his ability to stop the sun; as an enlightened being, he can control phenomena of nature.',
          museum: 'The Cleveland Museum of Art',
          period: 'early 1400s'
        }
      }
      return relics[id] || {
        id: id,
        name: name || '未知文物',
        image: 'https://picsum.photos/seed/relic/400/300',
        description: '暂无详细描述',
        museum: '未知博物馆',
        period: '未知年代'
      }
    },
    loadComments () {
      const relicId = this.$route.query.id
      const allComments = JSON.parse(localStorage.getItem('comments') || '[]')
      this.comments = allComments.filter(c => c.relicId == relicId)
    },
    checkCollectionStatus () {
      if (!localStorage.getItem('username')) return
      const relicId = this.$route.query.id
      const collections = JSON.parse(localStorage.getItem('collections') || '[]')
      this.isCollected = collections.some(c => c.relicId == relicId)
    },
    toggleCollect () {
      if (!localStorage.getItem('username')) {
        this.$message.warning('请先登录')
        return
      }

      const relicId = this.$route.query.id
      const collections = JSON.parse(localStorage.getItem('collections') || '[]')
      const index = collections.findIndex(c => c.relicId == relicId)

      if (index !== -1) {
        collections.splice(index, 1)
        localStorage.setItem('collections', JSON.stringify(collections))
        this.isCollected = false
        this.$message.success('取消收藏成功')
      } else {
        collections.push({
          id: Date.now(),
          relicId: relicId,
          relicName: this.relic.name,
          relicImage: this.relic.image,
          userId: localStorage.getItem('user_id'),
          userName: localStorage.getItem('user_name'),
          time: new Date().toLocaleString()
        })
        localStorage.setItem('collections', JSON.stringify(collections))
        this.isCollected = true
        this.$message.success('收藏成功')
      }
    },
    focusComment () {
      document.querySelector('.comment-input').focus()
    },
    submitComment () {
      if (!localStorage.getItem('username')) {
        this.$message.warning('请先登录')
        return
      }
      if (!this.newComment.trim()) {
        this.$message.warning('请输入评论内容')
        return
      }

      const relicId = this.$route.query.id
      const allComments = JSON.parse(localStorage.getItem('comments') || '[]')
      const newCommentObj = {
        id: Date.now(),
        relicId: relicId,
        relicName: this.relic.name,
        userId: localStorage.getItem('user_id'),
        userName: localStorage.getItem('user_name') || localStorage.getItem('username'),
        content: this.newComment,
        time: new Date().toLocaleString()
      }
      allComments.push(newCommentObj)
      localStorage.setItem('comments', JSON.stringify(allComments))
      this.comments.push(newCommentObj)
      this.newComment = ''
      this.$message.success('评论成功')
    }
  }
}
</script>

<style lang="scss" scoped>
.relic-detail {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.detail-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.relic-hero {
  position: relative;
  height: 400px;
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 30px;

  .hero-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .hero-overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 40px;
    background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));

    .relic-title {
      color: #fff;
      font-size: 42px;
      font-weight: bold;
      margin-bottom: 10px;
      text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
    }

    .relic-period {
      color: #fff;
      font-size: 18px;
      opacity: 0.9;
    }
  }
}

.relic-content {
  display: grid;
  gap: 20px;
}

.info-card {
  background: #fff;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

  .card-title {
    font-size: 20px;
    font-weight: bold;
    color: #333;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 2px solid #8B4513;
  }

  .info-row {
    display: flex;
    justify-content: space-between;
    padding: 12px 0;
    border-bottom: 1px solid #eee;

    &:last-child {
      border-bottom: none;
    }

    .info-label {
      font-size: 15px;
      color: #999;
    }

    .info-value {
      font-size: 15px;
      font-weight: 500;
      color: #333;
    }
  }
}

.action-card {
  display: flex;
  flex-direction: column;
  gap: 15px;

  .action-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    padding: 15px 20px;
    border: none;
    border-radius: 10px;
    font-size: 16px;
    cursor: pointer;
    transition: all 0.3s;

    .btn-icon {
      font-size: 20px;
    }

    .btn-text {
      font-weight: 500;
    }
  }

  .collect-btn {
    background: #fff0f0;
    color: #e74c3c;

    &:hover {
      background: #ffe0e0;
    }

    &.collected {
      background: #ffe0e0;
    }
  }

  .comment-btn {
    background: #f0f0f0;
    color: #333;

    &:hover {
      background: #e0e0e0;
    }
  }
}

.description-card {
  background: #fff;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

  .card-title {
    font-size: 20px;
    font-weight: bold;
    color: #333;
    margin-bottom: 15px;
    padding-bottom: 10px;
    border-bottom: 2px solid #8B4513;
  }

  .description-text {
    font-size: 16px;
    line-height: 1.8;
    color: #666;
    text-align: justify;
  }
}

.comment-section {
  background: #fff;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

  .card-title {
    font-size: 20px;
    font-weight: bold;
    color: #333;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 2px solid #8B4513;
  }
}

.comment-input-box {
  display: flex;
  gap: 15px;
  margin-bottom: 25px;

  .comment-input {
    flex: 1;
    padding: 15px;
    border: 1px solid #ddd;
    border-radius: 10px;
    font-size: 15px;
    resize: none;
    outline: none;

    &:focus {
      border-color: #8B4513;
    }
  }

  .submit-btn {
    padding: 0 30px;
    background: #8B4513;
    color: #fff;
    border: none;
    border-radius: 10px;
    font-size: 15px;
    cursor: pointer;
    transition: background 0.3s;

    &:hover {
      background: #6B3510;
    }
  }
}

.comment-list {
  .comment-item {
    padding: 20px 0;
    border-bottom: 1px solid #eee;

    &:last-child {
      border-bottom: none;
    }

    .comment-header {
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;

      .comment-author {
        font-weight: 600;
        color: #333;
      }

      .comment-time {
        font-size: 14px;
        color: #999;
      }
    }

    .comment-content {
      font-size: 15px;
      color: #666;
      line-height: 1.6;
    }
  }

  .no-comment {
    text-align: center;
    padding: 40px;
    color: #999;
  }
}

@media (max-width: 768px) {
  .info-main {
    grid-template-columns: 1fr;
  }

  .relic-hero {
    height: 250px;

    .hero-overlay {
      padding: 20px;

      .relic-title {
        font-size: 28px;
      }
    }
  }

  .comment-input-box {
    flex-direction: column;
  }
}
</style>
