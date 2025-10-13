import { FormEvent, useState } from 'react'
import { register } from '../lib/api'
import { Link, useNavigate } from 'react-router-dom'

export default function RegisterPage() {
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [city, setCity] = useState('')
  const [role, setRole] = useState<'ADMIN' | 'USER'>('USER')
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError(null)
    const res = await register({ name, email, password, city, role })
    if (res.statusCode === 200) {
      navigate('/login')
    } else {
      setError(res.message || 'Registration failed')
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
        <label>Role
          <select value={role} onChange={e => setRole(e.target.value as 'ADMIN' | 'USER')}>
            <option value="USER">USER</option>
            <option value="ADMIN">ADMIN</option>
          </select>
        </label>
        {error && <div className="error">{error}</div>}
        <button className="btn" type="submit">Create account</button>
      </form>
      <p>Have an account? <Link to="/login">Login</Link></p>
    </div>
  )
}
