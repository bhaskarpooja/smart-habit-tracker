import React, { useEffect, useState } from 'react'
import { createHabit, getHabits, deleteHabit, logHabit, getHabitLogs } from '../services/habitService'
import { format, startOfMonth, endOfMonth, eachDayOfInterval, isSameDay, isToday } from 'date-fns'

const categories = ['Health', 'Fitness', 'Study', 'Sleep', 'Work', 'Personal', 'Other']

const Habits = () => {
  const [habits, setHabits] = useState([])
  const [showModal, setShowModal] = useState(false)
  const [selectedHabit, setSelectedHabit] = useState(null)
  const [habitLogs, setHabitLogs] = useState({})
  const [newHabit, setNewHabit] = useState({ title: '', category: 'Health' })
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadHabits()
  }, [])

  const loadHabits = async () => {
    try {
      const data = await getHabits()
      setHabits(data)
      
      // Load logs for each habit
      const logsMap = {}
      for (const habit of data) {
        try {
          const logs = await getHabitLogs(habit.id)
          logsMap[habit.id] = logs
        } catch (error) {
          logsMap[habit.id] = []
        }
      }
      setHabitLogs(logsMap)
    } catch (error) {
      console.error('Error loading habits:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleCreateHabit = async (e) => {
    e.preventDefault()
    try {
      await createHabit(newHabit.title, newHabit.category)
      setNewHabit({ title: '', category: 'Health' })
      setShowModal(false)
      loadHabits()
    } catch (error) {
      console.error('Error creating habit:', error)
    }
  }

  const handleDeleteHabit = async (id) => {
    if (window.confirm('Are you sure you want to delete this habit?')) {
      try {
        await deleteHabit(id)
        loadHabits()
      } catch (error) {
        console.error('Error deleting habit:', error)
      }
    }
  }

  const handleToggleLog = async (habitId, date) => {
    const logs = habitLogs[habitId] || []
    const existingLog = logs.find(log => isSameDay(new Date(log.date), date))
    const newStatus = existingLog ? !existingLog.status : true

    try {
      await logHabit(habitId, format(date, 'yyyy-MM-dd'), newStatus)
      loadHabits()
    } catch (error) {
      console.error('Error logging habit:', error)
    }
  }

  const getLogStatus = (habitId, date) => {
    const logs = habitLogs[habitId] || []
    const log = logs.find(l => isSameDay(new Date(l.date), date))
    return log ? log.status : null
  }

  const getMonthDays = () => {
    const today = new Date()
    const start = startOfMonth(today)
    const end = endOfMonth(today)
    return eachDayOfInterval({ start, end })
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
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-4xl font-bold text-gray-800 mb-2">My Habits</h1>
          <p className="text-gray-600">Track your daily habits and build consistency</p>
        </div>
        <button onClick={() => setShowModal(true)} className="btn-primary">
          + New Habit
        </button>
      </div>

      {habits.length === 0 ? (
        <div className="card text-center py-12">
          <p className="text-gray-500 mb-4 text-lg">No habits yet. Create your first one!</p>
          <button onClick={() => setShowModal(true)} className="btn-primary">
            Create Habit
          </button>
        </div>
      ) : (
        <div className="space-y-6">
          {habits.map((habit) => (
            <div key={habit.id} className="card animate-slide-up">
              <div className="flex items-start justify-between mb-4">
                <div>
                  <div className="flex items-center space-x-3 mb-2">
                    <h2 className="text-2xl font-bold text-gray-800">{habit.title}</h2>
                    <span className="text-sm bg-indigo-100 text-indigo-700 px-3 py-1 rounded-full">
                      {habit.category}
                    </span>
                  </div>
                  <div className="flex items-center space-x-4 text-sm text-gray-600">
                    <span>🔥 {habit.currentStreak} day streak</span>
                    <span>✨ {habit.consistencyPercentage?.toFixed(0) || 0}% consistent</span>
                  </div>
                </div>
                <button
                  onClick={() => handleDeleteHabit(habit.id)}
                  className="text-red-500 hover:text-red-700 px-3 py-1 rounded-lg hover:bg-red-50 transition-all"
                >
                  Delete
                </button>
              </div>

              {/* Calendar Heatmap */}
              <div className="mt-6">
                <h3 className="text-sm font-semibold text-gray-700 mb-3">
                  {format(new Date(), 'MMMM yyyy')}
                </h3>
                <div className="grid grid-cols-7 gap-2">
                  {getMonthDays().map((day, index) => {
                    const status = getLogStatus(habit.id, day)
                    const isCurrentDay = isToday(day)
                    return (
                      <button
                        key={index}
                        onClick={() => handleToggleLog(habit.id, day)}
                        className={`
                          aspect-square rounded-lg transition-all hover:scale-110
                          ${status === true 
                            ? 'bg-green-500 hover:bg-green-600' 
                            : status === false 
                            ? 'bg-red-300 hover:bg-red-400' 
                            : 'bg-gray-200 hover:bg-gray-300'
                          }
                          ${isCurrentDay ? 'ring-2 ring-indigo-500 ring-offset-2' : ''}
                        `}
                        title={format(day, 'MMM dd, yyyy')}
                      >
                        <span className="text-xs text-gray-600">{format(day, 'd')}</span>
                      </button>
                    )
                  })}
                </div>
                <div className="flex items-center justify-end space-x-4 mt-4 text-xs text-gray-600">
                  <div className="flex items-center space-x-1">
                    <div className="w-4 h-4 bg-green-500 rounded"></div>
                    <span>Completed</span>
                  </div>
                  <div className="flex items-center space-x-1">
                    <div className="w-4 h-4 bg-red-300 rounded"></div>
                    <span>Missed</span>
                  </div>
                  <div className="flex items-center space-x-1">
                    <div className="w-4 h-4 bg-gray-200 rounded"></div>
                    <span>Not logged</span>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Create Habit Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="card max-w-md w-full animate-slide-up">
            <h2 className="text-2xl font-bold text-gray-800 mb-6">Create New Habit</h2>
            <form onSubmit={handleCreateHabit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Habit Title
                </label>
                <input
                  type="text"
                  value={newHabit.title}
                  onChange={(e) => setNewHabit({ ...newHabit, title: e.target.value })}
                  required
                  className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                  placeholder="e.g., Morning Meditation"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Category
                </label>
                <select
                  value={newHabit.category}
                  onChange={(e) => setNewHabit({ ...newHabit, category: e.target.value })}
                  className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                >
                  {categories.map(cat => (
                    <option key={cat} value={cat}>{cat}</option>
                  ))}
                </select>
              </div>
              <div className="flex space-x-4 pt-4">
                <button type="submit" className="btn-primary flex-1">
                  Create
                </button>
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="btn-secondary flex-1"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

export default Habits

