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
          <div class="museum-card" v-for="museum in hotMuseums" :key="museum.id">
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
      recommendedRelics: [
        {
          id: 1,
          name: '多乳博局式镜',
          image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=ancient%20chinese%20bronze%20mirror%20with%20intricate%20patterns&image_size=square_hd',
          description: '汉代青铜镜，纹饰精美，具有极高的艺术价值和历史价值。',
          museum: '大英博物馆',
          period: '汉代'
        },
        {
          id: 2,
          name: '翡翠西瓜',
          image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=jade%20jadeite%20watermelon%20sculpture%20Chinese%20art&image_size=square_hd',
          description: '清代翡翠雕刻艺术品，色泽鲜艳，工艺精湛。',
          museum: '大都会博物馆',
          period: '清代'
        },
        {
          id: 3,
          name: '敦煌壁画',
          image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Dunhuang%20mural%20painting%20Buddhist%20art%20ancient%20China&image_size=square_hd',
          description: '唐代敦煌莫高窟壁画，展现了古代丝绸之路的繁荣景象。',
          museum: '大英博物馆',
          period: '唐代'
        },
        {
          id: 4,
          name: '女史箴图',
          image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=ancient%20Chinese%20scroll%20painting%20women%20figures%20traditional&image_size=square_hd',
          description: '东晋顾恺之画作摹本，是中国绘画史上的珍品。',
          museum: '大英博物馆',
          period: '东晋'
        }
      ],
      hotMuseums: [
        { id: 1, name: '大英博物馆', location: '英国伦敦', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=British%20Museum%20London%20historic%20building%20architecture&image_size=square_hd' },
        { id: 2, name: '大都会博物馆', location: '美国纽约', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Metropolitan%20Museum%20New%20York%20classic%20architecture&image_size=square_hd' },
        { id: 3, name: '卢浮宫', location: '法国巴黎', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Louvre%20Museum%20Paris%20pyramid%20famous%20landmark&image_size=square_hd' },
        { id: 4, name: '故宫博物院', location: '中国北京', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Forbidden%20City%20Beijing%20Chinese%20imperial%20palace&image_size=square_hd' }
      ],
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
  methods: {
    handleSearch () {
      if (this.searchKeyword.trim()) {
        this.$router.push({ path: '/keyword', query: { keyword: this.searchKeyword } })
      }
    },
    viewDetail (relic) {
      this.selectedRelic = relic
      this.detailVisible = true
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
