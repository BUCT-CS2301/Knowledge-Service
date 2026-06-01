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
      <el-table :data="comments" style="width: 100%" border :size="small" class="comment-table">
        <el-table-column label="评论ID" prop="id" width="100" />
        <el-table-column label="文物ID" prop="relicId" width="100" />
        <el-table-column label="文物名称" prop="relicName" width="180" />
        <el-table-column label="评论内容" prop="content" />
        <el-table-column label="评论时间" prop="time" width="180" />
        <el-table-column fixed="right" label="操作" width="100">
          <template #default="scope">
            <el-button size="small" type="danger" @click="shanchu(scope.row.id)">
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
import {
  getCurrentUserId,
  getUserComments,
  deleteUserComment,
  parseJsonResult
} from '@/api/user'

export default {
  data () {
    return {
      comments: []
    }
  },
  methods: {
    formatTime (value) {
      if (!value) return '—'
      const d = new Date(value)
      return Number.isNaN(d.getTime()) ? String(value) : d.toLocaleString('zh-CN')
    },
    async pageInit () {
      const userId = getCurrentUserId()
      if (!userId) {
        this.comments = []
        return
      }
      try {
        const res = await getUserComments(userId)
        const list = parseJsonResult(res) || []
        this.comments = list.map((item) => ({
          id: item.cid,
          relicId: item.rid,
          relicName: item.relicname || '—',
          content: item.content || '',
          time: this.formatTime(item.created_time)
        }))
      } catch (error) {
        console.error('加载评论失败:', error)
        this.$message.error('加载评论失败')
        this.comments = []
      }
    },
    async shanchu (id) {
      try {
        await deleteUserComment(id)
        this.$message.success('删除评论成功')
        this.pageInit()
      } catch (error) {
        this.$message.error(error.message || '删除评论失败')
      }
    }
  },
  created () {
    this.pageInit()
  }
}
</script>

<style scoped>
.comment-page { min-height: 400px; }
.page-header { margin-bottom: 32px; }
.page-title {
  font-size: 24px; font-weight: 600; color: #333; margin: 0 0 8px;
  display: flex; align-items: center; gap: 12px;
}
.page-desc { font-size: 14px; color: #999; margin: 0; }
.empty-state { text-align: center; padding: 80px 0; }
.empty-icon { font-size: 64px; margin-bottom: 20px; }
.empty-text { font-size: 16px; color: #999; margin: 0 0 24px; }
.empty-link { text-decoration: none; }
.table-card { background: #fafafa; border-radius: 12px; padding: 16px; }
.comment-table { background: white; border-radius: 8px; }
:deep(.el-button--primary) {
  background: linear-gradient(135deg, #8B4513 0%, #CD853F 100%);
  border: none; border-radius: 8px;
  &:hover { background: linear-gradient(135deg, #6B3510 0%, #A06030 100%); }
}
:deep(.el-button--danger) { border-radius: 6px; }
:deep(.el-table th) { background: #f8f9fa; font-weight: 500; color: #666; }
:deep(.el-table tr:hover) { background: rgba(139, 69, 19, 0.05); }
</style>
