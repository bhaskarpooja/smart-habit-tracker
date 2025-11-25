import api from './api'

export const getAIReports = async () => {
  const response = await api.get('/ai-reports')
  return response.data
}

export const getLatestReport = async () => {
  const response = await api.get('/ai-reports/latest')
  return response.data
}

