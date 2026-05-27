<template>
  <div class="knowledge-graph-page">
    <MainHeader></MainHeader>

    <div class="page-container">
      <div class="page-header">
        <h1>知识图谱</h1>
        <p>探索文物、博物馆与朝代的关联关系</p>
      </div>

      <div class="graph-container">
        <div v-if="loading" class="loading-overlay">
          <div class="loading-spinner"></div>
          <p>加载知识图谱数据...</p>
        </div>
        
        <svg 
          width="100%" 
          height="600" 
          class="graph-svg"
          :style="{ cursor: isDragging ? 'grabbing' : 'grab' }"
          @mousedown="handleCanvasMouseDown"
          @mousemove="handleMouseMove"
          @mouseup="handleMouseUp"
          @mouseleave="handleMouseUp"
          @wheel.prevent="handleWheel"
        >
          <defs>
            <!-- 文物渐变 - 深棕色 -->
            <radialGradient id="relicGrad" cx="50%" cy="50%" r="50%">
              <stop offset="0%" style="stop-color:#C9A86C"/>
              <stop offset="100%" style="stop-color:#5D4E37"/>
            </radialGradient>
            
            <!-- 博物馆渐变 - 深蓝色 -->
            <radialGradient id="museumGrad" cx="50%" cy="50%" r="50%">
              <stop offset="0%" style="stop-color:#667EEA"/>
              <stop offset="100%" style="stop-color:#364FC7"/>
            </radialGradient>
            
            <!-- 朝代渐变 - 深绿色 -->
            <radialGradient id="periodGrad" cx="50%" cy="50%" r="50%">
              <stop offset="0%" style="stop-color:#51CF66"/>
              <stop offset="100%" style="stop-color:#2F9E44"/>
            </radialGradient>
          </defs>

          <g :transform="`translate(${offsetX}, ${offsetY}) scale(${scale})`">
            <!-- Edges -->
            <line
              v-for="(link, index) in links"
              :key="'link-' + index"
              :x1="getNodePosition(link.source).x"
              :y1="getNodePosition(link.source).y"
              :x2="getNodePosition(link.target).x"
              :y2="getNodePosition(link.target).y"
              :stroke="getLinkColor(link.relationType)"
              stroke-width="1.5"
              stroke-opacity="0.5"
              stroke-dasharray="4,2"
            />

            <!-- Nodes -->
            <g 
              v-for="(node, index) in nodes" 
              :key="'node-' + index"
              :transform="`translate(${node.x}, ${node.y})`"
              :class="{ 'node-selected': selectedNode && selectedNode.id === node.id }"
            >
              <circle
                cx="0"
                cy="0"
                r="18"
                :fill="`url(#${getNodeGradient(node.type)})`"
                stroke="rgba(255,255,255,0.9)"
                stroke-width="1.5"
                shape-rendering="geometricPrecision"
                class="node-circle"
                @mousedown.stop="handleNodeMouseDown($event, index)"
                @click.stop="handleNodeClick(node)"
              />
              <text
                x="0"
                y="4"
                text-anchor="middle"
                font-size="12"
                fill="white"
                font-weight="bold"
                pointer-events="none"
                style="user-select: none; -webkit-user-select: none;"
              >
                {{ getNodeIcon(node.type) }}
              </text>
              <text
                x="0"
                y="35"
                text-anchor="middle"
                font-size="11"
                fill="white"
                font-weight="500"
                pointer-events="none"
                style="user-select: none; -webkit-user-select: none;"
              >
                {{ node.label }}
              </text>
            </g>
          </g>
        </svg>

        <div class="zoom-controls">
          <button class="zoom-btn" @click="zoomIn" title="放大">+</button>
          <span class="zoom-level">{{ Math.round(scale * 100) }}%</span>
          <button class="zoom-btn" @click="zoomOut" title="缩小">-</button>
          <button class="zoom-btn reset" @click="resetView" title="重置视图">⟲</button>
        </div>

        <div class="legend">
          <div class="legend-item">
            <span class="legend-dot" style="background: #8B4513;"></span>
            <span>文物</span>
          </div>
          <div class="legend-item">
            <span class="legend-dot" style="background: #4facfe;"></span>
            <span>博物馆</span>
          </div>
          <div class="legend-item">
            <span class="legend-dot" style="background: #43e97b;"></span>
            <span>朝代</span>
          </div>
        </div>

        <!-- Node Detail Panel -->
        <div v-if="selectedNode" class="node-detail" :class="{ 'show': selectedNode }">
          <button class="close-btn" @click="selectedNode = null">✕</button>
          <h3>{{ selectedNode.label }}</h3>
          <div class="detail-type">{{ selectedNode.type }}</div>
          <div class="detail-info">
            <p><strong>类型:</strong> {{ selectedNode.type }}</p>
            <p><strong>关系:</strong></p>
            <ul>
              <li v-for="(rel, i) in getNodeRelations(selectedNode.id)" :key="i">
                {{ rel }}
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <MainFooter></MainFooter>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'
import { getApiRoot } from '@/config/api'
import kgData from '../../assets/knowledge_graph.json'

export default {
  name: 'KnowledgeGraph',
  components: {
    MainHeader,
    MainFooter
  },
  setup() {
    const nodes = ref([])
    const links = ref([])
    const loading = ref(true)
    const scale = ref(1)
    const offsetX = ref(0)
    const offsetY = ref(0)
    const isDragging = ref(false)
    const dragStart = ref({ x: 0, y: 0 })
    const isNodeDragging = ref(false)
    const nodeDragStart = ref({ x: 0, y: 0, nodeIndex: -1 })
    const selectedNode = ref(null)

    const getNodeGradient = (type) => {
      const gradients = {
        '文物': 'relicGrad',
        '博物馆': 'museumGrad',
        '朝代': 'periodGrad'
      }
      return gradients[type] || 'relicGrad'
    }

    const getNodeIcon = (type) => {
      const icons = {
        '文物': '🏺',
        '博物馆': '🏛',
        '朝代': '📜'
      }
      return icons[type] || '●'
    }

    const getLinkColor = (relationType) => {
      const colors = {
        '收藏于': '#8B6914',
        '属于': '#37B24D',
        '相关': '#4C63D2'
      }
      return colors[relationType] || '#999'
    }

    const getNodePosition = (nodeId) => {
      const node = nodes.value.find(n => n.id === nodeId)
      return node ? { x: node.x, y: node.y } : { x: 0, y: 0 }
    }

    const getNodeRelations = (nodeId) => {
      const relations = []
      links.value.forEach(link => {
        if (link.source === nodeId) {
          const targetNode = nodes.value.find(n => n.id === link.target)
          if (targetNode) {
            relations.push(link.relationType + ' ' + targetNode.label)
          }
        } else if (link.target === nodeId) {
          const sourceNode = nodes.value.find(n => n.id === link.source)
          if (sourceNode) {
            relations.push(link.relationType + ' ' + sourceNode.label)
          }
        }
      })
      return relations.length > 0 ? relations : ['暂无关系信息']
    }

    const handleCanvasMouseDown = (e) => {
      if (e.target.classList.contains('node-circle')) return
      isDragging.value = true
      dragStart.value = {
        x: e.clientX - offsetX.value,
        y: e.clientY - offsetY.value
      }
    }

    const handleMouseMove = (e) => {
      if (isNodeDragging.value) {
        const svg = document.querySelector('.graph-svg')
        const svgRect = svg.getBoundingClientRect()
        
        // 鼠标在SVG坐标系中的位置
        const currentX = (e.clientX - svgRect.left) / scale.value - offsetX.value / scale.value
        const currentY = (e.clientY - svgRect.top) / scale.value - offsetY.value / scale.value
        
        const index = nodeDragStart.value.nodeIndex
        
        // 节点位置 = 初始节点位置 + (当前鼠标 - 初始鼠标)
        nodes.value[index].x = nodeDragStart.value.nodeX + (currentX - nodeDragStart.value.mouseX)
        nodes.value[index].y = nodeDragStart.value.nodeY + (currentY - nodeDragStart.value.mouseY)
      } else if (isDragging.value) {
        offsetX.value = e.clientX - dragStart.value.x
        offsetY.value = e.clientY - dragStart.value.y
      }
    }

    const handleMouseUp = () => {
      isDragging.value = false
      isNodeDragging.value = false
      nodeDragStart.value.nodeIndex = -1
    }

    const handleNodeMouseDown = (e, index) => {
      e.stopPropagation()
      isNodeDragging.value = true
      
      const svg = document.querySelector('.graph-svg')
      const svgRect = svg.getBoundingClientRect()
      
      // 鼠标在SVG坐标系中的位置
      const mouseX = (e.clientX - svgRect.left) / scale.value - offsetX.value / scale.value
      const mouseY = (e.clientY - svgRect.top) / scale.value - offsetY.value / scale.value
      
      nodeDragStart.value = {
        mouseX: mouseX,
        mouseY: mouseY,
        nodeX: nodes.value[index].x,
        nodeY: nodes.value[index].y,
        nodeIndex: index
      }
    }

    const handleNodeClick = (node) => {
      selectedNode.value = selectedNode.value && selectedNode.value.id === node.id ? null : node
    }

    const handleWheel = (e) => {
      const delta = e.deltaY > 0 ? -0.1 : 0.1
      const newScale = Math.max(0.5, Math.min(3, scale.value + delta))
      scale.value = newScale
    }

    const zoomIn = () => {
      scale.value = Math.min(3, scale.value + 0.2)
    }

    const zoomOut = () => {
      scale.value = Math.max(0.5, scale.value - 0.2)
    }

    const resetView = () => {
      scale.value = 1
      offsetX.value = 0
      offsetY.value = 0
      selectedNode.value = null
    }

    // 从后端API获取数据
    const fetchFromAPI = async () => {
      try {
        console.log('尝试从后端API获取知识图谱数据...')
        const response = await fetch(`${getApiRoot()}/api/v1/data/knowledge-graph?limit=30`)
        
        if (!response.ok) {
          throw new Error(`API响应错误: ${response.status}`)
        }
        
        const result = await response.json()
        
        if (result && result.data && result.data.nodes && result.data.nodes.length > 0) {
          console.log('从后端API获取到数据:', result.data.nodes.length, '个节点')
          
          // 转换后端数据格式
          const apiNodes = result.data.nodes.map(node => ({
            id: node.id,
            type: node.type,
            label: node.label,
            x: node.x || Math.random() * 600 + 100,
            y: node.y || Math.random() * 400 + 100
          }))
          
          const apiLinks = result.data.links.map(link => ({
            source: link.source,
            target: link.target,
            relationType: link.relationType
          }))
          
          return { nodes: apiNodes, links: apiLinks }
        }
        
        throw new Error('后端返回数据为空')
      } catch (error) {
        console.log('后端API不可用，使用本地Mock数据:', error.message)
        return null
      }
    }

    // 初始化图谱数据
    const initGraphData = async () => {
      console.log('Initializing knowledge graph data...')
      
      // 1. 先尝试从后端API获取数据
      const apiData = await fetchFromAPI()
      
      if (apiData) {
        nodes.value = apiData.nodes
        links.value = apiData.links
      } else {
        // 2. 后端不可用时，使用本地静态JSON
        console.log('使用本地Mock数据')
        nodes.value = kgData.nodes
        links.value = kgData.edges
      }
      
      loading.value = false
      console.log('Loaded', nodes.value.length, 'nodes and', links.value.length, 'edges')
    }

    onMounted(() => {
      initGraphData()
    })

    return {
      nodes,
      links,
      loading,
      scale,
      offsetX,
      offsetY,
      isDragging,
      selectedNode,
      getNodeGradient,
      getNodeIcon,
      getLinkColor,
      getNodePosition,
      getNodeRelations,
      handleCanvasMouseDown,
      handleMouseMove,
      handleMouseUp,
      handleNodeMouseDown,
      handleNodeClick,
      handleWheel,
      zoomIn,
      zoomOut,
      resetView
    }
  }
}
</script>

<style scoped>
.knowledge-graph-page {
  min-height: 100vh;
  background: #ffffff;
  display: flex;
  flex-direction: column;
}

.page-container {
  flex: 1;
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.page-header {
  text-align: center;
  margin-bottom: 24px;
}

.page-header h1 {
  color: #8B4513;
  font-size: 32px;
  font-weight: 600;
  letter-spacing: 4px;
  margin-bottom: 8px;
}

.page-header p {
  color: #666;
  font-size: 14px;
  letter-spacing: 1px;
}

.graph-container {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(26, 26, 46, 0.95);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 100;
  border-radius: 16px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 2px solid rgba(232, 213, 183, 0.2);
  border-top-color: #E8D5B7;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-overlay p {
  margin-top: 16px;
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
  letter-spacing: 1px;
}

.graph-svg {
  display: block;
  margin: 0 auto;
  border-radius: 12px;
  background: linear-gradient(180deg, rgba(15, 23, 42, 0.8) 0%, rgba(15, 23, 42, 0.95) 100%);
}

.node-circle {
  cursor: grab;
  transition: transform 0.1s ease-out;
}

.node-circle:hover {
  transform: scale(1.15);
}

.node-selected .node-circle {
  stroke: #E8D5B7 !important;
  stroke-width: 2.5;
}

.zoom-controls {
  position: absolute;
  top: 24px;
  right: 24px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  z-index: 10;
}

.zoom-btn {
  width: 32px;
  height: 32px;
  border: 1px solid rgba(232, 213, 183, 0.3);
  border-radius: 8px;
  background: rgba(232, 213, 183, 0.1);
  color: #E8D5B7;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(4px);
}

.zoom-btn:hover {
  background: rgba(232, 213, 183, 0.2);
  border-color: rgba(232, 213, 183, 0.5);
  transform: scale(1.05);
}

.zoom-btn:active {
  transform: scale(0.95);
}

.zoom-btn.reset {
  font-size: 14px;
}

.zoom-level {
  text-align: center;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.6);
  background: rgba(0, 0, 0, 0.3);
  padding: 4px 8px;
  border-radius: 4px;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.legend {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-top: 20px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: #666;
  letter-spacing: 0.5px;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  box-shadow: 0 0 6px currentColor;
}

.node-detail {
  position: absolute;
  top: 24px;
  left: 24px;
  background: rgba(26, 26, 46, 0.95);
  backdrop-filter: blur(12px);
  padding: 20px;
  border-radius: 12px;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
  max-width: 260px;
  opacity: 0;
  transform: translateY(-10px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 50;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.node-detail.show {
  opacity: 1;
  transform: translateY(0);
}

.node-detail h3 {
  margin: 0 0 8px 0;
  color: #E8D5B7;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 1px;
}

.node-detail .detail-type {
  color: rgba(255, 255, 255, 0.5);
  font-size: 12px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.node-detail .detail-info p {
  margin: 10px 0;
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
  line-height: 1.5;
}

.node-detail .detail-info strong {
  color: rgba(232, 213, 183, 0.9);
  font-weight: 500;
}

.node-detail .detail-info ul {
  margin: 8px 0 0 16px;
  padding: 0;
}

.node-detail .detail-info li {
  margin: 6px 0;
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
  line-height: 1.4;
}

.close-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.4);
  transition: color 0.2s;
  padding: 4px;
}

.close-btn:hover {
  color: rgba(255, 255, 255, 0.8);
}
</style>