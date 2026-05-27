<template>
  <div class="test-graph">
    <h1>知识图谱测试</h1>
    <div v-if="loading">加载中...</div>
    <div v-else>
      <p>节点数量: {{ nodes.length }}</p>
      <p>连接数量: {{ links.length }}</p>
      <svg width="800" height="600" border="1">
        <!-- 绘制连接线 -->
        <line
          v-for="(link, index) in links"
          :key="'link-' + index"
          :x1="link.source.x"
          :y1="link.source.y"
          :x2="link.target.x"
          :y2="link.target.y"
          stroke="#999"
          stroke-width="2"
        />
        <!-- 绘制节点 -->
        <g v-for="(node, index) in nodes" :key="'node-' + index">
          <circle
            :cx="node.x"
            :cy="node.y"
            r="20"
            :fill="getNodeColor(node.type)"
          />
          <text
            :x="node.x"
            :y="node.y + 5"
            text-anchor="middle"
            font-size="12"
            fill="white"
          >
            {{ node.label }}
          </text>
        </g>
      </svg>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const nodes = ref([])
const links = ref([])
const loading = ref(true)

const getNodeColor = (type) => {
  const colors = {
    '文物': '#8B4513',
    '博物馆': '#4facfe',
    '朝代': '#43e97b'
  }
  return colors[type] || '#999'
}

const initData = () => {
  nodes.value = [
    { id: 'relic_1', label: '青铜鼎', type: '文物', x: 100, y: 100 },
    { id: 'relic_2', label: '青花瓷', type: '文物', x: 200, y: 150 },
    { id: 'museum_1', label: '大英', type: '博物馆', x: 150, y: 300 },
    { id: 'period_1', label: '商代', type: '朝代', x: 150, y: 450 }
  ]
  
  links.value = [
    { source: { x: 100, y: 100 }, target: { x: 150, y: 300 } },
    { source: { x: 200, y: 150 }, target: { x: 150, y: 300 } },
    { source: { x: 100, y: 100 }, target: { x: 150, y: 450 } }
  ]
  
  loading.value = false
}

onMounted(() => {
  initData()
})
</script>

<style>
.test-graph {
  padding: 20px;
}
</style>