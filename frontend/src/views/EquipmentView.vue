<template>
  <section class="page">
    <div class="page-title">
      <div>
        <h2>设备列表</h2>
        <p>查询设备状态，管理员可以新增设备或调整设备状态。</p>
      </div>
      <el-button v-if="isAdminUser" type="primary" @click="dialogVisible = true">新增设备</el-button>
    </div>

    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="按设备名称或编号搜索" clearable />
      <el-input v-model="query.category" placeholder="分类" clearable />
      <el-select v-model="query.status" placeholder="状态" clearable>
        <el-option label="可预约" :value="1" />
        <el-option label="占用中" :value="2" />
        <el-option label="维修中" :value="3" />
        <el-option label="停用" :value="0" />
      </el-select>
      <el-button @click="loadEquipment">查询</el-button>
    </div>

    <el-table :data="equipment" border>
      <el-table-column prop="assetNo" label="资产编号" width="150" />
      <el-table-column prop="name" label="设备名称" />
      <el-table-column prop="category" label="分类" width="130" />
      <el-table-column prop="location" label="位置" />
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag class="status-tag" :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column v-if="isAdminUser" label="管理" width="170">
        <template #default="{ row }">
          <el-select v-model="row.status" size="small" @change="changeStatus(row)">
            <el-option label="停用" :value="0" />
            <el-option label="可预约" :value="1" />
            <el-option label="占用中" :value="2" />
            <el-option label="维修中" :value="3" />
          </el-select>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="新增设备" width="480px">
      <el-form :model="newEquipment" label-position="top">
        <el-form-item label="资产编号">
          <el-input v-model="newEquipment.assetNo" />
        </el-form-item>
        <el-form-item label="设备名称">
          <el-input v-model="newEquipment.name" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="newEquipment.category" />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="newEquipment.location" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createEquipment">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'
import { isAdmin } from '../session'

const equipment = ref([])
const dialogVisible = ref(false)
const isAdminUser = isAdmin()
const query = reactive({
  keyword: '',
  category: '',
  status: null
})
const newEquipment = reactive({
  assetNo: '',
  name: '',
  category: '',
  location: ''
})

function statusText(status) {
  return { 0: '停用', 1: '可预约', 2: '占用中', 3: '维修中' }[status] || '未知'
}

function statusType(status) {
  return { 0: 'info', 1: 'success', 2: 'warning', 3: 'danger' }[status] || 'info'
}

async function loadEquipment() {
  equipment.value = await api.get('/equipment', { params: query })
}

async function changeStatus(row) {
  await api.patch(`/equipment/${row.id}/status`, { status: row.status })
  ElMessage.success('状态已更新')
}

async function createEquipment() {
  await api.post('/equipment', newEquipment)
  Object.assign(newEquipment, { assetNo: '', name: '', category: '', location: '' })
  dialogVisible.value = false
  ElMessage.success('设备已新增')
  await loadEquipment()
}

onMounted(loadEquipment)
</script>
