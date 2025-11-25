import React from 'react'
import { Link, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const Layout = ({ children }) => {
  const { user, logout } = useAuth()
  const location = useLocation()

  const isActive = (path) => location.pathname === path

  return (
    <div className="min-h-screen">
      <nav className="glass border-b border-white/20 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16 items-center">
            <div className="flex items-center space-x-8">
              <Link to="/" className="text-2xl font-bold bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent">
                🎯 Smart Habit Tracker
              </Link>
              <div className="hidden md:flex space-x-4">
                <Link
                  to="/"
                  className={`px-4 py-2 rounded-lg transition-all ${
                    isActive('/') ? 'bg-indigo-600 text-white' : 'text-gray-700 hover:bg-white/50'
                  }`}
                >
                  Dashboard
                </Link>
                <Link
                  to="/habits"
                  className={`px-4 py-2 rounded-lg transition-all ${
                    isActive('/habits') ? 'bg-indigo-600 text-white' : 'text-gray-700 hover:bg-white/50'
                  }`}
                >
                  Habits
                </Link>
                <Link
                  to="/analytics"
                  className={`px-4 py-2 rounded-lg transition-all ${
                    isActive('/analytics') ? 'bg-indigo-600 text-white' : 'text-gray-700 hover:bg-white/50'
                  }`}
                >
                  Analytics
                </Link>
                <Link
                  to="/ai-reports"
                  className={`px-4 py-2 rounded-lg transition-all ${
                    isActive('/ai-reports') ? 'bg-indigo-600 text-white' : 'text-gray-700 hover:bg-white/50'
                  }`}
                >
                  AI Reports
                </Link>
              </div>
            </div>
            <div className="flex items-center space-x-4">
              <span className="text-gray-700 font-medium">{user?.name}</span>
              <button
                onClick={logout}
                className="btn-secondary text-sm"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </nav>
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {children}
      </main>
    </div>
  )
}

export default Layout

