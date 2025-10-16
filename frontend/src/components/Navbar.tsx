import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../state/AuthContext'

export default function Navbar() {
  const { token, role, logout } = useAuth()
  const navigate = useNavigate()
  const onLogout = () => {
    logout()
    navigate('/login')
  }
  return (
    <nav className="nav">
      <div className="brand">SecureManagement</div>
      <div className="links">
        {!token && (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}
        {token && (
          <>
            <Link to="/profile">My Profile</Link>
            <Link to="/admin/business">Business</Link>
            <button onClick={onLogout} className="btn">Logout</button>
          </>
        )}
      </div>
    </nav>
  )
}
