import { Navigate, Route, Routes } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import ProfilePage from './pages/ProfilePage'
import AdminUsersPage from './pages/AdminUsersPage'
import ManagerUsersPage from './pages/ManagerUsersPage'
import DeveloperUsersPage from './pages/DeveloperUsersPage'
import { useAuth } from './state/AuthContext'
import Navbar from './components/Navbar'

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

export default function App() {
  return (
    <div className="app">
      <Navbar />
      <div className="container">
        <Routes>
          <Route path="/" element={<Navigate to="/profile" replace />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route
            path="/profile"
            element={
              <PrivateRoute>
                <ProfilePage />
              </PrivateRoute>
            }
          />
          <Route
            path="/admin/users"
            element={
              <AdminRoute>
                <AdminUsersPage />
              </AdminRoute>
            }
          />
          <Route
            path="/manager/users"
            element={
              <ManagerRoute>
                <ManagerUsersPage />
              </ManagerRoute>
            }
          />
          <Route
            path="/developer/users"
            element={
              <DeveloperRoute>
                <DeveloperUsersPage />
              </DeveloperRoute>
            }
          />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </div>
  )
}
