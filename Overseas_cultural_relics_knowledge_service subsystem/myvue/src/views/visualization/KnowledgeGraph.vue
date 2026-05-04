<template>
  <div class="knowledge-graph-page">
    <MainHeader></MainHeader>

    <div class="page-container">
      <div class="page-header">
        <h1>知识图谱关系图</h1>
        <p>力导向图展示文物实体及其关联关系</p>
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
          <button class="reset-btn" @click="resetGraph">重置视图</button>
        </div>

        <div class="graph-area" ref="graphArea">
          <svg :width="svgWidth" :height="svgHeight">
            <defs>
              <filter id="shadow" x="-20%" y="-20%" width="140%" height="140%">
                <feDropShadow dx="2" dy="2" stdDeviation="3" flood-opacity="0.3"/>
              </filter>
              <linearGradient id="nodeGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:#8B4513"/>
                <stop offset="100%" style="stop-color:#A0522D"/>
              </linearGradient>
            </defs>

            <g>
              <line
                v-for="(link, index) in links"
                :key="'link-' + index"
                :x1="nodes[link.source].x"
                :y1="nodes[link.source].y"
                :x2="nodes[link.target].x"
                :y2="nodes[link.target].y"
                stroke="#ccc"
                stroke-width="2"
                class="link-line"
              />
            </g>

            <g>
              <g
                v-for="(node, index) in nodes"
                :key="'node-' + index"
                :transform="`translate(${node.x}, ${node.y})`"
                class="node-group"
                @click="selectNode(index)"
                @mouseenter="hoverNode(index)"
                @mouseleave="unhoverNode"
              >
                <circle
                  :r="selectedNode === index ? nodeSize + 5 : nodeSize"
                  :fill="selectedNode === index ? '#8B4513' : 'url(#nodeGradient)'"
                  :stroke="selectedNode === index ? '#5D3A1A' : '#8B4513'"
                  stroke-width="2"
                  filter="url(#shadow)"
                  class="node-circle"
                />
                <text
                  v-if="showLabels"
                  :y="nodeSize + 15"
                  text-anchor="middle"
                  font-size="12"
                  fill="#333"
                  class="node-label"
                >
                  {{ node.label }}
                </text>
              </g>
            </g>
          </svg>

          <div v-if="hoveredNode" class="node-tooltip" :style="tooltipStyle">
            <div class="tooltip-title">{{ nodes[hoveredNode].label }}</div>
            <div class="tooltip-info">类型: {{ nodes[hoveredNode].type }}</div>
            <div class="tooltip-info">描述: {{ nodes[hoveredNode].description }}</div>
          </div>
        </div>

        <div class="node-info-panel" v-if="selectedNode !== null">
          <h3>{{ nodes[selectedNode].label }}</h3>
          <div class="info-row">
            <span class="info-label">类型:</span>
            <span>{{ nodes[selectedNode].type }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">描述:</span>
            <span>{{ nodes[selectedNode].description }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">相关实体:</span>
            <div class="related-nodes">
              <span
                v-for="(rel, idx) in getRelatedNodes(selectedNode)"
                :key="idx"
                class="related-tag"
                @click="selectNode(rel)"
              >
                {{ nodes[rel].label }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <MainFooter></MainFooter>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
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
    const selectedNode = ref(null)
    const hoveredNode = ref(null)
    const tooltipStyle = reactive({ left: '0px', top: '0px' })
    const graphArea = ref(null)

    const nodes = reactive([
      { x: 450, y: 150, label: '青铜器', type: '文物', description: '商周时期青铜礼器' },
      { x: 250, y: 250, label: '商周', type: '朝代', description: '中国古代朝代' },
      { x: 650, y: 250, label: '大英博物馆', type: '博物馆', description: '英国伦敦著名博物馆' },
      { x: 450, y: 350, label: '青花瓷', type: '文物', description: '明代青花瓷器' },
      { x: 250, y: 400, label: '明代', type: '朝代', description: '中国古代朝代' },
      { x: 650, y: 400, label: '大都会博物馆', type: '博物馆', description: '美国纽约著名博物馆' },
      { x: 150, y: 150, label: '青铜礼器', type: '文物类型', description: '古代祭祀用器' },
      { x: 750, y: 150, label: '陶瓷', type: '文物类型', description: '陶瓷制品' }
    ])

    const links = [
      { source: 0, target: 1 },
      { source: 0, target: 2 },
      { source: 0, target: 6 },
      { source: 3, target: 4 },
      { source: 3, target: 5 },
      { source: 3, target: 7 },
      { source: 0, target: 3 }
    ]

    const selectNode = (index) => {
      selectedNode.value = selectedNode.value === index ? null : index
    }

    const hoverNode = (index) => {
      hoveredNode.value = index
      const node = nodes[index]
      tooltipStyle.left = node.x + 50 + 'px'
      tooltipStyle.top = node.y - 30 + 'px'
    }

    const unhoverNode = () => {
      hoveredNode.value = null
    }

    const getRelatedNodes = (nodeIndex) => {
      const related = new Set()
      links.forEach(link => {
        if (link.source === nodeIndex) related.add(link.target)
        if (link.target === nodeIndex) related.add(link.source)
      })
      return Array.from(related)
    }

    const resetGraph = () => {
      selectedNode.value = null
      hoveredNode.value = null
    }

    onMounted(() => {
      const handleResize = () => {
        if (graphArea.value) {
          svgWidth.value = Math.min(graphArea.value.clientWidth - 30, 1000)
          svgHeight.value = 500
        }
      }
      handleResize()
      window.addEventListener('resize', handleResize)
      onUnmounted(() => window.removeEventListener('resize', handleResize))
    })

    return {
      svgWidth,
      svgHeight,
      showLabels,
      nodeSize,
      selectedNode,
      hoveredNode,
      tooltipStyle,
      nodes,
      links,
      graphArea,
      selectNode,
      hoverNode,
      unhoverNode,
      getRelatedNodes,
      resetGraph
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

  .control-group {
    display: flex;
    align-items: center;
    gap: 10px;

    label {
      font-size: 14px;
      color: #666;
    }
  }

  .reset-btn {
    margin-left: auto;
    padding: 8px 20px;
    background: #8B4513;
    color: white;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    font-size: 14px;
    transition: background 0.3s;

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

  svg {
    display: block;
    margin: 0 auto;
  }
}

.node-group {
  cursor: pointer;
}

.node-circle {
  transition: all 0.3s;
}

.node-label {
  pointer-events: none;
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

  .tooltip-title {
    font-weight: 600;
    color: #8B4513;
    margin-bottom: 8px;
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

  h3 {
    color: #8B4513;
    margin-bottom: 15px;
    font-size: 18px;
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
    }
  }

  .related-nodes {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;

    .related-tag {
      padding: 4px 12px;
      background: #8B4513;
      color: white !important;
      border-radius: 20px;
      font-size: 13px;
      cursor: pointer;
      transition: background 0.3s;

      &:hover {
        background: #6B3510;
      }
    }
  }
}
</style>
