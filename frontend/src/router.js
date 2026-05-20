import { createRouter, createWebHistory } from 'vue-router'
import { getSession } from './session'
import LoginView from './views/LoginView.vue'
import EquipmentView from './views/EquipmentView.vue'
import ReservationCreateView from './views/ReservationCreateView.vue'
import MyReservationsView from './views/MyReservationsView.vue'
import AdminReservationsView from './views/AdminReservationsView.vue'
import MaintenanceView from './views/MaintenanceView.vue'

const routes = [
  { path: '/login', component: LoginView },
  { path: '/', redirect: '/equipment' },
  { path: '/equipment', component: EquipmentView },
  { path: '/reserve', component: ReservationCreateView },
  { path: '/my-reservations', component: MyReservationsView },
  { path: '/admin/reservations', component: AdminReservationsView },
  { path: '/maintenance', component: MaintenanceView }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.path !== '/login' && !getSession()) {
    return '/login'
  }
})

export default router
