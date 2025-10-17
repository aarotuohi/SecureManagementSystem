import { FormEvent, useState } from 'react'
import { register } from '../lib/api'
import { useAuth } from '../state/AuthContext'
import { Link, useNavigate } from 'react-router-dom'

export default function RegisterPage() {
  const { setUser } = useAuth()
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [city, setCity] = useState('')
  const [organization, setOrganization] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault()
    if (loading) return
    setError(null)
    setLoading(true)
    try {
  const res = await register({ name, email, password, city, organization: organization || undefined })
      if (res.statusCode === 200) {
        
        try {
          if ((res as any).ourUsers) setUser((res as any).ourUsers)
        } catch {}
        navigate('/login')
      } else {
        setError(res.message || 'Registration failed')
      }
    } catch (err: any) {
      setError(err?.message || 'Network error. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="card">
      <h2>Register</h2>
      <form onSubmit={onSubmit} className="form">
  <label>Name<input value={name} placeholder="Enter your full name" onChange={e => setName(e.target.value)} required /></label>
  <label>Email<input value={email} placeholder="Enter your email" onChange={e => setEmail(e.target.value)} type="email" required /></label>
  <label>Password<input value={password} placeholder="Create a password" onChange={e => setPassword(e.target.value)} type="password" required /></label>
  <label>City<input value={city} placeholder="Enter your city" onChange={e => setCity(e.target.value)} /></label>
  <label>Organization / Business<input value={organization} placeholder="Enter organization or business" onChange={e => setOrganization(e.target.value)} /></label>
        
        {error && <div className="error">{error}</div>}
        <button className="btn" type="submit" disabled={loading}>{loading ? 'Creating…' : 'Create account'}</button>
      </form>
      <p>Have an account? <Link to="/login">Login</Link></p>
    </div>
  )
}
