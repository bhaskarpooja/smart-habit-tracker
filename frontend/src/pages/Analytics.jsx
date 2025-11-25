import React, { useEffect, useState } from 'react'
import { getAnalytics } from '../services/analyticsService'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js'
import { Line, Bar, Doughnut } from 'react-chartjs-2'

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  ArcElement,
  Title,
  Tooltip,
  Legend
)

const Analytics = () => {
  const [analytics, setAnalytics] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadAnalytics()
  }, [])

  const loadAnalytics = async () => {
    try {
      const data = await getAnalytics()
      setAnalytics(data)
    } catch (error) {
      console.error('Error loading analytics:', error)
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-600"></div>
      </div>
    )
  }

  if (!analytics) {
    return (
      <div className="card text-center py-12">
        <p className="text-gray-500">No analytics data available yet.</p>
      </div>
    )
  }

  const weeklyData = {
    labels: analytics.weeklyTrend?.map(w => w.week) || [],
    datasets: [
      {
        label: 'Consistency %',
        data: analytics.weeklyTrend?.map(w => w.consistency) || [],
        borderColor: 'rgb(99, 102, 241)',
        backgroundColor: 'rgba(99, 102, 241, 0.1)',
        tension: 0.4,
      },
    ],
  }

  const categoryData = {
    labels: Object.keys(analytics.categoryConsistency || {}),
    datasets: [
      {
        label: 'Consistency %',
        data: Object.values(analytics.categoryConsistency || {}),
        backgroundColor: [
          'rgba(99, 102, 241, 0.8)',
          'rgba(139, 92, 246, 0.8)',
          'rgba(236, 72, 153, 0.8)',
          'rgba(251, 146, 60, 0.8)',
          'rgba(34, 197, 94, 0.8)',
        ],
      },
    ],
  }

  const streakData = {
    labels: Object.keys(analytics.categoryStreaks || {}),
    datasets: [
      {
        label: 'Longest Streak',
        data: Object.values(analytics.categoryStreaks || {}),
        backgroundColor: 'rgba(99, 102, 241, 0.8)',
      },
    ],
  }

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
      },
    },
  }

  return (
    <div className="space-y-8 animate-fade-in">
      <div>
        <h1 className="text-4xl font-bold text-gray-800 mb-2">Analytics</h1>
        <p className="text-gray-600">Deep insights into your habit tracking journey</p>
      </div>

      {/* Overview Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="card animate-slide-up">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600 mb-1">Overall Consistency</p>
              <p className="text-3xl font-bold text-indigo-600">
                {analytics.overallConsistency?.toFixed(1) || 0}%
              </p>
            </div>
            <div className="text-4xl">📊</div>
          </div>
        </div>

        <div className="card animate-slide-up" style={{ animationDelay: '0.1s' }}>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600 mb-1">Longest Streak</p>
              <p className="text-3xl font-bold text-purple-600">{analytics.totalStreak || 0} days</p>
            </div>
            <div className="text-4xl">🔥</div>
          </div>
        </div>

        <div className="card animate-slide-up" style={{ animationDelay: '0.2s' }}>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600 mb-1">Categories Tracked</p>
              <p className="text-3xl font-bold text-pink-600">
                {Object.keys(analytics.categoryConsistency || {}).length}
              </p>
            </div>
            <div className="text-4xl">✨</div>
          </div>
        </div>
      </div>

      {/* Weekly Trend Chart */}
      <div className="card animate-slide-up">
        <h2 className="text-2xl font-bold text-gray-800 mb-6">Weekly Consistency Trend</h2>
        <div className="h-64">
          <Line data={weeklyData} options={chartOptions} />
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Category Consistency */}
        <div className="card animate-slide-up">
          <h2 className="text-2xl font-bold text-gray-800 mb-6">Category Performance</h2>
          <div className="h-64">
            <Doughnut data={categoryData} options={chartOptions} />
          </div>
        </div>

        {/* Category Streaks */}
        <div className="card animate-slide-up">
          <h2 className="text-2xl font-bold text-gray-800 mb-6">Category Streaks</h2>
          <div className="h-64">
            <Bar data={streakData} options={chartOptions} />
          </div>
        </div>
      </div>

      {/* Category Details */}
      {Object.keys(analytics.categoryConsistency || {}).length > 0 && (
        <div className="card animate-slide-up">
          <h2 className="text-2xl font-bold text-gray-800 mb-6">Category Breakdown</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {Object.entries(analytics.categoryConsistency || {}).map(([category, consistency]) => (
              <div
                key={category}
                className="bg-white/50 rounded-xl p-4 border border-gray-200 hover:shadow-lg transition-all"
              >
                <h3 className="font-semibold text-gray-800 mb-2">{category}</h3>
                <div className="flex items-center justify-between">
                  <span className="text-2xl font-bold text-indigo-600">
                    {consistency.toFixed(1)}%
                  </span>
                  <span className="text-sm text-gray-600">
                    🔥 {analytics.categoryStreaks[category] || 0} days
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

export default Analytics

