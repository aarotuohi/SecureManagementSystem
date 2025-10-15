import React, { createContext, useContext, useEffect, useMemo, useState } from 'react'
import type { Role } from '../lib/api'

type AuthState = {
  token: string | null
  role: Role | null
  setToken: (v: string | null) => void
  setRole: (r: Role | null) => void
  logout: () => void
}

const Ctx = createContext<AuthState | undefined>(undefined)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('token'))
  const [role, setRole] = useState<Role | null>(() => (localStorage.getItem('role') as Role | null))

  useEffect(() => {
    if (token) localStorage.setItem('token', token)
    else localStorage.removeItem('token')
  }, [token])

  useEffect(() => {
    if (role) localStorage.setItem('role', role)
    else localStorage.removeItem('role')
  }, [role])

  const logout = () => {
    setToken(null)
    setRole(null)
  }

  const value = useMemo(() => ({ token, role, setToken, setRole, logout }), [token, role])
  return <Ctx.Provider value={value}>{children}</Ctx.Provider>
}

export function useAuth() {
  const ctx = useContext(Ctx)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
