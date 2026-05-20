<template>
  <router-view v-if="$route.path === '/login'" />
  <el-container v-else class="app-shell">
    <el-aside width="232px" class="sidebar">
      <div class="brand">
        <strong>实验室设备</strong>
        <span>预约与归还系统</span>
      </div>
      <el-menu router :default-active="$route.path" class="nav">
        <el-menu-item index="/equipment">设备列表</el-menu-item>
        <el-menu-item index="/reserve">提交预约</el-menu-item>
        <el-menu-item index="/my-reservations">我的预约</el-menu-item>
        <el-menu-item index="/maintenance">维修工单</el-menu-item>
        <el-menu-item v-if="isAdminUser" index="/admin/reservations">预约审批</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="topbar">
        <div>
          <strong>{{ user?.name }}</strong>
          <span class="role">{{ user?.role }}</span>
        </div>
        <el-button @click="logout">退出登录</el-button>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { clearSession, getSession } from './session'

const route = useRoute()
const router = useRouter()

const user = computed(() => {
  route.fullPath
  return getSession()
})

const isAdminUser = computed(() => user.value?.role === 'ADMIN')

function logout() {
  clearSession()
  router.push('/login')
}
</script>
