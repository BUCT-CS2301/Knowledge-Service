<template>
  <div>
    <MainHeader></MainHeader>
    <el-card :body-style="{ padding: '0px' }">
      <div class="block">
        <span class="demonstration"></span>
        <img :src="form1.img_url" class="image">
      </div>
      <div style="padding: 14px;">
        <span>文物名称：{{form1.object_name}}</span>
        <br>
        <span>文物时期：{{form1.time_period}}</span>
        <br>
        <span>文物详情请访问：
          <a v-bind:herf="form1.url">原地址</a>
        </span>
        <div class="bottom clearfix">
          <el-button type="primary" @click="changeButton" v-if="!isStared">
            收藏
            <i class="el-icon-star-off"></i>
          </el-button>
        </div>
        <div class="bottom clearfix">
          <el-button type="primary" @click="changeButton" v-if="isStared">
            已收藏
            <i class="el-icon-star-on"></i>
          </el-button>
        </div>
      </div>
    </el-card>
    <div>
      <div
        v-for="(item, i) in form1.commentView"
        :key="i"
        class="author-title reply-father"
      >
        <div class="author-info">
          <span class="author-name">{{ item.username }}</span>
          <span class="author-info">{{ item.content }}</span>
          <span class="author-time">{{ item.created_time }}</span>
        </div>
        <div class="talk-box">
          <p>
            <span class="reply">{{ item.comment }}</span>
          </p>
        </div>
      </div>
      <div  @click="inputFocus" v-clickoutside="hideReplyBtn" class="my-reply">
        <div class="reply-info">
          <div
            tabindex="0"
            contenteditable="true"
            id="replyInput"
            spellcheck="false"
            placeholder="输入评论..."
            class="reply-input"
            @focus="showReplyBtn"
            @input="onDivInput($event)"
          ></div>
        </div>
        <div class="reply-btn-box" v-show="btnShow">
          <el-button
            class="reply-btn"
            size="medium"
            @click="sendComment"
            type="primary"
          >
            发表评论
          </el-button>
        </div>          <!--发表评论按钮-->
      </div>
    </div>
    <MainFooter></MainFooter>
  </div>
</template>

<script>
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'
import axios from 'axios'
var storage = window.localStorage
export default {
  name: 'antiqueDetail',
  components: {MainFooter, MainHeader},
  data () {
    return {
      isStared: 0,
      form1: {
        name: '古董',
        pic: 'src/assets/timg.jpeg',
        period: '100-1-1',
        url: 'www.baidu.com'
      },
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
  created () {
    this.pageInit()
  },
  methods: {
    pageInit () {
      this.form.rid = this.$route.query.id
      this.form.uid = storage.getItem('username')
      this.commentForm.uid = storage.getItem('username')
      this.commentForm.rid = this.form.rid
      console.log(this.form)
      axios.post('http://localhost:8085/search/searchById', this.form
      ).then((response) => {
        this.form1 = response.data.data
        console.log(this.form1)
        this.isStared = this.form1.if_collect
      })
        .catch(function (error) {
          console.log(error)
        })
    },
    changeButton () {
      this.form2.rid = this.$route.query.id
      this.form2.rid = this.form.rid
      this.form2.uid = storage.getItem('username')
      if (this.form1.if_collect === 1) {
        axios.post('http://localhost:8085/user_admin/deleteCollect', this.form2// 注意数据是直接保存到json对象
        ).then((response) => {
          if (response.data.state === 200) {
            this.$message({
              showClose: true,
              type: 'warning',
              message: '已取消收藏'
            })
            this.isStared = 0
            this.form1.if_collect = 0
          }
        }
        ).catch(function (error) {
          console.log(error)
        })
      } else {
        axios.post('http://localhost:8085/search/searchById/collect', this.form2// 注意数据是直接保存到json对象
        ).then((response) => {
          if (response.data.state === 200) {
            this.$message({
              showClose: true,
              type: 'success',
              message: '收藏成功'
            })
            this.isStared = 1
            this.form1.if_collect = 1
          }
        }
        ).catch(function (error) {
          console.log(error)
        })
      }
    },
    inputFocus () {
      var replyInput = document.getElementById('replyInput')
      replyInput.style.padding = '8px 8px'
      replyInput.style.border = '2px solid blue'
      replyInput.focus()
    },
    showReplyBtn () {
      this.btnShow = true
    },
    hideReplyBtn () {
      var replyInput = document.getElementById('replyInput')
      this.btnShow = false
      replyInput.style.padding = '10px'
      replyInput.style.border = 'none'
    },
    sendComment () {
      if (storage.getItem('user_comment') > 3) {          /*  */
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
          axios.post('http://localhost:8085/search/searchById/comment', this.commentForm// 注意数据是直接保存到json对象
          ).then((response) => {
            if (response.data.state === 200) {
              alert('评论成功！')
              this.$router.go(0)
            }
            console.log(response.data)
          }).catch(function (error) {
            console.log(error)
          })
        }
      }
    },
    onDivInput: function (e) {
      this.replyComment = e.target.innerHTML
    },
    dateStr (date) {
      // 获取js 时间戳
      var time = new Date().getTime()
      // 去掉 js 时间戳后三位，与php 时间戳保持一致
      time = parseInt((time - date) / 1000)
      // 存储转换值
      var s
      if (time < 60 * 10) {
        // 十分钟内
        return '刚刚'
      } else if (time < 60 * 60 && time >= 60 * 10) {
        // 超过十分钟少于1小时
        s = Math.floor(time / 60)
        return s + '分钟前'
      } else if (time < 60 * 60 * 24 && time >= 60 * 60) {
        // 超过1小时少于24小时
        s = Math.floor(time / 60 / 60)
        return s + '小时前'
      } else if (time < 60 * 60 * 24 * 30 && time >= 60 * 60 * 24) {
        // 超过1天少于30天内
        s = Math.floor(time / 60 / 60 / 24)
        return s + '天前'
      } else {
        // 超过30天ddd
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
  .image{
    width: 300px;
    height: 300px;
  }
.my-reply {
  padding: 10px;
  background-color: #fafbfc;
  .header-img {
    display: inline-block;
    vertical-align: top;
  }
  .reply-info {
    display: inline-block;
    margin-left: 5px;
    width: 90%;
    //根据屏幕宽度自适应
    @media screen and (max-width:1200px) {
      width: 80%;
    }
    .reply-input {
      min-height: 20px;
      line-height: 22px;
      padding: 10px 10px;
      color: black;
      background-color: #fff;
      border-radius: 5px;
      // &:empty::before{
      //   content attr(placeholder);
      // }
      &:empty:before {
        content: attr(placeholder);
      }
      &:focus:before {
        content: none;
      }
      &:focus {
        padding: 8px 8px;
        border: 2px solid blue;
        box-shadow: none;
        outline: none;
      }
    }
  }
  .reply-btn-box {
    margin-top:10px;
    height: 25px;
    display: inline-block;
    .reply-btn {
      position: relative;
      float: right;
      margin-left: 15px;
      // margin-right: 15px;
    }
  }
}
.my-comment-reply {
  margin-left: 50px;
  .reply-input {
    width: auto;
  }
}
.author-title:not(:last-child) {
  border-bottom: 1px solid rgba(74, 136, 199, .3)
}
.reply-father {
  padding: 10px;

  .header-img {
    display: inline-block;
    vertical-align: top;
  }

  .author-info {
    display: inline-block;
    margin-left: 5px;
    width: 60%;
    height: 40px;
    line-height: 20px;

    span {
      display: block;
      cursor: pointer;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }

    .author-name {
      color: #000;
      font-size: 18px;
      font-weight: bold;
    }

    .author-time {
      font-size: 14px;
    }
  }

  .icon-btn {
    width: 30%;
    padding: 0 !important;
    float: right;
    @media screen and (max-width: 1200px) {
      width: 20%;
      padding: 7px;
    }

    span {
      cursor: pointer;
    }

    .iconfont {
      margin: 0 5px;
    }
  }

  .talk-box {
    margin: 0 50px;

    p {
      margin: 0;
    }

    .reply {
      font-size: 16px;
      color: #000;
    }
  }

  .reply-box {
    margin: 10px 0 0 50px;
    background-color: #efefef;
  }
}
</style>
