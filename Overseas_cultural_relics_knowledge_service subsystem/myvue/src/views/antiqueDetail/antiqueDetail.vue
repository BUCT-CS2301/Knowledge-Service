<template>
  <div class="detail-page">
    <MainHeader />

    <div class="detail-container">
      <!-- 面包屑 -->
      <nav class="breadcrumb">
        <router-link to="/index" class="breadcrumb-link">首页</router-link>
        <span class="breadcrumb-sep">/</span>
        <span class="breadcrumb-current">{{ form1.object_name || '文物详情' }}</span>
      </nav>

      <!-- 主体：左图右文 -->
      <div class="detail-main">
        <aside class="detail-gallery">
          <div
            class="gallery-frame"
            ref="galleryFrame"
            :style="galleryFrameStyle"
          >
            <div class="gallery-image-wrap">
              <img
                :src="imageUrl"
                :alt="form1.object_name || '文物图片'"
                class="gallery-image"
                referrerpolicy="no-referrer"
                @load="syncGalleryLayout"
                @error="onImageError"
              >
            </div>
            <p v-if="form1.accession_number" class="accession-tag">
              馆藏编号 {{ form1.accession_number }}
            </p>
          </div>
        </aside>

        <div class="detail-info" ref="detailInfo">
          <h1 class="relic-title">{{ form1.object_name || '未命名文物' }}</h1>

          <div class="meta-tags">
            <span v-if="form1.type" class="meta-tag">{{ form1.type }}</span>
            <span v-if="form1.material" class="meta-tag">{{ form1.material }}</span>
            <span v-if="form1.museum" class="meta-tag museum-tag">{{ form1.museum }}</span>
          </div>

          <div class="info-card">
            <h2 class="card-heading">基本信息</h2>
            <dl class="info-list">
              <div v-if="form1.time_period" class="info-item">
                <dt>文物时期</dt>
                <dd>{{ form1.time_period }}</dd>
              </div>
              <div v-if="form1.material" class="info-item">
                <dt>文物材质</dt>
                <dd>{{ form1.material }}</dd>
              </div>
              <div v-if="form1.type" class="info-item">
                <dt>文物品类</dt>
                <dd>{{ form1.type }}</dd>
              </div>
              <div v-if="form1.museum" class="info-item">
                <dt>收藏博物馆</dt>
                <dd>{{ form1.museum }}</dd>
              </div>
              <div v-if="form1.accession_number" class="info-item">
                <dt>馆藏编号</dt>
                <dd>{{ form1.accession_number }}</dd>
              </div>
              <div v-if="form1.dimensions" class="info-item">
                <dt>尺寸规格</dt>
                <dd>{{ form1.dimensions }}</dd>
              </div>
            </dl>
          </div>

          <div v-if="form1.description" class="info-card">
            <h2 class="card-heading">文物简介</h2>
            <p class="description-text">{{ form1.description }}</p>
          </div>

          <div class="action-row">
            <el-button
              :type="isStared ? 'default' : 'primary'"
              size="large"
              round
              class="collect-btn"
              @click="changeButton"
            >
              <i :class="isStared ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
              {{ isStared ? '已收藏' : '收藏文物' }}
            </el-button>
            <a
              v-if="form1.url"
              :href="form1.url"
              target="_blank"
              rel="noreferrer"
              class="source-link"
            >
              <i class="el-icon-link"></i>
              查看原馆址
            </a>
          </div>
        </div>
      </div>

      <!-- 评论区 -->
      <section class="comment-section">
        <h2 class="section-title">
          用户评论
          <span v-if="form1.commentView && form1.commentView.length" class="comment-count">
            {{ form1.commentView.length }} 条
          </span>
        </h2>

        <div
          v-for="item in form1.commentView"
          :key="item.cid || item.id"
          class="comment-item"
        >
          <div class="comment-avatar">{{ (item.username || '?').charAt(0).toUpperCase() }}</div>
          <div class="comment-body">
            <div class="comment-header">
              <span class="comment-author">{{ item.username }}</span>
              <span class="comment-time">{{ formatCommentTime(item.created_time || item.time) }}</span>
            </div>
            <p class="comment-text">{{ item.content || item.comment }}</p>
          </div>
        </div>

        <div v-if="!form1.commentView || !form1.commentView.length" class="no-comments">
          暂无评论，欢迎发表第一条见解
        </div>

        <div
          class="comment-input-area"
          @click="inputFocus"
          v-clickoutside="hideReplyBtn"
        >
          <div
            tabindex="0"
            contenteditable="true"
            id="replyInput"
            spellcheck="false"
            placeholder="写下你对这件文物的看法..."
            class="reply-input"
            @focus="showReplyBtn"
            @input="onDivInput($event)"
          ></div>
          <div class="reply-btn-box" v-show="btnShow">
            <el-button
              class="reply-btn"
              size="medium"
              type="primary"
              round
              @click="sendComment"
            >
              发表评论
            </el-button>
          </div>
        </div>
      </section>
    </div>

    <MainFooter />
  </div>
</template>

<script>
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'
import request from '@/api/request'
import { getApiRoot } from '@/config/api'
import { getArtifactImageUrl, handleArtifactImageError } from '@/utils/artifactPlaceholder'
import { collectRelic, uncollectRelic, getRelicComments } from '@/api/relic'
import { relicKeyFromObjectId } from '@/utils/relicKey'

const clickoutside = {
  bind (el, binding) {
    function documentHandler (e) {
      if (el.contains(e.target)) return
      if (binding.expression) binding.value(e)
    }
    el.vueClickOutside = documentHandler
    document.addEventListener('click', documentHandler)
  },
  unbind (el) {
    document.removeEventListener('click', el.vueClickOutside)
    delete el.vueClickOutside
  }
}

var storage = window.localStorage
export default {
  name: 'antiqueDetail',
  components: { MainFooter, MainHeader },
  directives: { clickoutside },
  data () {
    return {
      isStared: 0,
      imgErrorCount: 0,
      galleryMinHeight: 0,
      objectId: '',
      form1: {
        name: '古董',
        pic: 'src/assets/timg.jpeg',
        img_url: 'src/assets/timg.jpeg',
        period: '100-1-1',
        url: 'www.baidu.com',
        commentView: []
      },
      baseUrl: getApiRoot(),
      btnShow: false,
      replyComment: '',
      myName: 'GQS',
      myId: 12,
      comments: [
        {
          name: 'gqs',
          id: 12,
          comment: 'qqq',
          time: '2019-2-2'
        }
      ],
      form: {
        rid: '',
        uid: ''
      },
      form2: {
        rid: '',
        uid: ''
      },
      commentForm: {
        rid: '',
        uid: '',
        content: ''
      }
    }
  },
  computed: {
    galleryFrameStyle () {
      if (!this.galleryMinHeight || this.isMobileLayout) return {}
      return { minHeight: `${this.galleryMinHeight}px` }
    },
    isMobileLayout () {
      if (typeof window === 'undefined') return false
      return window.matchMedia('(max-width: 768px)').matches
    },
    imageUrl () {
      return getArtifactImageUrl({
        img_url: this.form1.img_url || this.form1.pic || '',
        accession_number: this.form1.accession_number,
        museum: this.form1.museum || this.form1.makers_name,
        object_name: this.form1.object_name
      })
    }
  },
  created () {
    this.pageInit()
  },
  mounted () {
    this.syncGalleryLayout()
    window.addEventListener('resize', this.syncGalleryLayout)
  },
  beforeUnmount () {
    window.removeEventListener('resize', this.syncGalleryLayout)
  },
  methods: {
    syncGalleryLayout () {
      this.$nextTick(() => {
        const info = this.$refs.detailInfo
        if (!info) return

        if (this.isMobileLayout) {
          this.galleryMinHeight = 0
          return
        }

        this.galleryMinHeight = info.offsetHeight
      })
    },
    onImageError (e) {
      handleArtifactImageError(e, {
        img_url: this.form1.img_url,
        accession_number: this.form1.accession_number,
        museum: this.form1.museum || this.form1.makers_name,
        object_name: this.form1.object_name
      })
    },
    pageInit () {
      const objectId = this.$route.query.objectId
      const userId = storage.getItem('username')
      this.objectId = objectId || ''
      this.form.rid = objectId ? String(relicKeyFromObjectId(objectId)) : (this.$route.query.id || '')
      this.form.uid = userId
      this.commentForm.uid = userId
      this.commentForm.rid = this.form.rid
      if (!objectId) {
        this.$message && this.$message.warning('缺少文物标识，无法加载详情')
        return
      }
      request.post('/search/detailByObjectId', { objectId, uid: userId || undefined })
        .then((response) => {
          if (response.data.state === 200 && response.data.data) {
            const data = response.data.data
            this.form1 = {
              ...this.form1,
              ...data,
              commentView: Array.isArray(data.commentView) ? data.commentView : []
            }
            this.isStared = this.form1.if_collect || 0
            this.syncGalleryLayout()
          } else {
            this.$message && this.$message.warning(response.data.message || '未找到该文物')
          }
        })
        .catch((error) => {
          console.log(error)
        })
    },
    formatCommentTime (value) {
      if (!value) return ''
      const d = new Date(value)
      return Number.isNaN(d.getTime()) ? String(value) : d.toLocaleString('zh-CN')
    },
    async loadRelicComments () {
      if (!this.form.rid && !this.objectId) return
      try {
        const res = await getRelicComments({ rid: this.form.rid, objectId: this.objectId })
        if (res.data.state === 200 && Array.isArray(res.data.data)) {
          this.form1.commentView = res.data.data
        }
      } catch (e) {
        console.error('加载评论失败', e)
      }
    },
    changeButton () {
      const objectId = this.objectId || this.$route.query.objectId
      this.form2.uid = storage.getItem('username')
      if (!this.form2.uid) {
        this.$message({ showClose: true, type: 'warning', message: '请先登录后再收藏' })
        return
      }
      if (!objectId) {
        this.$message({ showClose: true, type: 'info', message: '缺少文物标识，无法收藏' })
        return
      }
      const relicName = this.form1.object_name || ''
      if (this.form1.if_collect === 1) {
        uncollectRelic({ uid: this.form2.uid, objectId })
          .then((response) => {
            if (response.data.state === 200) {
              this.$message({ showClose: true, type: 'warning', message: '已取消收藏' })
              this.isStared = 0
              this.form1.if_collect = 0
            } else {
              this.$message.warning(response.data.message || '取消收藏失败')
            }
          })
          .catch((error) => {
            console.error(error)
            this.$message.error('取消收藏失败')
          })
      } else {
        collectRelic({ uid: this.form2.uid, objectId, relicName })
          .then((response) => {
            if (response.data.state === 200) {
              this.$message({ showClose: true, type: 'success', message: '收藏成功' })
              this.isStared = 1
              this.form1.if_collect = 1
            } else {
              this.$message.warning(response.data.message || '收藏失败')
            }
          })
          .catch((error) => {
            console.error(error)
            this.$message.error('收藏失败')
          })
      }
    },
    inputFocus () {
      var replyInput = document.getElementById('replyInput')
      replyInput.style.padding = '14px 16px'
      replyInput.style.borderColor = '#8b4513'
      replyInput.focus()
    },
    showReplyBtn () {
      this.btnShow = true
    },
    hideReplyBtn () {
      var replyInput = document.getElementById('replyInput')
      this.btnShow = false
      replyInput.style.padding = '14px 16px'
      replyInput.style.borderColor = '#e8e0d8'
    },
    sendComment () {
      if (storage.getItem('user_comment') > 3) {
        this.$message({
          showClose: true,
          type: 'warning',
          message: '您没有评论权限！'
        })
      } else {
        if (!this.replyComment) {
          this.$message({
            showClose: true,
            type: 'warning',
            message: '评论不能为空'
          })
        } else if (!this.commentForm.uid) {
          this.$message({ showClose: true, type: 'warning', message: '请先登录后再评论' })
        } else if (!/^\d+$/.test(String(this.commentForm.rid))) {
          this.$message({ showClose: true, type: 'info', message: '该文物暂不支持评论' })
        } else {
          let a = {}
          let input = document.getElementById('replyInput')
          let timeNow = new Date().getTime()
          let time = this.dateStr(timeNow)
          a.name = storage.getItem('username')
          a.comment = this.replyComment
          a.time = time
          a.id = storage.getItem('username')
          this.comments.push(a)
          this.commentForm.content = a.comment
          this.replyComment = ''
          input.innerHTML = ''
          const commentPayload = {
            ...this.commentForm,
            objectId: this.objectId || undefined,
            relicName: this.form1.object_name || ''
          }
          request.post('/search/searchById/comment', commentPayload
          ).then(async (response) => {
            if (response.data.state === 200) {
              this.$message.success('评论成功')
              await this.loadRelicComments()
            } else {
              this.$message.warning(response.data.message || '评论失败')
            }
          }).catch(function (error) {
            console.log(error)
            this.$message.error('评论失败')
          }.bind(this))
        }
      }
    },
    onDivInput: function (e) {
      this.replyComment = e.target.innerHTML
    },
    dateStr (date) {
      var time = new Date().getTime()
      time = parseInt((time - date) / 1000)
      var s
      if (time < 60 * 10) {
        return '刚刚'
      } else if (time < 60 * 60 && time >= 60 * 10) {
        s = Math.floor(time / 60)
        return s + '分钟前'
      } else if (time < 60 * 60 * 24 && time >= 60 * 60) {
        s = Math.floor(time / 60 / 60)
        return s + '小时前'
      } else if (time < 60 * 60 * 24 * 30 && time >= 60 * 60 * 24) {
        s = Math.floor(time / 60 / 60 / 24)
        return s + '天前'
      } else {
        var date2 = new Date(parseInt(date))
        return (
          date2.getFullYear() +
          '/' +
          (date2.getMonth() + 1) +
          '/' +
          date2.getDate()
        )
      }
    }
  }
}
</script>

<style lang="scss" scoped>
$primary: #8b4513;
$primary-dark: #6b3510;
$bg: #f5f0eb;
$card-bg: #fff;
$text: #333;
$text-muted: #888;
$border: #e8e0d8;

.detail-page {
  min-height: 100vh;
  background: $bg;
}

.detail-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 24px 5% 48px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 28px;
  font-size: 14px;
  color: $text-muted;

  .breadcrumb-link {
    color: $primary;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }

  .breadcrumb-sep {
    color: #ccc;
  }

  .breadcrumb-current {
    color: $text;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 300px;
  }
}

.detail-main {
  display: grid;
  grid-template-columns: minmax(280px, 420px) minmax(0, 1fr);
  gap: 36px;
  align-items: start;
  margin-bottom: 40px;
}

.detail-gallery {
  position: sticky;
  top: 72px;
  align-self: start;

  .gallery-frame {
    background: $card-bg;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(74, 55, 40, 0.12);
    border: 1px solid $border;
    display: flex;
    flex-direction: column;
    min-height: 420px;
  }

  .gallery-image-wrap {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
    background: #faf8f5;
    min-height: 0;
  }

  .gallery-image {
    max-width: 100%;
    max-height: 100%;
    width: auto;
    height: auto;
    object-fit: contain;
    display: block;
  }

  .accession-tag {
    margin: 0;
    padding: 12px 16px;
    text-align: center;
    font-size: 13px;
    color: $text-muted;
    letter-spacing: 0.5px;
    background: $card-bg;
    border-top: 1px solid $border;
    flex-shrink: 0;
  }
}

.detail-info {
  .relic-title {
    font-size: 28px;
    font-weight: 700;
    color: $text;
    margin: 0 0 16px;
    line-height: 1.3;
  }

  .meta-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 24px;

    .meta-tag {
      display: inline-block;
      padding: 4px 12px;
      background: rgba(139, 69, 19, 0.08);
      color: $primary;
      border-radius: 20px;
      font-size: 13px;
      border: 1px solid rgba(139, 69, 19, 0.15);

      &.museum-tag {
        background: rgba(74, 55, 40, 0.06);
        color: #5a4a3a;
        border-color: rgba(74, 55, 40, 0.12);
      }
    }
  }
}

.info-card {
  background: $card-bg;
  border-radius: 12px;
  padding: 20px 24px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(74, 55, 40, 0.06);
  border: 1px solid $border;

  .card-heading {
    font-size: 16px;
    font-weight: 600;
    color: $text;
    margin: 0 0 16px;
    padding-bottom: 10px;
    border-bottom: 2px solid $primary;
  }

  .info-list {
    margin: 0;
    padding: 0;
  }

  .info-item {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 16px;
    padding: 10px 0;
    border-bottom: 1px solid #f0ebe5;

    &:last-child {
      border-bottom: none;
    }

    dt {
      flex-shrink: 0;
      font-size: 14px;
      color: $text-muted;
      min-width: 80px;
    }

    dd {
      margin: 0;
      font-size: 14px;
      color: $text;
      text-align: right;
      line-height: 1.5;
    }
  }

  .description-text {
    margin: 0;
    font-size: 15px;
    line-height: 1.85;
    color: #555;
    text-align: justify;
  }
}

.action-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 8px;
  flex-wrap: wrap;

  .collect-btn {
    min-width: 140px;
  }

  .source-link {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 10px 20px;
    color: $primary;
    text-decoration: none;
    font-size: 14px;
    border: 1px solid rgba(139, 69, 19, 0.3);
    border-radius: 20px;
    transition: all 0.2s;

    &:hover {
      background: rgba(139, 69, 19, 0.06);
      border-color: $primary;
    }
  }
}

.comment-section {
  background: $card-bg;
  border-radius: 16px;
  padding: 28px 32px;
  box-shadow: 0 2px 16px rgba(74, 55, 40, 0.08);
  border: 1px solid $border;

  .section-title {
    font-size: 18px;
    font-weight: 600;
    color: $text;
    margin: 0 0 24px;
    padding-bottom: 12px;
    border-bottom: 2px solid $primary;
    display: flex;
    align-items: center;
    gap: 10px;

    .comment-count {
      font-size: 13px;
      font-weight: 400;
      color: $text-muted;
      background: #f5f0eb;
      padding: 2px 10px;
      border-radius: 12px;
    }
  }
}

.comment-item {
  display: flex;
  gap: 14px;
  padding: 16px 0;
  border-bottom: 1px solid #f0ebe5;

  &:last-of-type {
    border-bottom: none;
  }

  .comment-avatar {
    flex-shrink: 0;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: linear-gradient(135deg, $primary, $primary-dark);
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
    font-weight: 600;
  }

  .comment-body {
    flex: 1;
    min-width: 0;
  }

  .comment-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 6px;
  }

  .comment-author {
    font-size: 15px;
    font-weight: 600;
    color: $text;
  }

  .comment-time {
    font-size: 12px;
    color: $text-muted;
  }

  .comment-text {
    margin: 0;
    font-size: 14px;
    color: #555;
    line-height: 1.7;
  }
}

.no-comments {
  text-align: center;
  padding: 32px;
  color: $text-muted;
  font-size: 14px;
}

.comment-input-area {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0ebe5;

  .reply-input {
    min-height: 80px;
    line-height: 1.6;
    padding: 14px 16px;
    color: $text;
    background: #faf8f5;
    border: 1px solid $border;
    border-radius: 10px;
    outline: none;
    transition: border-color 0.2s;

    &:empty:before {
      content: attr(placeholder);
      color: #bbb;
    }

    &:focus:before {
      content: none;
    }

    &:focus {
      border-color: $primary;
      background: #fff;
    }
  }

  .reply-btn-box {
    margin-top: 12px;
    display: flex;
    justify-content: flex-end;
  }
}

:deep(.el-button--primary) {
  background-color: $primary;
  border-color: $primary;

  &:hover {
    background-color: $primary-dark;
    border-color: $primary-dark;
  }
}

@media (max-width: 768px) {
  .detail-container {
    padding: 16px 16px 32px;
  }

  .breadcrumb {
    margin-bottom: 16px;
    font-size: 13px;

    .breadcrumb-current {
      max-width: 160px;
    }
  }

  .detail-main {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .detail-gallery {
    position: static;
    width: 100%;

    .gallery-frame {
      min-height: auto;
    }

    .gallery-image-wrap {
      min-height: 220px;
      max-height: 50vh;
      padding: 16px;
    }

    .gallery-image {
      max-height: 46vh;
    }
  }

  .detail-info {
    .relic-title {
      font-size: 20px;
    }

    .meta-tags {
      gap: 6px;
      margin-bottom: 16px;

      .meta-tag {
        font-size: 12px;
        padding: 3px 10px;
      }
    }
  }

  .info-card {
    padding: 16px;
    margin-bottom: 12px;
  }

  .action-row {
    flex-direction: column;
    align-items: stretch;

    .collect-btn {
      width: 100%;
      min-width: 0;
    }

    .source-link {
      justify-content: center;
      width: 100%;
      box-sizing: border-box;
    }
  }

  .comment-section {
    padding: 20px 16px;
    border-radius: 12px;
  }

  .info-item {
    flex-direction: column;
    gap: 4px;

    dt {
      min-width: 0;
    }

    dd {
      text-align: left;
    }
  }
}
</style>
