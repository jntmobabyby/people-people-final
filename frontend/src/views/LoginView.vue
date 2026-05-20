<template>
  <main class="login-page">
    <section class="login-panel">
      <h1>实验室设备预约与归还系统</h1>
      <p>使用测试账号登录后进行设备预约、归还和维修上报。</p>
      <el-form :model="form" label-position="top" @submit.prevent>
        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="admin / stu001 / stu002" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="login">
          登录
        </el-button>
      </el-form>
    </section>
  </main>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api'
import { setSession } from '../session'

const router = useRouter()
const loading = ref(false)
const form = reactive({
  username: 'stu001',
  password: '123456'
})

async function login() {
  loading.value = true
  try {
    const user = await api.post('/auth/login', form)
    setSession(user)
    ElMessage.success('登录成功')
    router.push('/equipment')
  } finally {
    loading.value = false
  }
}
</script>
