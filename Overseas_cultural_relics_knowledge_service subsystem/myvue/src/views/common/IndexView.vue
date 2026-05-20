<template>
  <div class="main-index">
    <MainHeader></MainHeader>

    <div class="hero-section">
      <div class="carousel">
        <el-carousel :interval="4000" type="card" height="500px">
          <el-carousel-item v-for="(item, index) in carouselImages" :key="index">
            <img :src="item" class="carousel-img" alt="">
          </el-carousel-item>
        </el-carousel>
      </div>

      <div class="search-overlay">
        <div class="search-container">
          <h1 class="search-title">探索海外文物世界</h1>
          <p class="search-subtitle">发现珍贵的文化遗产，了解千年历史文明</p>
          <div class="search-box">
            <input
              type="text"
              v-model="searchKeyword"
              class="search-input"
              placeholder="搜索文物名称、博物馆、年代..."
              @keyup.enter="handleSearch"
            >
            <button class="search-button" @click="handleSearch">
              搜索
            </button>
          </div>
          <div class="hot-search">
            <span class="hot-label">热门搜索：</span>
            <span
              v-for="tag in hotTags"
              :key="tag"
              class="hot-tag"
              @click="searchKeyword = tag; handleSearch()"
            >
              {{ tag }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <div class="content-section">
      <div class="section-item">
        <h2 class="section-title">推荐文物信息</h2>
        <div class="relic-grid">
          <div class="relic-card" v-for="relic in recommendedRelics" :key="relic.id">
            <img :src="relic.image" :alt="relic.name" class="relic-image">
            <div class="relic-info">
              <span class="relic-name">{{ relic.name }}</span>
              <div class="relic-actions">
                <button class="view-btn" @click="viewDetail(relic)">查看详情</button>
                <button
                  class="action-btn"
                  :class="{ collected: isCollected(relic.id) }"
                  @click="toggleCollect(relic)"
                >
                  <span>{{ isCollected(relic.id) ? '❤️' : '🤍' }}</span>
                  <span>{{ isCollected(relic.id) ? '已收藏' : '收藏' }}</span>
                </button>
                <button class="action-btn" @click="openComment(relic)">
                  <span>💬</span>
                  <span>评论</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="section-item">
        <h2 class="section-title">热门博物馆</h2>
        <div class="museum-grid">
          <div class="museum-card" v-for="museum in hotMuseums" :key="museum.id" @click="viewMuseumDetail(museum)">
            <img :src="museum.image" :alt="museum.name" class="museum-image">
            <div class="museum-info">
              <span class="museum-name">{{ museum.name }}</span>
              <span class="museum-location">{{ museum.location }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="section-item">
        <h2 class="section-title">新闻资讯</h2>
        <div class="news-list">
          <div class="news-item" v-for="news in newsList" :key="news.id">
            <div class="news-dot"></div>
            <span class="news-title">{{ news.title }}</span>
            <span class="news-date">{{ news.date }}</span>
          </div>
        </div>
      </div>
    </div>

    <MainFooter></MainFooter>

    <el-dialog title="文物详情" :visible.sync="detailVisible" width="600px">
      <div v-if="selectedRelic" class="relic-detail">
        <img :src="selectedRelic.image" class="detail-image" :alt="selectedRelic.name">
        <div class="detail-info">
          <h3>{{ selectedRelic.name }}</h3>
          <p class="detail-desc">{{ selectedRelic.description }}</p>
          <p class="detail-museum">收藏于：{{ selectedRelic.museum }}</p>
          <p class="detail-period">年代：{{ selectedRelic.period }}</p>
        </div>
        <div class="detail-comments">
          <h4>评论 ({{ getRelicComments(selectedRelic.id).length }})</h4>
          <div v-if="getRelicComments(selectedRelic.id).length === 0" class="no-comments">暂无评论</div>
          <div v-else class="comment-list">
            <div v-for="comment in getRelicComments(selectedRelic.id)" :key="comment.id" class="comment-item">
              <span class="comment-user">{{ comment.user_name }}</span>
              <span class="comment-content">{{ comment.content }}</span>
              <span class="comment-time">{{ comment.time }}</span>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-input
          v-model="newComment"
          placeholder="发表评论"
          @keyup.enter="submitComment"
        ></el-input>
        <el-button type="primary" @click="submitComment">发表</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'
import axios from 'axios'
import carouselImg1 from '@/assets/index/1.png'
import carouselImg2 from '@/assets/index/2.png'
import carouselImg3 from '@/assets/index/3.png'
import carouselImg4 from '@/assets/index/4.png'
import carouselImg5 from '@/assets/index/5.png'

export default {
  components: {
    MainHeader,
    MainFooter
  },
  data () {
    return {
      searchKeyword: '',
      hotTags: ['青铜器', '敦煌壁画', '青花瓷', '大英博物馆', '女史箴图'],
      carouselImages: [
        carouselImg1,
        carouselImg2,
        carouselImg3,
        carouselImg4,
        carouselImg5
      ],
      recommendedRelics: [],
      hotMuseums: [],
      isLoading: true,  // 添加加载状态
      newsList: [
        { id: 1, title: '海外流失文物数字化回归项目启动', date: '2026-04-25' },
        { id: 2, title: '大英博物馆举办中国古代青铜器特展', date: '2026-04-23' },
        { id: 3, title: '敦煌壁画数字展在全球巡回展出', date: '2026-04-20' },
        { id: 4, title: '新发现唐代文物填补历史空白', date: '2026-04-18' },
        { id: 5, title: '数字博物馆云平台用户突破100万', date: '2026-04-15' }
      ],
      detailVisible: false,
      selectedRelic: null,
      newComment: ''
    }
  },
  created () {
    // 先显示默认数据，让页面立刻能看到内容
    this.recommendedRelics = this.getDefaultRelics()
    this.hotMuseums = this.getDefaultMuseums()
    this.isLoading = false

    // 然后在后台尝试加载API数据，不阻塞页面
    setTimeout(() => {
      this.fetchRecommendedRelics()
      this.fetchHotMuseums()
    }, 0)
  },
  methods: {
    // 获取推荐文物列表
    async fetchRecommendedRelics () {
      try {
        const accessToken = localStorage.getItem('accessToken')
        const headers = accessToken ? { Authorization: `Bearer ${accessToken}` } : {}
        const response = await axios.get('/api/v1/data/relics', {
          params: {
            page: 1,
            pageSize: 8
          },
          headers: headers,
          timeout: 3000  // 添加3秒超时，避免长时间等待
        })
        if (response.data.code === 200) {
          // 将后端数据映射到前端需要的格式
          this.recommendedRelics = response.data.data.records.map(relic => ({
            id: relic.objectId,
            name: relic.title,
            image: relic.imageUrl || 'https://via.placeholder.com/300x200?text=No+Image',
            description: relic.description || '暂无描述',
            museum: relic.museumId || '未知博物馆',
            period: relic.period || '未知年代'
          }))
          console.log('成功从后端获取文物数据:', this.recommendedRelics.length, '件')
        }
      } catch (error) {
        console.error('获取推荐文物失败:', error)
        // 后端服务不可用时，静默使用默认数据
        this.recommendedRelics = this.getDefaultRelics()
      }
    },
    // 获取热门博物馆列表
    async fetchHotMuseums () {
      try {
        const accessToken = localStorage.getItem('accessToken')
        const headers = accessToken ? { Authorization: `Bearer ${accessToken}` } : {}
        const response = await axios.get('/api/v1/data/museums', {
          params: {
            page: 1,
            pageSize: 4
          },
          headers: headers,
          timeout: 3000  // 添加3秒超时，避免长时间等待
        })
        if (response.data.code === 200) {
          // 将后端数据映射到前端需要的格式
          this.hotMuseums = response.data.data.records.map(museum => ({
            id: museum.objectId,
            name: museum.nameCn || museum.name,
            location: museum.location || '未知地点',
            image: museum.imageUrl || 'https://via.placeholder.com/300x200?text=No+Image'
          }))
          console.log('成功从后端获取博物馆数据:', this.hotMuseums.length, '个')
        }
      } catch (error) {
        console.error('获取热门博物馆失败:', error)
        // 后端服务不可用时，静默使用默认数据
        this.hotMuseums = this.getDefaultMuseums()
      }
    },
    // 默认文物数据（使用数据库组爬取的真实数据）
    getDefaultRelics () {
      return [
        {
          id: 149146,
          name: 'Raft Cup',
          image: 'https://openaccess-cdn.clevelandart.org/1977.7/1977.7_web.jpg',
          description: 'The figure watching the stars is believed to be the messenger Zhang Qian (died 114 BCE). Legend says he lost his way in the Milky Way, where he met the Weaving Maid who gave him a stone from her loom.',
          museum: 'The Cleveland Museum of Art',
          period: '1300s-1400s'
        },
        {
          id: 137198,
          name: 'Jar with Lion-Head Handles',
          image: 'https://openaccess-cdn.clevelandart.org/1962.154/1962.154_web.jpg',
          description: 'Appreciated for its strong profile, brilliant blue color, and firm delineation of the decorative patterns, this jar is a classic example of Yuan dynasty blue-and-white ware.',
          museum: 'The Cleveland Museum of Art',
          period: '1300s'
        },
        {
          id: 130130,
          name: 'Cup with Daoist Figures',
          image: 'https://openaccess-cdn.clevelandart.org/1952.510/1952.510_web.jpg',
          description: 'During the Qing dynasty, Suzhou\'s best products were sent north to the capital. Those that met imperial approval were sometimes graced with Qianlong\'s mark.',
          museum: 'The Cleveland Museum of Art',
          period: '1736-95'
        },
        {
          id: 147084,
          name: 'Virupa',
          image: 'https://openaccess-cdn.clevelandart.org/1972.96/1972.96_web.jpg',
          description: 'Virupa is one of the great teachers in the history of tantric Buddhism. His posture references his ability to stop the sun; as an enlightened being, he can control phenomena of nature.',
          museum: 'The Cleveland Museum of Art',
          period: 'early 1400s'
        }
      ]
    },
    // 默认博物馆数据（使用数据库组爬取的真实博物馆信息）
    getDefaultMuseums () {
      return [
        { id: 1, name: 'The Cleveland Museum of Art', location: 'Cleveland, Ohio, United States', image: 'https://picsum.photos/seed/cleveland/300/200' },
        { id: 2, name: 'The Nelson-Atkins Museum of Art', location: 'Kansas City, Missouri, United States', image: 'https://picsum.photos/seed/nelson/300/200' },
        { id: 3, name: 'Penn Museum', location: 'Philadelphia, Pennsylvania, United States', image: 'https://picsum.photos/seed/penn/300/200' },
        { id: 4, name: 'The British Museum', location: 'London, United Kingdom', image: 'https://picsum.photos/seed/british/300/200' }
      ]
    },
    handleSearch () {
      if (this.searchKeyword.trim()) {
        this.$router.push({ path: '/keyword', query: { keyword: this.searchKeyword } })
      }
    },
    viewDetail (relic) {
      this.$router.push({ path: '/relicDetail', query: { id: relic.id, name: relic.name } })
    },
    viewMuseumDetail (museum) {
      this.$router.push({ path: '/museumDetail', query: { id: museum.id, name: museum.name } })
    },
    openComment (relic) {
      this.selectedRelic = relic
      this.detailVisible = true
    },
    isCollected (relicId) {
      if (!localStorage.getItem('username')) return false
      const collections = JSON.parse(localStorage.getItem('collections') || '[]')
      return collections.some(c => c.relicId === relicId)
    },
    toggleCollect (relic) {
      if (!localStorage.getItem('username')) {
        this.$message.warning('请先登录')
        return
      }

      const collections = JSON.parse(localStorage.getItem('collections') || '[]')
      const index = collections.findIndex(c => c.relicId === relic.id)

      if (index !== -1) {
        collections.splice(index, 1)
        localStorage.setItem('collections', JSON.stringify(collections))
        this.$message.success('取消收藏成功')
      } else {
        collections.push({
          id: Date.now(),
          relicId: relic.id,
          relicName: relic.name,
          relicImage: relic.image,
          userId: localStorage.getItem('user_id'),
          userName: localStorage.getItem('user_name'),
          time: new Date().toLocaleString()
        })
        localStorage.setItem('collections', JSON.stringify(collections))
        this.$message.success('收藏成功')
      }
    },
    getRelicComments (relicId) {
      const comments = JSON.parse(localStorage.getItem('comments') || '[]')
      return comments.filter(c => c.relicId === relicId)
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

      const comments = JSON.parse(localStorage.getItem('comments') || '[]')
      comments.push({
        id: Date.now(),
        relicId: this.selectedRelic.id,
        relicName: this.selectedRelic.name,
        userId: localStorage.getItem('user_id'),
        user_name: localStorage.getItem('user_name'),
        content: this.newComment,
        time: new Date().toLocaleString()
      })
      localStorage.setItem('comments', JSON.stringify(comments))
      this.newComment = ''
      this.$message.success('评论成功')
    }
  }
}
</script>

<style lang="scss" scoped>
.main-index {
  min-height: 100vh;
  background: #f5f5f5;
}

.hero-section {
  position: relative;
  height: 500px;
  overflow: hidden;

  .carousel {
    position: absolute;
    width: 100%;
    height: 100%;
  }

  .carousel-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .search-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;

    .search-container {
      text-align: center;
      color: white;

      .search-title {
        font-size: 42px;
        font-weight: 700;
        margin-bottom: 15px;
        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
      }

      .search-subtitle {
        font-size: 18px;
        margin-bottom: 30px;
        opacity: 0.9;
      }

      .search-box {
        display: flex;
        justify-content: center;
        gap: 10px;
        margin-bottom: 20px;
        z-index: 100;
        position: relative;

        .search-input {
          width: 500px;
          height: 45px;
          padding: 0 20px;
          border: none;
          border-radius: 23px;
          font-size: 16px;
          outline: none;
          background: white;
          color: #333;
        }

        .search-button {
          height: 45px;
          padding: 0 35px;
          background: #8B4513;
          color: white;
          border: none;
          border-radius: 23px;
          font-size: 16px;
          cursor: pointer;
          transition: background 0.3s;

          &:hover {
            background: #6B3510;
          }
        }
      }

      .hot-search {
        .hot-label {
          font-size: 14px;
          opacity: 0.8;
        }

        .hot-tag {
          display: inline-block;
          margin: 0 8px;
          padding: 5px 15px;
          background: rgba(255, 255, 255, 0.2);
          border-radius: 15px;
          font-size: 14px;
          cursor: pointer;
          transition: all 0.3s;

          &:hover {
            background: rgba(255, 255, 255, 0.3);
          }
        }
      }
    }
  }
}

.content-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;

  .section-item {
    margin-bottom: 50px;

    .section-title {
      font-size: 22px;
      font-weight: 600;
      color: #333;
      margin-bottom: 20px;
      padding-left: 15px;
      border-left: 4px solid #8B4513;
    }
  }

  .relic-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 15px;

    .relic-card {
      background: white;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      transition: transform 0.3s, box-shadow 0.3s;

      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
      }

      .relic-image {
        width: 100%;
        height: 200px;
        object-fit: cover;
      }

      .relic-info {
        padding: 15px;

        .relic-name {
          display: block;
          font-size: 15px;
          font-weight: 500;
          color: #333;
          margin-bottom: 10px;
        }

        .relic-actions {
          display: flex;
          gap: 10px;

          .view-btn {
            flex: 1;
            padding: 8px 15px;
            background: #8B4513;
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 13px;
            cursor: pointer;
            transition: background 0.3s;

            &:hover {
              background: #6B3510;
            }
          }

          .action-btn {
            display: flex;
            align-items: center;
            gap: 5px;
            padding: 8px 12px;
            background: #f5f5f5;
            color: #666;
            border: none;
            border-radius: 6px;
            font-size: 12px;
            cursor: pointer;
            transition: all 0.3s;

            &:hover {
              background: #eee;
            }

            &.collected {
              background: #fff0f0;
              color: #e74c3c;
            }
          }
        }
      }
    }
  }

  .museum-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 15px;

    .museum-card {
      background: white;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      cursor: pointer;
      transition: transform 0.3s, box-shadow 0.3s;

      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
      }

      .museum-image {
        width: 100%;
        height: 180px;
        object-fit: cover;
      }

      .museum-info {
        padding: 15px;

        .museum-name {
          display: block;
          font-size: 15px;
          font-weight: 500;
          color: #333;
          margin-bottom: 5px;
        }

        .museum-location {
          font-size: 13px;
          color: #999;
        }
      }
    }
  }

  .news-list {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

    .news-item {
      display: flex;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #eee;

      &:last-child {
        border-bottom: none;
      }

      .news-dot {
        width: 8px;
        height: 8px;
        background: #8B4513;
        border-radius: 50%;
        margin-right: 12px;
      }

      .news-title {
        flex: 1;
        font-size: 14px;
        color: #333;
        cursor: pointer;
        transition: color 0.3s;

        &:hover {
          color: #8B4513;
        }
      }

      .news-date {
        font-size: 13px;
        color: #999;
      }
    }
  }
}

.relic-detail {
  .detail-image {
    width: 100%;
    height: 300px;
    object-fit: cover;
    border-radius: 8px;
    margin-bottom: 20px;
  }

  .detail-info {
    margin-bottom: 20px;

    h3 {
      font-size: 22px;
      font-weight: 600;
      color: #333;
      margin-bottom: 10px;
    }

    .detail-desc {
      font-size: 14px;
      color: #666;
      line-height: 1.6;
      margin-bottom: 10px;
    }

    .detail-museum, .detail-period {
      font-size: 13px;
      color: #999;
      margin-bottom: 5px;
    }
  }

  .detail-comments {
    .comment-list {
      max-height: 200px;
      overflow-y: auto;
    }

    .comment-item {
      padding: 10px 0;
      border-bottom: 1px solid #eee;

      .comment-user {
        font-weight: 500;
        color: #8B4513;
        margin-right: 10px;
      }

      .comment-content {
        color: #333;
        margin-right: 10px;
      }

      .comment-time {
        font-size: 12px;
        color: #999;
      }
    }

    .no-comments {
      text-align: center;
      color: #999;
      padding: 20px;
    }
  }
}
</style>
