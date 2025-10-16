import React from 'react'
import { createBrowserRouter, Navigate } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import ProfilePage from './pages/ProfilePage'
import AdminUsersPage from './pages/AdminUsersPage'
import ManagerUsersPage from './pages/ManagerUsersPage'
import DeveloperUsersPage from './pages/DeveloperUsersPage'
import AdminBusinessPage from './pages/AdminBusinessPage'
import Navbar from './components/Navbar'
import { useAuth } from './state/AuthContext'
import { Outlet } from 'react-router-dom'

function Layout() {
  return (
    <div className="app">
      <Navbar />
      <div className="container">
        <Outlet />
      </div>
    </div>
  )
}

function PrivateRoute({ children }: { children: JSX.Element }) {
  const { token } = useAuth()
  return token ? children : <Navigate to="/login" replace />
}

function AdminRoute({ children }: { children: JSX.Element }) {
  const { token, role } = useAuth()
  return token && role === 'ADMIN' ? children : <Navigate to="/" replace />
}

function ManagerRoute({ children }: { children: JSX.Element }) {
  const { token, role } = useAuth()
  return token && (role === 'MANAGER' || role === 'ADMIN') ? children : <Navigate to="/" replace />
}

function DeveloperRoute({ children }: { children: JSX.Element }) {
  const { token, role } = useAuth()
  return token && (role === 'DEVELOPER' || role === 'ADMIN') ? children : <Navigate to="/" replace />
}

export const router = createBrowserRouter([
  {
    path: '/',
    element: <Layout />,
    children: [
      { index: true, element: <Navigate to="/profile" replace /> },
      { path: 'login', element: <LoginPage /> },
      { path: 'register', element: <RegisterPage /> },
      {
        path: 'profile',
        element: (
          <PrivateRoute>
            <ProfilePage />
          </PrivateRoute>
        ),
      },
      {
        path: 'admin/users',
        element: (
          <AdminRoute>
            <AdminUsersPage />
          </AdminRoute>
        ),
      },
      {
        path: 'admin/business',
        element: (
          <AdminRoute>
            <AdminBusinessPage />
          </AdminRoute>
        ),
      },
      {
        path: 'manager/users',
        element: (
          <ManagerRoute>
            <ManagerUsersPage />
          </ManagerRoute>
        ),
      },
      {
        path: 'developer/users',
        element: (
          <DeveloperRoute>
            <DeveloperUsersPage />
          </DeveloperRoute>
        ),
      },
      { path: '*', element: <Navigate to="/" replace /> },
    ],
  },
], {
  // Cast to any to allow future flags on versions where types lag behind
  future: {
    v7_startTransition: true as any,
    v7_relativeSplatPath: true as any,
  } as any,
})
