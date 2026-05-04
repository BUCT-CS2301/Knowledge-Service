<template>
  <div class="comment-page">
    <div class="page-header">
      <h1 class="page-title">
        <i class="el-icon-message"></i>
        我的评论
      </h1>
      <p class="page-desc">管理您发表的评论</p>
    </div>

    <div v-if="comments.length === 0" class="empty-state">
      <div class="empty-icon">💬</div>
      <p class="empty-text">暂无评论</p>
      <router-link to="/" class="empty-link">
        <el-button type="primary">去浏览文物</el-button>
      </router-link>
    </div>

    <div v-else class="table-card">
      <el-table
        :data="comments"
        style="width: 100%"
        border
        :size="small"
        class="comment-table">

        <el-table-column
          label="评论ID"
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
          prop="relicName"
          width="180">
        </el-table-column>

        <el-table-column
          label="评论内容"
          prop="content">
        </el-table-column>

        <el-table-column
          label="评论时间"
          prop="time"
          width="180">
        </el-table-column>

        <el-table-column
          fixed="right"
          label="操作"
          width="100">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="danger"
              @click="shanchu(scope.row.id)">
              <i class="el-icon-delete"></i>
              删除
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
      comments: []
    }
  },
  methods: {
    pageInit () {
      const userId = localStorage.getItem('user_id')
      const allComments = JSON.parse(localStorage.getItem('comments') || '[]')
      this.comments = allComments.filter(c => c.userId === userId)
    },
    shanchu (id) {
      let comments = JSON.parse(localStorage.getItem('comments') || '[]')
      comments = comments.filter(c => c.id !== id)
      localStorage.setItem('comments', JSON.stringify(comments))
      this.pageInit()
      this.$message.success('删除评论成功')
    }
  },
  created () {
    this.pageInit()
  }
}
</script>

<style scoped>
.comment-page {
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

.comment-table {
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
