<template>
  <section class="page">
    <div class="page-title">
      <div>
        <h2>维修工单</h2>
        <p>上报设备故障后，系统会自动把设备置为维修中。</p>
      </div>
      <el-button @click="loadTickets">刷新</el-button>
    </div>

    <el-form :model="form" label-position="top" style="max-width: 640px; margin-bottom: 20px">
      <el-form-item label="故障设备">
        <el-select v-model="form.equipmentId" filterable placeholder="请选择设备" style="width: 100%">
          <el-option
            v-for="item in equipment"
            :key="item.id"
            :label="`${item.assetNo} - ${item.name}`"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="故障描述">
        <el-input v-model="form.faultDesc" type="textarea" :rows="3" />
      </el-form-item>
      <el-button type="primary" @click="createTicket">提交工单</el-button>
    </el-form>

    <el-table :data="tickets" border>
      <el-table-column prop="id" label="编号" width="80" />
      <el-table-column prop="equipmentId" label="设备ID" width="90" />
      <el-table-column prop="reporterId" label="上报人" width="90" />
      <el-table-column prop="faultDesc" label="故障描述" />
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag>{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'
import { getSession } from '../session'

const equipment = ref([])
const tickets = ref([])
const form = reactive({
  equipmentId: null,
  faultDesc: ''
})

function statusText(status) {
  return { 0: '待处理', 1: '维修中', 2: '已关闭' }[status] || '未知'
}

async function loadEquipment() {
  equipment.value = await api.get('/equipment')
}

async function loadTickets() {
  tickets.value = await api.get('/maintenance-tickets')
}

async function createTicket() {
  const user = getSession()
  await api.post('/maintenance-tickets', {
    equipmentId: form.equipmentId,
    reporterId: user.userId,
    faultDesc: form.faultDesc
  })
  Object.assign(form, { equipmentId: null, faultDesc: '' })
  ElMessage.success('维修工单已提交')
  await Promise.all([loadEquipment(), loadTickets()])
}

onMounted(() => Promise.all([loadEquipment(), loadTickets()]))
</script>
