<template>
  <div>
    <MainHeader />
    <div class="result-page">
      <div class="result-toolbar">
        <div class="toolbar-left">
          <h2 class="page-title">查询结果</h2>
          <p v-if="summaryText" class="summary">{{ summaryText }}</p>
        </div>
        <div class="toolbar-right">
          <el-select
            v-model="filterDynasty"
            clearable
            placeholder="二次筛选：朝代"
            class="filter-select"
            @change="onSecondaryFilter"
          >
            <el-option
              v-for="d in dynastyOptions"
              :key="d"
              :label="d"
              :value="d"
            />
          </el-select>
          <el-select
            v-model="filterMaterial"
            clearable
            placeholder="二次筛选：材质"
            class="filter-select"
            @change="onSecondaryFilter"
          >
            <el-option
              v-for="m in materialOptions"
              :key="m"
              :label="m"
              :value="m"
            />
          </el-select>
          <el-button @click="sortByName('asc')">名称升序</el-button>
          <el-button @click="sortByName('desc')">名称降序</el-button>
          <el-button type="primary" :disabled="!displayList.length" @click="handleExportCsv">
            导出 CSV
          </el-button>
          <el-button type="primary" plain :disabled="!displayList.length" @click="handleExportJson">
            导出 JSON
          </el-button>
        </div>
      </div>

      <el-alert
        v-if="usingMock"
        type="warning"
        :closable="false"
        show-icon
        title="当前为演示数据（后端未连接）"
        class="mock-alert"
      />

      <div v-loading="loading" class="result-body">
        <el-empty v-if="!loading && !displayList.length" description="未找到符合条件的文物" />

        <el-row v-else :gutter="20">
          <el-col
            v-for="item in pagedList"
            :key="item.id"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
          >
            <router-link :to="{ path: '/antiqueDetail', query: { id: item.id } }" class="card-link">
              <el-card shadow="hover" class="artifact-card">
                <img
                  :src="item.img_url || placeholderImg"
                  class="artifact-img"
                  alt=""
                  @error="onImgError"
                >
                <div class="artifact-info">
                  <div class="artifact-name">{{ item.object_name || '未命名' }}</div>
                  <div class="artifact-meta">
                    <span v-if="item.cat2">{{ item.cat2 }}</span>
                    <span v-if="item.cat1"> · {{ item.cat1 }}</span>
                  </div>
                </div>
              </el-card>
            </router-link>
          </el-col>
        </el-row>

        <div v-if="displayList.length" class="pagination-wrap">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[8, 12, 24, 48]"
            :total="displayList.length"
            layout="total, sizes, prev, pager, next"
            background
          />
        </div>
      </div>
    </div>
    <MainFooter />
  </div>
</template>

<script>
import MainHeader from '../../components/MainHeader/MainHeader.vue'
import MainFooter from '../../components/MainFooter/MainFooter.vue'
import {
  searchObscure,
  searchMulti,
  searchClassify,
  parseSearchResponse
} from '@/api/search'
import { exportToCsv, exportToJson } from '@/utils/export'
import { MOCK_ARTIFACTS } from '@/utils/mockArtifacts'
import { ElMessage } from 'element-plus'

export default {
  name: 'SearchResult',
  components: { MainHeader, MainFooter },
  data () {
    return {
      loading: false,
      usingMock: false,
      allList: [],
      displayList: [],
      filterDynasty: '',
      filterMaterial: '',
      currentPage: 1,
      pageSize: 12,
      summaryText: '',
      placeholderImg: 'https://picsum.photos/seed/artifact/400/300',
      dynastyOptions: [],
      materialOptions: []
    }
  },
  computed: {
    pagedList () {
      const start = (this.currentPage - 1) * this.pageSize
      return this.displayList.slice(start, start + this.pageSize)
    }
  },
  watch: {
    '$route.query': {
      handler () {
        this.fetchResults()
      },
      deep: true
    }
  },
  created () {
    this.fetchResults()
  },
  methods: {
    async fetchResults () {
      const q = this.$route.query
      this.loading = true
      this.usingMock = false
      this.filterDynasty = q.filterDynasty || ''
      this.filterMaterial = q.filterMaterial || ''
      this.currentPage = 1

      try {
        let list = []
        const mode = q.mode || (q.keyword ? 'obscure' : (q.v_1 || q.v_2 || q.v_3 || q.v_4 ? 'multi' : 'obscure'))

        if (mode === 'obscure') {
          const keyword = q.keyword || ''
          if (!keyword) {
            this.allList = []
            this.applySecondaryFilter()
            this.summaryText = '请输入关键字后搜索'
            return
          }
          const res = await searchObscure({ keyword })
          list = parseSearchResponse(res).list
          this.summaryText = `关键字「${keyword}」共 ${list.length} 条结果`
        } else if (mode === 'multi') {
          const params = {
            v_1: q.v_1 || undefined,
            v_2: q.v_2 || undefined,
            v_3: q.v_3 || undefined,
            v_4: q.v_4 || undefined
          }
          const res = await searchMulti(params)
          list = parseSearchResponse(res).list
          this.summaryText = `组合查询共 ${list.length} 条结果`
        } else if (mode === 'classify') {
          const res = await searchClassify({
            c: q.c,
            v_1: q.v_1,
            v_2: q.v_2,
            v_3: q.v_3,
            v_4: q.v_4
          })
          list = parseSearchResponse(res).list
          this.summaryText = `分类查询共 ${list.length} 条结果`
        }

        this.allList = list
        this.buildFilterOptions(list)
        this.applySecondaryFilter()
      } catch (err) {
        console.warn(err)
        this.usingMock = true
        this.allList = [...MOCK_ARTIFACTS]
        this.buildFilterOptions(this.allList)
        this.applySecondaryFilter()
        this.summaryText = `演示数据 ${this.displayList.length} 条（${err.message || '接口异常'}）`
        ElMessage.warning('无法连接后端，已加载演示数据')
      } finally {
        this.loading = false
      }
    },
    buildFilterOptions (list) {
      const dynasties = new Set()
      const materials = new Set()
      list.forEach((item) => {
        if (item.cat2) dynasties.add(item.cat2)
        if (item.cat1) materials.add(item.cat1)
      })
      this.dynastyOptions = [...dynasties].sort()
      this.materialOptions = [...materials].sort()
    },
    applySecondaryFilter () {
      let list = [...this.allList]
      if (this.filterDynasty) {
        list = list.filter((item) => item.cat2 === this.filterDynasty)
      }
      if (this.filterMaterial) {
        list = list.filter((item) => item.cat1 === this.filterMaterial)
      }
      this.displayList = list
      this.currentPage = 1
    },
    onSecondaryFilter () {
      this.applySecondaryFilter()
    },
    sortByName (order) {
      this.displayList.sort((a, b) => {
        const na = (a.object_name || '').localeCompare(b.object_name || '')
        return order === 'asc' ? na : -na
      })
    },
    handleExportCsv () {
      if (exportToCsv(this.displayList)) {
        ElMessage.success('CSV 已导出')
      }
    },
    handleExportJson () {
      if (exportToJson(this.displayList)) {
        ElMessage.success('JSON 已导出')
      }
    },
    onImgError (e) {
      e.target.src = this.placeholderImg
    }
  }
}
</script>

<style lang="scss" scoped>
.result-page {
  min-height: calc(100vh - 160px);
  padding: 32px 5%;
  background: #f5f5f5;
}

.result-toolbar {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 24px;
}

.page-title {
  margin: 0 0 8px;
  font-size: 22px;
  color: #333;
}

.summary {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.toolbar-right {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
}

.filter-select {
  width: 160px;
}

.mock-alert {
  margin-bottom: 16px;
}

.result-body {
  min-height: 200px;
}

.card-link {
  text-decoration: none;
  color: inherit;
  display: block;
  margin-bottom: 20px;
}

.artifact-card {
  overflow: hidden;
  transition: transform 0.2s;

  &:hover {
    transform: translateY(-4px);
  }
}

.artifact-img {
  width: 100%;
  height: 220px;
  object-fit: cover;
  display: block;
}

.artifact-info {
  padding: 4px 0;
}

.artifact-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.artifact-meta {
  font-size: 12px;
  color: #999;
  margin-top: 6px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

:deep(.el-button--primary) {
  background-color: #8b4513;
  border-color: #8b4513;

  &:hover {
    background-color: #6b3510;
    border-color: #6b3510;
  }
}
</style>
