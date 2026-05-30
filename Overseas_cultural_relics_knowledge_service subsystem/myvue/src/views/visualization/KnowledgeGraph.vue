<template>
  <div class="knowledge-graph-page">
    <MainHeader></MainHeader>

    <div class="page-container">
      <div class="page-header">
        <h1>知识图谱</h1>
        <p>探索文物、博物馆与朝代的关联关系（演示模式：展示 {{ demoLimit }} 件文物及其关联）</p>
        <p v-if="demoHint" class="demo-hint">{{ demoHint }}</p>
      </div>

      <div class="graph-filter">
        <el-select v-model="selectedPeriod" placeholder="筛选时代" clearable @change="applyFilter">
          <el-option label="全部时代" value=""></el-option>
          <el-option v-for="period in periodOptions" :key="period" :label="period" :value="period"></el-option>
        </el-select>
        <el-select v-model="selectedMuseum" placeholder="筛选所属地" clearable @change="applyFilter">
          <el-option label="全部所属地" value=""></el-option>
          <el-option v-for="museum in museumOptions" :key="museum" :label="museum" :value="museum"></el-option>
        </el-select>
        <el-button @click="resetFilter" size="small">重置筛选</el-button>
        <span class="filter-info">当前显示: {{ filteredNodes.length }} / {{ allNodes.length }} 个节点</span>
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
                :r="node.type === '文物' ? 20 : 16"
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
                y="38"
                text-anchor="middle"
                font-size="10"
                fill="rgba(255,255,255,0.92)"
                font-weight="500"
                pointer-events="none"
                style="user-select: none; -webkit-user-select: none;"
              >
                {{ node.label && node.label.length > 10 ? node.label.slice(0, 10) + '…' : node.label }}
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
import { ref, computed, onMounted } from 'vue'
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
    const allNodes = ref([])
    const allLinks = ref([])
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
    const demoLimit = ref(25)
    const demoHint = ref('')
    const selectedPeriod = ref('')
    const selectedMuseum = ref('')
    const periodOptions = ref([])
    const museumOptions = ref([])

    const filteredNodes = computed(() => {
      if (!selectedPeriod.value && !selectedMuseum.value) {
        return allNodes.value
      }
      return allNodes.value.filter(node => {
        if (selectedPeriod.value && node.type === '朝代' && node.label === selectedPeriod.value) {
          return true
        }
        if (selectedMuseum.value && node.type === '博物馆' && node.label === selectedMuseum.value) {
          return true
        }
        if (selectedPeriod.value || selectedMuseum.value) {
          const connectedLinks = allLinks.value.filter(
            l => l.source === node.id || l.target === node.id
          )
          for (const link of connectedLinks) {
            const neighborId = link.source === node.id ? link.target : link.source
            const neighbor = allNodes.value.find(n => n.id === neighborId)
            if (neighbor) {
              if (selectedPeriod.value && neighbor.type === '朝代' && neighbor.label === selectedPeriod.value) {
                return true
              }
              if (selectedMuseum.value && neighbor.type === '博物馆' && neighbor.label === selectedMuseum.value) {
                return true
              }
            }
          }
          if (node.label === selectedPeriod.value || node.label === selectedMuseum.value) {
            return true
          }
          return false
        }
        return true
      })
    })

    const applyFilter = () => {
      const filteredIds = new Set(filteredNodes.value.map(n => n.id))
      nodes.value = allNodes.value.filter(n => filteredIds.has(n.id))
      links.value = allLinks.value.filter(
        l => filteredIds.has(l.source) && filteredIds.has(l.target)
      )
      const laid = layoutNodes(nodes.value, links.value)
      nodes.value = laid.nodeList
      links.value = laid.linkList
    }

    const resetFilter = () => {
      selectedPeriod.value = ''
      selectedMuseum.value = ''
      nodes.value = [...allNodes.value]
      links.value = [...allLinks.value]
      const laid = layoutNodes(nodes.value, links.value)
      nodes.value = laid.nodeList
      links.value = laid.linkList
    }

    const initFilterOptions = () => {
      const periods = new Set()
      const museums = new Set()
      allNodes.value.forEach(node => {
        if (node.type === '朝代') {
          periods.add(node.label)
        } else if (node.type === '博物馆') {
          museums.add(node.label)
        }
      })
      periodOptions.value = Array.from(periods).sort()
      museumOptions.value = Array.from(museums).sort()
    }

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

    const deduplicateNodes = (nodeList, linkList) => {
      const seen = new Set()
      const uniqueNodes = nodeList.filter(node => {
        if (seen.has(node.id)) {
          return false
        }
        seen.add(node.id)
        return true
      })
      const uniqueLinks = linkList.filter(link =>
        uniqueNodes.some(n => n.id === link.source) &&
        uniqueNodes.some(n => n.id === link.target)
      )
      return { uniqueNodes, uniqueLinks }
    }

    const getDuplicateLeafNodes = (nodeList, linkList) => {
      const degreeMap = new Map()
      nodeList.forEach(n => degreeMap.set(n.id, 0))
      linkList.forEach(link => {
        degreeMap.set(link.source, (degreeMap.get(link.source) || 0) + 1)
        degreeMap.set(link.target, (degreeMap.get(link.target) || 0) + 1)
      })
      const seen = new Set()
      const duplicates = new Set()
      degreeMap.forEach((degree, id) => {
        if (degree === 1) {
          if (seen.has(id)) {
            duplicates.add(id)
          }
          seen.add(id)
        }
      })
      return duplicates
    }

    const layoutNodes = (nodeList, linkList) => {
      const width = 900
      const height = 520
      const relics = nodeList.filter(n => n.type === '文物')
      const museums = nodeList.filter(n => n.type === '博物馆')
      const periods = nodeList.filter(n => n.type === '朝代')
      const cx = width / 2
      const cy = height / 2

      relics.forEach((node, i) => {
        const angle = (2 * Math.PI * i) / Math.max(relics.length, 1)
        const r = Math.min(180, 70 + relics.length * 4)
        node.x = cx + r * Math.cos(angle)
        node.y = cy + r * Math.sin(angle)
      })
      museums.forEach((node, i) => {
        const angle = Math.PI + (Math.PI * i) / Math.max(museums.length, 1)
        node.x = cx + 260 * Math.cos(angle)
        node.y = cy + 200 * Math.sin(angle)
      })
      periods.forEach((node, i) => {
        const angle = (Math.PI * i) / Math.max(periods.length, 1)
        node.x = cx + 260 * Math.cos(angle)
        node.y = cy - 200 * Math.sin(angle)
      })

      // 未分类节点兜底
      nodeList.forEach((node, i) => {
        if (node.x == null || node.y == null) {
          node.x = 120 + (i % 8) * 90
          node.y = 120 + Math.floor(i / 8) * 70
        }
      })
      return { nodeList, linkList, width, height }
    }

    // 从后端API获取数据
    const fetchFromAPI = async () => {
      try {
        console.log('尝试从后端API获取知识图谱数据...')
        const response = await fetch(`${getApiRoot()}/api/v1/data/knowledge-graph?limit=${demoLimit.value}`)
        
        if (!response.ok) {
          throw new Error(`API响应错误: ${response.status}`)
        }
        
        const result = await response.json()
        
        if (result && result.data && result.data.nodes && result.data.nodes.length > 0) {
          console.log('从后端API获取到数据:', result.data.nodes.length, '个节点')

          let apiNodes = result.data.nodes.map(node => ({
            id: node.id,
            type: node.type,
            label: node.label
          }))
          let apiLinks = result.data.links.map(link => ({
            source: link.source,
            target: link.target,
            relationType: link.relationType
          }))

          const duplicateLeafNodes = getDuplicateLeafNodes(apiNodes, apiLinks)
          if (duplicateLeafNodes.size > 0) {
            console.log(`发现 ${duplicateLeafNodes.size} 个重复叶子节点，将被去重`)
          }

          const deduplicated = deduplicateNodes(apiNodes, apiLinks)
          apiNodes = deduplicated.uniqueNodes
          apiLinks = deduplicated.uniqueLinks

          const laid = layoutNodes(apiNodes, apiLinks)
          demoHint.value = `已从后端加载演示子图：${apiNodes.length} 个节点、${apiLinks.length} 条关系`
          return { nodes: laid.nodeList, links: laid.linkList }
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
        allNodes.value = [...apiData.nodes]
        allLinks.value = [...apiData.links]
        nodes.value = apiData.nodes
        links.value = apiData.links
      } else {
        console.log('使用本地Mock数据')
        let localNodes = (kgData.nodes || []).slice(0, 40)
        let localLinks = (kgData.edges || []).filter(
          l => localNodes.some(n => n.id === l.source) && localNodes.some(n => n.id === l.target)
        )

        const duplicateLeafNodes = getDuplicateLeafNodes(localNodes, localLinks)
        if (duplicateLeafNodes.size > 0) {
          console.log(`发现 ${duplicateLeafNodes.size} 个重复叶子节点，将被去重`)
        }

        const deduplicated = deduplicateNodes(localNodes, localLinks)
        localNodes = deduplicated.uniqueNodes
        localLinks = deduplicated.uniqueLinks

        const laid = layoutNodes(localNodes, localLinks)
        allNodes.value = [...localNodes]
        allLinks.value = [...localLinks]
        nodes.value = laid.nodeList
        links.value = laid.linkList
        demoHint.value = '后端不可用，已使用本地精简示例数据'
      }

      initFilterOptions()

      scale.value = 0.85
      offsetX.value = 20
      offsetY.value = 10
      loading.value = false
      console.log('Loaded', nodes.value.length, 'nodes and', links.value.length, 'edges')
    }

    onMounted(() => {
      initGraphData()
    })

    return {
      allNodes,
      allLinks,
      nodes,
      links,
      loading,
      scale,
      offsetX,
      offsetY,
      isDragging,
      selectedNode,
      demoLimit,
      demoHint,
      filteredNodes,
      selectedPeriod,
      selectedMuseum,
      periodOptions,
      museumOptions,
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
      resetView,
      applyFilter,
      resetFilter
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

.demo-hint {
  margin-top: 6px;
  font-size: 13px;
  color: #8b4513;
}

.graph-filter {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 20px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  flex-wrap: wrap;
}

.graph-filter .el-select {
  width: 160px;
}

.graph-filter .el-button {
  background-color: #8B4513;
  border-color: #8B4513;
  color: white;
}

.graph-filter .el-button:hover {
  background-color: #6B3510;
  border-color: #6B3510;
}

.filter-info {
  margin-left: auto;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
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