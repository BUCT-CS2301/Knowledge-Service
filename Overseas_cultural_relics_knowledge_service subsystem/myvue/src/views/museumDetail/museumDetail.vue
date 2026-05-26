<template>
  <div class="museum-detail">
    <MainHeader></MainHeader>

    <div class="detail-container">
      <!-- 博物馆图片 -->
      <div class="museum-hero">
        <img :src="museum.image" :alt="museum.name" class="hero-image">
        <div class="hero-overlay">
          <h1 class="museum-title">{{ museum.name }}</h1>
          <p class="museum-location">{{ museum.location }}</p>
        </div>
      </div>

      <!-- 博物馆信息 -->
      <div class="museum-content">
        <div class="info-section">
          <h2 class="section-title">博物馆介绍</h2>
          <p class="description">{{ museum.description }}</p>
        </div>

        <div class="info-section">
          <h2 class="section-title">基本信息</h2>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">成立时间</span>
              <span class="info-value">{{ museum.founded }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">藏品数量</span>
              <span class="info-value">{{ museum.collectionCount }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">展厅数量</span>
              <span class="info-value">{{ museum.galleryCount }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">年度访客</span>
              <span class="info-value">{{ museum.visitorsPerYear }}</span>
            </div>
          </div>
        </div>

        <div class="info-section">
          <h2 class="section-title">著名藏品</h2>
          <div class="famous-relics">
            <div class="relic-item" v-for="relic in famousRelics" :key="relic.id">
              <img :src="relic.image" :alt="relic.name" class="relic-img">
              <div class="relic-info">
                <span class="relic-name">{{ relic.name }}</span>
                <span class="relic-period">{{ relic.period }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="info-section">
          <h2 class="section-title">参观信息</h2>
          <div class="visit-info">
            <div class="visit-item">
              <span class="visit-icon">地点：</span>
              <span class="visit-text">{{ museum.address }}</span>
            </div>
            <div class="visit-item">
              <span class="visit-icon">时间：</span>
              <span class="visit-text">{{ museum.openHours }}</span>
            </div>
            <div class="visit-item">
              <span class="visit-icon">门票：</span>
              <span class="visit-text">{{ museum.ticketInfo }}</span>
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

export default {
  name: 'museumDetail',
  components: { MainHeader, MainFooter },
  data () {
    return {
      museum: {
        id: 1,
        name: '',
        location: '',
        image: '',
        description: '',
        founded: '',
        collectionCount: '',
        galleryCount: '',
        visitorsPerYear: '',
        address: '',
        openHours: '',
        ticketInfo: ''
      },
      famousRelics: []
    }
  },
  created () {
    this.loadMuseumDetail()
  },
  methods: {
    loadMuseumDetail () {
      const museumId = this.$route.query.id
      const museumName = this.$route.query.name

      // 根据博物馆名称加载对应的详情数据
      this.museum = this.getMuseumData(museumName || museumId)
      this.famousRelics = this.getFamousRelics(museumName || museumId)
    },
    getMuseumData (museumName) {
      const museumData = {
        'The Cleveland Museum of Art': {
          id: 1,
          name: 'The Cleveland Museum of Art',
          location: 'Cleveland, Ohio, United States',
          image: 'https://picsum.photos/seed/cleveland/800/400',
          description: 'The Cleveland Museum of Art (CMA) is an art museum in Cleveland, Ohio, located in the Wade Park District, in the University Circle neighborhood on the city\'s east side. The museum was founded in 1913 with a $1 million endowment from prominent Cleveland industrialists Hinman Hurlbut and John Huntington. It opened to the public in 1916 in a Beaux-Arts building designed by the firm of Hubbell & Benes.',
          founded: '1913年',
          collectionCount: '超过61,000件',
          galleryCount: '61个展厅',
          visitorsPerYear: '约50万人次',
          address: '11150 East Boulevard, Cleveland, OH 44106',
          openHours: '周二至周日 10:00-17:00',
          ticketInfo: '免费参观，特展需购票'
        },
        'The Nelson-Atkins Museum of Art': {
          id: 2,
          name: 'The Nelson-Atkins Museum of Art',
          location: 'Kansas City, Missouri, United States',
          image: 'https://picsum.photos/seed/nelson/800/400',
          description: 'The Nelson-Atkins Museum of Art is an art museum in Kansas City, Missouri, known for its encyclopedic collection of art from nearly every continent and culture, and especially for its extensive collection of Asian art. The museum was founded in 1933 with funds from the estates of William Rockhill Nelson, a prominent Kansas City newspaper editor, and his wife, Mary McAfee Nelson.',
          founded: '1933年',
          collectionCount: '超过35,000件',
          galleryCount: '35个展厅',
          visitorsPerYear: '约40万人次',
          address: '4525 Oak Street, Kansas City, MO 64111',
          openHours: '周三至周日 10:00-17:00',
          ticketInfo: '免费参观'
        },
        'Penn Museum': {
          id: 3,
          name: 'Penn Museum',
          location: 'Philadelphia, Pennsylvania, United States',
          image: 'https://picsum.photos/seed/penn/800/400',
          description: 'The University of Pennsylvania Museum of Archaeology and Anthropology, commonly known as the Penn Museum, is an archaeology and anthropology museum located on the campus of the University of Pennsylvania in Philadelphia. Founded in 1887, the museum has an extensive collection of over one million objects from around the world.',
          founded: '1887年',
          collectionCount: '超过100万件',
          galleryCount: '20个展厅',
          visitorsPerYear: '约30万人次',
          address: '3260 South Street, Philadelphia, PA 19104',
          openHours: '周二至周日 10:00-17:00',
          ticketInfo: '成人$15，学生$10'
        },
        'The British Museum': {
          id: 4,
          name: 'The British Museum',
          location: 'London, United Kingdom',
          image: 'https://picsum.photos/seed/british/800/400',
          description: 'The British Museum is a public institution dedicated to human history, art and culture located in the Bloomsbury area of London. Its permanent collection of eight million works is among the largest and most comprehensive in existence, having been sourced during the era of the British Empire.',
          founded: '1753年',
          collectionCount: '超过800万件',
          galleryCount: '94个展厅',
          visitorsPerYear: '约600万人次',
          address: 'Great Russell St, Bloomsbury, London WC1B 3DG',
          openHours: '周四至周二 10:00-17:00',
          ticketInfo: '免费参观'
        }
      }

      return museumData[museumName] || museumData['The British Museum']
    },
    getFamousRelics (museumName) {
      const relicsData = {
        'The Cleveland Museum of Art': [
          { id: 149146, name: 'Raft Cup', period: '1300s-1400s', image: 'https://openaccess-cdn.clevelandart.org/1977.7/1977.7_web.jpg' },
          { id: 137198, name: 'Jar with Lion-Head Handles', period: '1300s', image: 'https://openaccess-cdn.clevelandart.org/1962.154/1962.154_web.jpg' },
          { id: 130130, name: 'Cup with Daoist Figures', period: '1736-95', image: 'https://openaccess-cdn.clevelandart.org/1952.510/1952.510_web.jpg' }
        ],
        'The Nelson-Atkins Museum of Art': [
          { id: 1, name: 'Blue and White Porcelain', period: 'Ming Dynasty', image: 'https://picsum.photos/seed/nelson1/200/200' },
          { id: 2, name: 'Buddhist Sculpture', period: 'Tang Dynasty', image: 'https://picsum.photos/seed/nelson2/200/200' },
          { id: 3, name: 'Chinese Painting', period: 'Song Dynasty', image: 'https://picsum.photos/seed/nelson3/200/200' }
        ],
        'Penn Museum': [
          { id: 1, name: 'Egyptian Mummy', period: 'Ancient Egypt', image: 'https://picsum.photos/seed/penn1/200/200' },
          { id: 2, name: 'Chinese Bronze', period: 'Shang Dynasty', image: 'https://picsum.photos/seed/penn2/200/200' },
          { id: 3, name: 'Roman Statue', period: 'Ancient Rome', image: 'https://picsum.photos/seed/penn3/200/200' }
        ],
        'The British Museum': [
          { id: 1, name: 'Rosetta Stone', period: '196 BCE', image: 'https://picsum.photos/seed/british1/200/200' },
          { id: 2, name: 'Parthenon Marbles', period: '447-438 BCE', image: 'https://picsum.photos/seed/british2/200/200' },
          { id: 3, name: 'Terracotta Army', period: '210 BCE', image: 'https://picsum.photos/seed/british3/200/200' }
        ]
      }

      return relicsData[museumName] || relicsData['The British Museum']
    }
  }
}
</script>

<style lang="scss" scoped>
.museum-detail {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.museum-hero {
  position: relative;
  height: 400px;
  border-radius: 12px;
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
    padding: 30px;
    background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));

    .museum-title {
      color: #fff;
      font-size: 36px;
      font-weight: bold;
      margin-bottom: 10px;
    }

    .museum-location {
      color: #fff;
      font-size: 18px;
      opacity: 0.9;
    }
  }
}

.museum-content {
  background-color: #fff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.info-section {
  margin-bottom: 40px;

  .section-title {
    font-size: 24px;
    font-weight: bold;
    color: #333;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 2px solid #8B7355;
  }

  .description {
    font-size: 16px;
    line-height: 1.8;
    color: #666;
    text-align: justify;
  }
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;

  .info-item {
    background-color: #f9f9f9;
    padding: 20px;
    border-radius: 8px;

    .info-label {
      display: block;
      font-size: 14px;
      color: #999;
      margin-bottom: 8px;
    }

    .info-value {
      font-size: 18px;
      font-weight: bold;
      color: #333;
    }
  }
}

.famous-relics {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;

  .relic-item {
    width: calc(33.33% - 14px);
    background-color: #f9f9f9;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    transition: transform 0.3s ease;

    &:hover {
      transform: translateY(-5px);
    }

    .relic-img {
      width: 100%;
      height: 180px;
      object-fit: cover;
    }

    .relic-info {
      padding: 15px;

      .relic-name {
        display: block;
        font-size: 16px;
        font-weight: bold;
        color: #333;
        margin-bottom: 5px;
      }

      .relic-period {
        font-size: 14px;
        color: #999;
      }
    }
  }
}

.visit-info {
  .visit-item {
    display: flex;
    align-items: center;
    padding: 15px 0;
    border-bottom: 1px solid #eee;

    &:last-child {
      border-bottom: none;
    }

    .visit-icon {
      font-size: 24px;
      margin-right: 15px;
    }

    .visit-text {
      font-size: 16px;
      color: #333;
    }
  }
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }

  .famous-relics {
    .relic-item {
      width: calc(50% - 10px);
    }
  }

  .museum-hero {
    height: 250px;

    .hero-overlay {
      padding: 20px;

      .museum-title {
        font-size: 24px;
      }
    }
  }
}
</style>
