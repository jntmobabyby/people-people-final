<template>
  <section class="page">
    <div class="page-title">
      <div>
        <h2>我的预约</h2>
        <p>查看自己的预约状态，未完成预约可以取消。</p>
      </div>
      <el-button @click="loadReservations">刷新</el-button>
    </div>

    <el-table :data="reservations" border>
      <el-table-column prop="id" label="编号" width="80" />
      <el-table-column prop="equipmentId" label="设备ID" width="100" />
      <el-table-column prop="startTime" label="开始时间" />
      <el-table-column prop="endTime" label="结束时间" />
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag class="status-tag">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button size="small" :disabled="row.status === 2 || row.status === 3" @click="cancelReservation(row)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'
import { getSession } from '../session'

const reservations = ref([])

function statusText(status) {
  return { 0: '待审批', 1: '已通过', 2: '已取消', 3: '已完成', 4: '已拒绝' }[status] || '未知'
}

async function loadReservations() {
  const user = getSession()
  reservations.value = await api.get('/reservations', { params: { userId: user.userId } })
}

async function cancelReservation(row) {
  await api.post(`/reservations/${row.id}/cancel`)
  ElMessage.success('预约已取消')
  await loadReservations()
}

onMounted(loadReservations)
</script>
