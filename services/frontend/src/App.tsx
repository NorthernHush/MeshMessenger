import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/auth/Login'
import Register from './pages/auth/Register'
import Shell from './pages/app/Shell'
import { useAuthStore } from './stores/auth'

export default function App(){
  const { token } = useAuthStore()
  return (
    <Routes>
      <Route path="/auth/login" element={<Login/>} />
      <Route path="/auth/register" element={<Register/>} />
      <Route path="/app/*" element={token ? <Shell/> : <Navigate to="/auth/login" replace/>} />
      <Route path="/" element={<Navigate to={token?'/app':'/auth/login'} replace/>} />
    </Routes>
  )
}
