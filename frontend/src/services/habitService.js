import api from './api'

export const getHabits = async () => {
  const response = await api.get('/habits')
  return response.data
}

export const createHabit = async (title, category) => {
  const response = await api.post('/habits', { title, category })
  return response.data
}

export const updateHabit = async (id, title, category) => {
  const response = await api.put(`/habits/${id}`, { title, category })
  return response.data
}

export const deleteHabit = async (id) => {
  await api.delete(`/habits/${id}`)
}

export const logHabit = async (habitId, date, status) => {
  await api.post(`/habits/${habitId}/logs`, { date, status })
}

export const getHabitLogs = async (habitId) => {
  const response = await api.get(`/habits/${habitId}/logs`)
  return response.data
}

