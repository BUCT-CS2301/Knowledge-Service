<template>
  <div class="favourite-page">
    <div class="page-header">
      <h1 class="page-title">
        <i class="el-icon-star-on"></i>
        我的收藏
      </h1>
      <p class="page-desc">管理您收藏的文物</p>
    </div>

    <div v-if="collections.length === 0" class="empty-state">
      <div class="empty-icon">📭</div>
      <p class="empty-text">暂无收藏的文物</p>
      <router-link to="/" class="empty-link">
        <el-button type="primary">去浏览文物</el-button>
      </router-link>
    </div>

    <div v-else class="table-card">
      <el-table
        :data="collections"
        style="width: 100%"
        border
        :size="small"
        class="collection-table">

        <el-table-column
          label="收藏ID"
          prop="id"
          width="100">
        </el-table-column>

        <el-table-column
          label="文物ID"
          prop="relicId"
          width="100">
        </el-table-column>

        <el-table-column
          label="文物名称"
          prop="relicName">
        </el-table-column>

        <el-table-column
          label="收藏时间"
          prop="time">
        </el-table-column>

        <el-table-column
          fixed="right"
          label="操作"
          width="120">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="danger"
              @click="removeCollection(scope.row.id)">
              <i class="el-icon-delete"></i>
              取消收藏
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
export default {
  data () {
    return {
      collections: []
    }
  },
  methods: {
    pageInit () {
      const userId = localStorage.getItem('user_id')
      const allCollections = JSON.parse(localStorage.getItem('collections') || '[]')
      this.collections = allCollections.filter(c => c.userId === userId)
    },
    removeCollection (id) {
      let collections = JSON.parse(localStorage.getItem('collections') || '[]')
      collections = collections.filter(c => c.id !== id)
      localStorage.setItem('collections', JSON.stringify(collections))
      this.pageInit()
      this.$message.success('取消收藏成功')
    }
  },
  created () {
    this.pageInit()
  }
}
</script>

<style scoped>
.favourite-page {
  min-height: 400px;
}

.page-header {
  margin-bottom: 32px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-desc {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.empty-state {
  text-align: center;
  padding: 80px 0;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.empty-text {
  font-size: 16px;
  color: #999;
  margin: 0 0 24px;
}

.empty-link {
  text-decoration: none;
}

.table-card {
  background: #fafafa;
  border-radius: 12px;
  padding: 16px;
}

.collection-table {
  background: white;
  border-radius: 8px;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #8B4513 0%, #CD853F 100%);
  border: none;
  border-radius: 8px;

  &:hover {
    background: linear-gradient(135deg, #6B3510 0%, #A06030 100%);
  }
}

:deep(.el-button--danger) {
  border-radius: 6px;
}

:deep(.el-table th) {
  background: #f8f9fa;
  font-weight: 500;
  color: #666;
}

:deep(.el-table tr:hover) {
  background: rgba(139, 69, 19, 0.05);
}
</style>
