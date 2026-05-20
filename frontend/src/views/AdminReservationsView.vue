<template>
  <section class="page">
    <div class="page-title">
      <div>
        <h2>预约审批</h2>
        <p>管理员审批学生提交的设备预约。</p>
      </div>
      <el-button @click="loadReservations">刷新</el-button>
    </div>

    <el-table :data="reservations" border>
      <el-table-column prop="id" label="编号" width="80" />
      <el-table-column prop="userId" label="用户ID" width="90" />
      <el-table-column prop="equipmentId" label="设备ID" width="90" />
      <el-table-column prop="startTime" label="开始时间" />
      <el-table-column prop="endTime" label="结束时间" />
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag>{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审批" width="120">
        <template #default="{ row }">
          <el-button size="small" type="primary" :disabled="row.status !== 0" @click="approve(row)">通过</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const reservations = ref([])

function statusText(status) {
  return { 0: '待审批', 1: '已通过', 2: '已取消', 3: '已完成', 4: '已拒绝' }[status] || '未知'
}

async function loadReservations() {
  reservations.value = await api.get('/reservations')
}

async function approve(row) {
  await api.post(`/reservations/${row.id}/approve`, { reviewComment: '审批通过' })
  ElMessage.success('预约已审批')
  await loadReservations()
}

onMounted(loadReservations)
</script>
