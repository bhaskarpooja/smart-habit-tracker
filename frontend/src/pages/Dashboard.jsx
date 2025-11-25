import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getHabits } from '../services/habitService'
import { getLatestReport } from '../services/aiReportService'
import { getAnalytics } from '../services/analyticsService'
import { format } from 'date-fns'

const Dashboard = () => {
  const [habits, setHabits] = useState([])
  const [latestReport, setLatestReport] = useState(null)
  const [analytics, setAnalytics] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      const [habitsData, reportData, analyticsData] = await Promise.all([
        getHabits(),
        getLatestReport().catch(() => null),
        getAnalytics()
      ])
      setHabits(habitsData)
      setLatestReport(reportData)
      setAnalytics(analyticsData)
    } catch (error) {
      console.error('Error loading dashboard data:', error)
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

  return (
    <div className="space-y-8 animate-fade-in">
      <div>
        <h1 className="text-4xl font-bold text-gray-800 mb-2">Dashboard</h1>
        <p className="text-gray-600">Track your progress and stay motivated</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="card animate-slide-up">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600 mb-1">Total Habits</p>
              <p className="text-3xl font-bold text-indigo-600">{habits.length}</p>
            </div>
            <div className="text-4xl">📊</div>
          </div>
        </div>

        <div className="card animate-slide-up" style={{ animationDelay: '0.1s' }}>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600 mb-1">Current Streak</p>
              <p className="text-3xl font-bold text-purple-600">{analytics?.totalStreak || 0}</p>
            </div>
            <div className="text-4xl">🔥</div>
          </div>
        </div>

        <div className="card animate-slide-up" style={{ animationDelay: '0.2s' }}>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600 mb-1">Consistency</p>
              <p className="text-3xl font-bold text-pink-600">
                {analytics?.overallConsistency?.toFixed(1) || 0}%
              </p>
            </div>
            <div className="text-4xl">✨</div>
          </div>
        </div>
      </div>

      {/* Latest AI Report */}
      {latestReport && (
        <div className="card bg-gradient-to-r from-indigo-50 to-purple-50 border-indigo-200 animate-slide-up">
          <div className="flex items-start justify-between mb-4">
            <div>
              <h2 className="text-2xl font-bold text-gray-800 mb-1">🤖 Latest AI Coaching Report</h2>
              <p className="text-sm text-gray-600">
                {format(new Date(latestReport.startDate), 'MMM dd')} - {format(new Date(latestReport.endDate), 'MMM dd, yyyy')}
              </p>
            </div>
            <Link to="/ai-reports" className="text-indigo-600 hover:text-indigo-700 font-semibold">
              View All →
            </Link>
          </div>
          <div className="bg-white/70 rounded-lg p-4 border border-white/50">
            <p className="text-gray-700 whitespace-pre-line leading-relaxed">
              {latestReport.feedbackText}
            </p>
          </div>
        </div>
      )}

      {/* Recent Habits */}
      <div className="card animate-slide-up">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-bold text-gray-800">Your Habits</h2>
          <Link to="/habits" className="btn-primary text-sm">
            Manage Habits
          </Link>
        </div>
        {habits.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-500 mb-4">No habits yet. Start tracking your progress!</p>
            <Link to="/habits" className="btn-primary inline-block">
              Create Your First Habit
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {habits.slice(0, 6).map((habit) => (
              <div
                key={habit.id}
                className="bg-white/50 rounded-xl p-4 border border-gray-200 hover:shadow-lg transition-all"
              >
                <div className="flex items-start justify-between mb-2">
                  <h3 className="font-semibold text-gray-800">{habit.title}</h3>
                  <span className="text-xs bg-indigo-100 text-indigo-700 px-2 py-1 rounded-full">
                    {habit.category}
                  </span>
                </div>
                <div className="flex items-center justify-between text-sm text-gray-600">
                  <span>🔥 {habit.currentStreak} day streak</span>
                  <span>{habit.consistencyPercentage?.toFixed(0) || 0}%</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Link to="/analytics" className="card hover:scale-105 transition-transform">
          <div className="flex items-center space-x-4">
            <div className="text-4xl">📈</div>
            <div>
              <h3 className="text-xl font-bold text-gray-800">View Analytics</h3>
              <p className="text-gray-600">See detailed insights and trends</p>
            </div>
          </div>
        </Link>

        <Link to="/ai-reports" className="card hover:scale-105 transition-transform">
          <div className="flex items-center space-x-4">
            <div className="text-4xl">🤖</div>
            <div>
              <h3 className="text-xl font-bold text-gray-800">AI Reports</h3>
              <p className="text-gray-600">Get personalized coaching insights</p>
            </div>
          </div>
        </Link>
      </div>
    </div>
  )
}

export default Dashboard

