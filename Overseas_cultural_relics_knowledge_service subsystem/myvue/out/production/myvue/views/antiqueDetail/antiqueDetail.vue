<template>
  <div>
    <MainHeader></MainHeader>
    <antique></antique>
    <comment></comment>
    <MainFooter></MainFooter>
  </div>
</template>

<script>
import antique from '../../components/antique/antique'
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'
import comment from '../../components/comment/comment'
export default {
  name: 'antiqueDetail',
  components: {antique, MainFooter, MainHeader, comment}
}
</script>

<style scoped>

</style>
