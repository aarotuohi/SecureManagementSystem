import { FormEvent, useState } from 'react'
import { getMyProfile, login } from '../lib/api'
import { useAuth } from '../state/AuthContext'
import { useNavigate, Link } from 'react-router-dom'

export default function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const { setToken, setRole, setUser } = useAuth()
  const navigate = useNavigate()

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault()
    if (loading) return
    setError(null)
    setLoading(true)
    try {
      const res = await login({ email, password })
      if (res.statusCode === 200 && res.token) {
        setToken(res.token)
        if (res.role) setRole(res.role)
        try {
          const prof = await getMyProfile(res.token)
          if (prof.statusCode === 200 && prof.ourUsers) setUser(prof.ourUsers)
        } catch {}
        navigate('/profile')
      } else {
        setError(res.message || 'Login failed')
      }
    } catch (err: any) {
      setError(err?.message || 'Network error. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="card">
      <h2>Login</h2>
      <form onSubmit={onSubmit} className="form">
  <label>Email<input value={email} placeholder="Enter your email" onChange={e => setEmail(e.target.value)} type="email" required /></label>
  <label>Password<input value={password} placeholder="Enter your password" onChange={e => setPassword(e.target.value)} type="password" required /></label>
        {error && <div className="error">{error}</div>}
        <button className="btn" type="submit" disabled={loading}>{loading ? 'Logging in…' : 'Login'}</button>
      </form>
      <p>No account? <Link to="/register">Register</Link></p>
    </div>
  )
}
