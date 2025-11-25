import React, { createContext, useState, useContext, useEffect } from 'react'
import { login as loginService, register as registerService } from '../services/authService'

const AuthContext = createContext()

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return context
}

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('token')
    const userData = localStorage.getItem('user')
    if (token && userData) {
      setUser(JSON.parse(userData))
    }
    setLoading(false)
  }, [])

  const login = async (email, password) => {
    try {
      const response = await loginService(email, password)
      localStorage.setItem('token', response.token)
      const userData = { email: response.email, name: response.name, userId: response.userId }
      localStorage.setItem('user', JSON.stringify(userData))
      setUser(userData)
      return response
    } catch (error) {
      throw error
    }
  }

  const register = async (name, email, password) => {
    try {
      const response = await registerService(name, email, password)
      localStorage.setItem('token', response.token)
      const userData = { email: response.email, name: response.name, userId: response.userId }
      localStorage.setItem('user', JSON.stringify(userData))
      setUser(userData)
      return response
    } catch (error) {
      throw error
    }
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, login, register, logout, loading }}>
      {children}
    </AuthContext.Provider>
  )
}

