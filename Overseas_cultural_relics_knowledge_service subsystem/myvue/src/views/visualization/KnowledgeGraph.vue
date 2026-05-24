<template>
  <div class="knowledge-graph-page">
    <MainHeader></MainHeader>

    <div class="page-container">
      <div class="page-header">
        <h1>知识图谱关系图</h1>
        <p>采用力导向图展示文物实体及其关联关系</p>
      </div>

      <div class="graph-container">
        <div class="graph-controls">
          <div class="control-group">
            <label>显示标签:</label>
            <el-switch v-model="showLabels" active-color="#8B4513"></el-switch>
          </div>
          <div class="control-group">
            <label>节点大小:</label>
            <el-slider v-model="nodeSize" :min="20" :max="60" :step="5"></el-slider>
          </div>
          <div class="control-group">
            <label>力强度:</label>
            <el-slider v-model="forceStrength" :min="0.1" :max="2" :step="0.1"></el-slider>
          </div>
          <button class="refresh-btn" @click="refreshGraph">刷新数据</button>
          <button class="reset-btn" @click="resetView">重置视图</button>
        </div>

        <div class="graph-area" ref="graphArea" @wheel.prevent="handleZoom" @mousedown="handleMouseDown">
          <svg :width="svgWidth" :height="svgHeight" :style="svgTransform">
            <defs>
              <filter id="shadow" x="-20%" y="-20%" width="140%" height="140%">
                <feDropShadow dx="2" dy="2" stdDeviation="3" flood-opacity="0.3"/>
              </filter>
              <linearGradient id="relicGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:#8B4513"/>
                <stop offset="100%" style="stop-color:#A0522D"/>
              </linearGradient>
              <linearGradient id="museumGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:#4facfe"/>
                <stop offset="100%" style="stop-color:#00f2fe"/>
              </linearGradient>
              <linearGradient id="periodGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:#43e97b"/>
                <stop offset="100%" style="stop-color:#38f9d7"/>
              </linearGradient>
            </defs>

            <g>
              <line
                v-for="(link, index) in links"
                :key="'link-' + index"
                :x1="getNodePosition(link.source).x"
                :y1="getNodePosition(link.source).y"
                :x2="getNodePosition(link.target).x"
                :y2="getNodePosition(link.target).y"
                :stroke="linkColor"
                stroke-width="2"
                class="link-line"
              />
            </g>

            <g>
              <g
                v-for="(node, index) in graphNodes"
                :key="'node-' + index"
                :transform="`translate(${node.x}, ${node.y})`"
                class="node-group"
                @click="selectNode(index)"
                @mouseenter="hoverNode(index, $event)"
                @mouseleave="unhoverNode"
              >
                <circle
                  :r="selectedNode === index ? nodeSize + 5 : nodeSize"
                  :fill="getNodeFill(node.type)"
                  :stroke="selectedNode === index ? '#5D3A1A' : 'none'"
                  stroke-width="3"
                  filter="url(#shadow)"
                  class="node-circle"
                />
                <text
                  v-if="showLabels"
                  :y="nodeSize + 18"
                  text-anchor="middle"
                  font-size="12"
                  fill="#333"
                  class="node-label"
                >
                  {{ truncateLabel(node.label) }}
                </text>
              </g>
            </g>
          </svg>

          <div v-if="hoveredNode" class="node-tooltip" :style="tooltipStyle">
            <div class="tooltip-title">{{ graphNodes[hoveredNode].label }}</div>
            <div class="tooltip-info">类型: {{ getTypeLabel(graphNodes[hoveredNode].type) }}</div>
            <div class="tooltip-info">描述: {{ graphNodes[hoveredNode].description }}</div>
          </div>

          <div class="zoom-controls">
            <button class="zoom-btn" @click="zoomIn">+</button>
            <button class="zoom-btn" @click="zoomOut">-</button>
          </div>
        </div>

        <div class="node-info-panel" v-if="selectedNode !== null">
          <div class="panel-header">
            <h3>{{ graphNodes[selectedNode].label }}</h3>
            <span class="type-badge" :style="{ background: getTypeColor(graphNodes[selectedNode].type) }">
              {{ getTypeLabel(graphNodes[selectedNode].type) }}
            </span>
          </div>
          <div class="info-row">
            <span class="info-label">描述:</span>
            <span>{{ graphNodes[selectedNode].description }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">相关实体:</span>
            <div class="related-nodes">
              <span
                v-for="(rel, idx) in getRelatedNodes(selectedNode)"
                :key="idx"
                class="related-tag"
                :style="{ background: getTypeColor(graphNodes[rel].type) }"
                @click="selectNode(rel)"
              >
                {{ graphNodes[rel].label }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <div class="legend">
        <h4>图例说明</h4>
        <div class="legend-items">
          <div class="legend-item">
            <span class="legend-circle" style="background: url(#relicGradient)"></span>
            <span>文物</span>
          </div>
          <div class="legend-item">
            <span class="legend-circle" style="background: url(#museumGradient)"></span>
            <span>博物馆</span>
          </div>
          <div class="legend-item">
            <span class="legend-circle" style="background: url(#periodGradient)"></span>
            <span>朝代</span>
          </div>
        </div>
      </div>
    </div>

    <MainFooter></MainFooter>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import axios from 'axios'
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'

export default {
  name: 'KnowledgeGraph',
  components: {
    MainHeader,
    MainFooter
  },
  setup() {
    const svgWidth = ref(900)
    const svgHeight = ref(500)
    const showLabels = ref(true)
    const nodeSize = ref(30)
    const forceStrength = ref(0.5)
    const selectedNode = ref(null)
    const hoveredNode = ref(null)
    const tooltipStyle = reactive({ left: '0px', top: '0px', display: 'none' })
    const graphArea = ref(null)
    const graphNodes = reactive([])
    const links = ref([])
    const scale = ref(1)
    const translateX = ref(0)
    const translateY = ref(0)
    const isDragging = ref(false)
    const dragStart = reactive({ x: 0, y: 0 })
    const isLoading = ref(false)

    const svgTransform = ref('')

    const getNodeFill = (type) => {
      const fills = {
        '文物': 'url(#relicGradient)',
        '博物馆': 'url(#museumGradient)',
        '朝代': 'url(#periodGradient)'
      }
      return fills[type] || 'url(#relicGradient)'
    }

    const getTypeColor = (type) => {
      const colors = {
        '文物': '#8B4513',
        '博物馆': '#4facfe',
        '朝代': '#43e97b'
      }
      return colors[type] || '#8B4513'
    }

    const getTypeLabel = (type) => {
      const labels = {
        '文物': '文物',
        '博物馆': '博物馆',
        '朝代': '朝代'
      }
      return labels[type] || type
    }

    const linkColor = '#ccc'

    const truncateLabel = (label) => {
      return label.length > 6 ? label.substring(0, 6) + '...' : label
    }

    const getNodePosition = (nodeId) => {
      const index = graphNodes.findIndex(n => n.id === nodeId)
      return index !== -1 ? graphNodes[index] : { x: 0, y: 0 }
    }

    const selectNode = (index) => {
      selectedNode.value = selectedNode.value === index ? null : index
    }

    const hoverNode = (index, event) => {
      hoveredNode.value = index
      const rect = graphArea.value.getBoundingClientRect()
      tooltipStyle.left = (event.clientX - rect.left + 15) + 'px'
      tooltipStyle.top = (event.clientY - rect.top - 30) + 'px'
      tooltipStyle.display = 'block'
    }

    const unhoverNode = () => {
      hoveredNode.value = null
      tooltipStyle.display = 'none'
    }

    const getRelatedNodes = (nodeIndex) => {
      const related = new Set()
      const nodeId = graphNodes[nodeIndex].id
      links.value.forEach(link => {
        if (link.source === nodeId) {
          const idx = graphNodes.findIndex(n => n.id === link.target)
          if (idx !== -1) related.add(idx)
        }
        if (link.target === nodeId) {
          const idx = graphNodes.findIndex(n => n.id === link.source)
          if (idx !== -1) related.add(idx)
        }
      })
      return Array.from(related)
    }

    const refreshGraph = async () => {
      isLoading.value = true
      try {
        const response = await axios.get('http://localhost:8085/api/v1/data/knowledge-graph', {
          params: { limit: 20 }
        })
        if (response.data && response.data.data) {
          const data = response.data.data
          graphNodes.splice(0, graphNodes.length)
          links.value = data.links || []

          data.nodes.forEach((node, index) => {
            graphNodes.push({
              ...node,
              x: svgWidth.value / 2 + (Math.random() - 0.5) * 400,
              y: svgHeight.value / 2 + (Math.random() - 0.5) * 300,
              vx: 0,
              vy: 0
            })
          })

          selectedNode.value = null
          startSimulation()
        }
      } catch (error) {
        console.error('Failed to fetch graph data:', error)
        loadMockData()
      }
      isLoading.value = false
    }

    const loadMockData = () => {
      const mockNodes = [
        { id: 'relic_1', label: '青铜鼎', type: '文物', description: '商周时期青铜礼器，造型精美' },
        { id: 'relic_2', label: '青花瓷瓶', type: '文物', description: '明代青花瓷器，纹饰精美' },
        { id: 'relic_3', label: '玉璧', type: '文物', description: '汉代玉礼器，象征权力' },
        { id: 'relic_4', label: '唐三彩', type: '文物', description: '唐代彩陶艺术品' },
        { id: 'museum_1', label: '大英博物馆', type: '博物馆', description: '英国伦敦著名博物馆' },
        { id: 'museum_2', label: '大都会博物馆', type: '博物馆', description: '美国纽约著名博物馆' },
        { id: 'museum_3', label: '卢浮宫', type: '博物馆', description: '法国巴黎著名博物馆' },
        { id: 'period_1', label: '商周', type: '朝代', description: '中国古代青铜时代' },
        { id: 'period_2', label: '唐代', type: '朝代', description: '中国古代鼎盛时期' },
        { id: 'period_3', label: '明代', type: '朝代', description: '中国古代瓷器发展高峰' }
      ]

      const mockLinks = [
        { source: 'relic_1', target: 'museum_1', relationType: '收藏于' },
        { source: 'relic_1', target: 'period_1', relationType: '属于' },
        { source: 'relic_2', target: 'museum_2', relationType: '收藏于' },
        { source: 'relic_2', target: 'period_3', relationType: '属于' },
        { source: 'relic_3', target: 'museum_3', relationType: '收藏于' },
        { source: 'relic_3', target: 'period_1', relationType: '属于' },
        { source: 'relic_4', target: 'museum_1', relationType: '收藏于' },
        { source: 'relic_4', target: 'period_2', relationType: '属于' },
        { source: 'relic_1', target: 'relic_2', relationType: '相关' },
        { source: 'relic_2', target: 'relic_4', relationType: '相关' }
      ]

      graphNodes.splice(0, graphNodes.length)
      mockNodes.forEach((node, index) => {
        graphNodes.push({
          ...node,
          x: svgWidth.value / 2 + (Math.random() - 0.5) * 400,
          y: svgHeight.value / 2 + (Math.random() - 0.5) * 300,
          vx: 0,
          vy: 0
        })
      })
      links.value = mockLinks
      startSimulation()
    }

    const startSimulation = () => {
      const centerX = svgWidth.value / 2
      const centerY = svgHeight.value / 2
      const damping = 0.85

      const simulate = () => {
        graphNodes.forEach(node => {
          let fx = 0
          let fy = 0

          graphNodes.forEach(other => {
            if (node.id !== other.id) {
              const dx = other.x - node.x
              const dy = other.y - node.y
              const distance = Math.sqrt(dx * dx + dy * dy) || 1
              const force = (distance - 300) / distance * 0.01 * forceStrength.value
              fx += dx * force
              fy += dy * force
            }
          })

          links.value.forEach(link => {
            if (link.source === node.id || link.target === node.id) {
              const targetId = link.source === node.id ? link.target : link.source
              const target = graphNodes.find(n => n.id === targetId)
              if (target) {
                const dx = target.x - node.x
                const dy = target.y - node.y
                const distance = Math.sqrt(dx * dx + dy * dy) || 1
                const springLength = 150
                const springForce = (distance - springLength) / distance * 0.15 * forceStrength.value
                if (link.source === node.id) {
                  fx += dx * springForce
                  fy += dy * springForce
                } else {
                  fx -= dx * springForce
                  fy -= dy * springForce
                }
              }
            }
          })

          const centerForce = 0.0005
          fx += (centerX - node.x) * centerForce
          fy += (centerY - node.y) * centerForce

          node.vx = (node.vx + fx) * damping
          node.vy = (node.vy + fy) * damping

          node.x += node.vx
          node.y += node.vy

          node.x = Math.max(nodeSize.value, Math.min(svgWidth.value - nodeSize.value, node.x))
          node.y = Math.max(nodeSize.value, Math.min(svgHeight.value - nodeSize.value, node.y))
        })
      }

      let count = 0
      const runSimulation = () => {
        simulate()
        count++
        if (count < 100) {
          requestAnimationFrame(runSimulation)
        }
      }
      runSimulation()
    }

    const resetView = () => {
      scale.value = 1
      translateX.value = 0
      translateY.value = 0
      updateSvgTransform()
      selectedNode.value = null
      hoveredNode.value = null
    }

    const zoomIn = () => {
      scale.value = Math.min(scale.value * 1.2, 3)
      updateSvgTransform()
    }

    const zoomOut = () => {
      scale.value = Math.max(scale.value / 1.2, 0.5)
      updateSvgTransform()
    }

    const handleZoom = (event) => {
      const delta = event.deltaY > 0 ? -0.1 : 0.1
      scale.value = Math.max(0.5, Math.min(3, scale.value + delta))
      updateSvgTransform()
    }

    const handleMouseDown = (event) => {
      if (event.target.tagName !== 'circle' && event.target.tagName !== 'text') {
        isDragging.value = true
        dragStart.x = event.clientX - translateX.value
        dragStart.y = event.clientY - translateY.value
        document.addEventListener('mousemove', handleMouseMove)
        document.addEventListener('mouseup', handleMouseUp)
      }
    }

    const handleMouseMove = (event) => {
      if (isDragging.value) {
        translateX.value = event.clientX - dragStart.x
        translateY.value = event.clientY - dragStart.y
        updateSvgTransform()
      }
    }

    const handleMouseUp = () => {
      isDragging.value = false
      document.removeEventListener('mousemove', handleMouseMove)
      document.removeEventListener('mouseup', handleMouseUp)
    }

    const updateSvgTransform = () => {
      svgTransform.value = `translate(${translateX.value}, ${translateY.value}) scale(${scale.value})`
    }

    onMounted(() => {
      const handleResize = () => {
        if (graphArea.value) {
          svgWidth.value = Math.min(graphArea.value.clientWidth - 30, 1200)
          svgHeight.value = 600
        }
      }
      handleResize()
      window.addEventListener('resize', handleResize)

      refreshGraph()

      onUnmounted(() => {
        window.removeEventListener('resize', handleResize)
        document.removeEventListener('mousemove', handleMouseMove)
        document.removeEventListener('mouseup', handleMouseUp)
      })
    })

    return {
      svgWidth,
      svgHeight,
      showLabels,
      nodeSize,
      forceStrength,
      selectedNode,
      hoveredNode,
      tooltipStyle,
      graphNodes,
      links,
      graphArea,
      svgTransform,
      isLoading,
      selectNode,
      hoverNode,
      unhoverNode,
      getRelatedNodes,
      refreshGraph,
      resetView,
      zoomIn,
      zoomOut,
      getNodeFill,
      getTypeColor,
      getTypeLabel,
      truncateLabel,
      linkColor
    }
  }
}
</script>

<style lang="scss" scoped>
.knowledge-graph-page {
  min-height: 100vh;
  background: #f8f8f8;
}

.page-container {
  padding: 30px 5%;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;

  h1 {
    font-size: 28px;
    color: #8B4513;
    margin-bottom: 8px;
  }

  p {
    font-size: 14px;
    color: #666;
  }
}

.graph-container {
  background: white;
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.graph-controls {
  display: flex;
  align-items: center;
  gap: 30px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
  flex-wrap: wrap;

  .control-group {
    display: flex;
    align-items: center;
    gap: 10px;

    label {
      font-size: 14px;
      color: #666;
    }

    :deep(.el-slider) {
      width: 120px;
    }
  }

  .refresh-btn, .reset-btn {
    margin-left: auto;
    padding: 8px 20px;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    font-size: 14px;
    transition: all 0.3s;
  }

  .refresh-btn {
    background: #4facfe;
    color: white;

    &:hover {
      background: #3fa5f0;
    }
  }

  .reset-btn {
    background: #8B4513;
    color: white;
    margin-left: 10px;

    &:hover {
      background: #6B3510;
    }
  }
}

.graph-area {
  position: relative;
  background: #fafafa;
  border-radius: 12px;
  overflow: hidden;
  cursor: grab;
  min-height: 600px;

  &:active {
    cursor: grabbing;
  }

  svg {
    display: block;
    margin: 0 auto;
    transform-origin: center center;
  }
}

.node-group {
  cursor: pointer;
}

.node-circle {
  transition: all 0.3s;

  &:hover {
    filter: url(#shadow) brightness(1.1);
  }
}

.node-label {
  pointer-events: none;
  font-weight: 500;
}

.node-tooltip {
  position: absolute;
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 12px 16px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  z-index: 100;
  pointer-events: none;
  max-width: 250px;

  .tooltip-title {
    font-weight: 600;
    color: #8B4513;
    margin-bottom: 8px;
    font-size: 14px;
  }

  .tooltip-info {
    font-size: 13px;
    color: #666;
    margin-bottom: 4px;
  }
}

.node-info-panel {
  margin-top: 20px;
  padding: 20px;
  background: #fff8f0;
  border-radius: 12px;
  border-left: 4px solid #8B4513;

  .panel-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 15px;

    h3 {
      color: #8B4513;
      margin-bottom: 0;
      font-size: 18px;
    }

    .type-badge {
      padding: 4px 12px;
      border-radius: 20px;
      color: white;
      font-size: 12px;
    }
  }

  .info-row {
    display: flex;
    margin-bottom: 12px;
    align-items: flex-start;

    .info-label {
      font-weight: 600;
      color: #333;
      min-width: 70px;
    }

    span:last-child {
      color: #666;
      flex: 1;
    }
  }

  .related-nodes {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;

    .related-tag {
      padding: 4px 12px;
      color: white !important;
      border-radius: 20px;
      font-size: 13px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        opacity: 0.8;
        transform: translateY(-2px);
      }
    }
  }
}

.zoom-controls {
  position: absolute;
  top: 15px;
  right: 15px;
  display: flex;
  flex-direction: column;
  gap: 5px;

  .zoom-btn {
    width: 36px;
    height: 36px;
    border: none;
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.9);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    font-size: 18px;
    font-weight: 600;
    cursor: pointer;
    color: #333;
    transition: all 0.3s;

    &:hover {
      background: #8B4513;
      color: white;
    }
  }
}

.legend {
  margin-top: 25px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);

  h4 {
    margin-bottom: 15px;
    color: #333;
    font-size: 15px;
  }

  .legend-items {
    display: flex;
    gap: 30px;
    flex-wrap: wrap;

    .legend-item {
      display: flex;
      align-items: center;
      gap: 8px;

      .legend-circle {
        width: 16px;
        height: 16px;
        border-radius: 50%;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      }

      span:last-child {
        font-size: 13px;
        color: #666;
      }
    }
  }
}
</style>