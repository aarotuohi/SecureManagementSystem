import React, { createContext, useContext, useEffect, useMemo, useState } from 'react'
import type { Role, User } from '../lib/api'

type AuthState = {
  token: string | null
  role: Role | null
  user: User | null
  setToken: (v: string | null) => void
  setRole: (r: Role | null) => void
  setUser: (u: User | null) => void
  logout: () => void
}

const Ctx = createContext<AuthState | undefined>(undefined)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('token'))
  const [role, setRole] = useState<Role | null>(() => (localStorage.getItem('role') as Role | null))
  const [user, setUser] = useState<User | null>(() => {
    try { const raw = localStorage.getItem('user'); return raw ? JSON.parse(raw) as User : null } catch { return null }
  })

  useEffect(() => {
    if (token) localStorage.setItem('token', token)
    else localStorage.removeItem('token')
  }, [token])

  useEffect(() => {
    if (role) localStorage.setItem('role', role)
    else localStorage.removeItem('role')
  }, [role])

  useEffect(() => {
    if (user) localStorage.setItem('user', JSON.stringify(user))
    else localStorage.removeItem('user')
  }, [user])

  const logout = () => {
    setToken(null)
    setRole(null)
    setUser(null)
  }

  const value = useMemo(() => ({ token, role, user, setToken, setRole, setUser, logout }), [token, role, user])
  return <Ctx.Provider value={value}>{children}</Ctx.Provider>
}

export function useAuth() {
  const ctx = useContext(Ctx)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
