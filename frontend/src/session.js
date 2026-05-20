const KEY = 'lab-reservation-user'

export function getSession() {
  const raw = localStorage.getItem(KEY)
  return raw ? JSON.parse(raw) : null
}

export function setSession(user) {
  localStorage.setItem(KEY, JSON.stringify(user))
}

export function clearSession() {
  localStorage.removeItem(KEY)
}

export function isAdmin() {
  return getSession()?.role === 'ADMIN'
}
