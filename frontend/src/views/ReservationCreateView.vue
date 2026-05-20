<template>
  <section class="page">
    <div class="page-title">
      <div>
        <h2>提交预约</h2>
        <p>选择可预约设备和时间段，系统会自动校验时间冲突。</p>
      </div>
    </div>

    <el-form :model="form" label-position="top" style="max-width: 640px">
      <el-form-item label="设备">
        <el-select v-model="form.equipmentId" filterable placeholder="请选择设备" style="width: 100%">
          <el-option
            v-for="item in availableEquipment"
            :key="item.id"
            :label="`${item.assetNo} - ${item.name}`"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="开始时间">
        <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
      </el-form-item>
      <el-form-item label="结束时间">
        <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
      </el-form-item>
      <el-button type="primary" @click="submitReservation">提交预约</el-button>
    </el-form>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const equipment = ref([])
const form = reactive({
  equipmentId: null,
  startTime: '',
  endTime: ''
})
const availableEquipment = computed(() => equipment.value.filter((item) => item.status === 1))

async function loadEquipment() {
  equipment.value = await api.get('/equipment')
}

async function submitReservation() {
  await api.post('/reservations', form)
  ElMessage.success('预约已提交，等待管理员审批')
  Object.assign(form, { equipmentId: null, startTime: '', endTime: '' })
}

onMounted(loadEquipment)
</script>
