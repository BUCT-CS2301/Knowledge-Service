<template>
  <div class="visualization-index">
    <MainHeader></MainHeader>

    <div class="visualization-content">
      <div class="page-title">
        <h1>文物数据可视化</h1>
        <p>多维度展示海外文物知识图谱</p>
      </div>

      <div class="visualization-grid">
        <router-link to="/visualization/knowledge-graph" class="visual-card knowledge-graph-card">
          <h3>知识图谱关系图</h3>
          <p>采用力导向图展示文物实体及其关联关系，支持节点点击展开、悬停显示属性</p>
          <div class="card-arrow">→</div>
        </router-link>

        <router-link to="/visualization/timeline" class="visual-card timeline-card">
          <h3>文物时间轴</h3>
          <p>以时间轴形式展示各历史时期的文物分布，支持按朝代或自定义时间段筛选</p>
          <div class="card-arrow">→</div>
        </router-link>

        <router-link to="/visualization/map" class="visual-card map-card">
          <h3>文物地理分布</h3>
          <p>以世界地图形式展示文物现藏博物馆的地理位置，直观呈现中国文物海外流散现状</p>
          <div class="card-arrow">→</div>
        </router-link>

        <router-link to="/visualization/dashboard" class="visual-card dashboard-card">
          <h3>统计分析看板</h3>
          <p>以图表形式展示文物数量、类型占比、朝代分布、博物馆藏量等统计数据</p>
          <div class="card-arrow">→</div>
        </router-link>
      </div>
    </div>

    <MainFooter></MainFooter>
  </div>
</template>

<script>
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'

export default {
  name: 'VisualizationIndex',
  components: {
    MainHeader,
    MainFooter
  }
}
</script>

<style lang="scss" scoped>
.visualization-index {
  min-height: 100vh;
  background: #f5f5f5;
}

.visualization-content {
  padding: 40px 5%;
}

.page-title {
  text-align: center;
  margin-bottom: 40px;

  h1 {
    font-size: 32px;
    color: #8B4513;
    margin-bottom: 10px;
  }

  p {
    font-size: 16px;
    color: #666;
  }
}

.visualization-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  max-width: 1400px;
  margin: 0 auto;
}

.visual-card {
  background: white;
  border-radius: 16px;
  padding: 40px 30px;
  min-height: 220px;
  text-decoration: none;
  color: inherit;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
  }

  h3 {
    font-size: 20px;
    font-weight: 600;
    color: #333;
    margin-bottom: 15px;
  }

  p {
    font-size: 15px;
    color: #666;
    line-height: 1.7;
  }

  .card-arrow {
    position: absolute;
    right: 30px;
    bottom: 30px;
    font-size: 24px;
    opacity: 0.5;
    transition: opacity 0.3s;
  }

  &:hover .card-arrow {
    opacity: 1;
  }
}

.knowledge-graph-card {
  .card-icon {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
  }
}

.timeline-card {
  .card-icon {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    color: white;
  }
}

.map-card {
  .card-icon {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: white;
  }
}

.dashboard-card {
  .card-icon {
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
    color: white;
  }
}
</style>
