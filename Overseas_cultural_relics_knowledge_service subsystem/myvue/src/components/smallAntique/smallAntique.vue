<template>
  <div>
    <router-link to="@views/antique/antique.vue">
      <div>
        <img :src="pic"  alt="">
      </div>
      <div style="font-size: 16px;font-weight: 500;height:30px;overflow: hidden;text-overflow: ellipsis;">
        {{ title }}
      </div>
      <div style="font-size: 12px;height: 50px;overflow: hidden;text-overflow: ellipsis;">
        {{ info }}
      </div>
    </router-link>
  </div>
</template>

<script>

export default {
  name: 'smallAntique',
  props: {
    title: {
      default: '',
      type: String
    },
    pic: {
      default: '',
      type: String
    },
    info: {
      default: '',
      type: String
    }
  }
}

</script>

<style scoped>
.product:hover{
  cursor: pointer;
  transform: scale(1.05);
  border-top: 1px solid red!important;
}
</style>
