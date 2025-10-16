import { useEffect, useState } from 'react'
import { getMyProfile, User } from '../lib/api'
import { useAuth } from '../state/AuthContext'

export default function ProfilePage() {
  const { token } = useAuth()
  const [user, setUser] = useState<User | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    async function load() {
      if (!token) return
      const res = await getMyProfile(token)
      if (res.statusCode === 200 && res.ourUsers) setUser(res.ourUsers)
      else setError(res.message || 'Failed to load')
    }
    load()
  }, [token])

  if (!token) return null
  return (
    <div className="card">
      <h2>My Profile</h2>
      {error && <div className="error">{error}</div>}
      {user && (
        <ul>
          <li><strong>Name:</strong> {user.name}</li>
          <li><strong>Email:</strong> {user.email}</li>
          <li><strong>City:</strong> {user.city}</li>
          <li><strong>Organization:</strong> {user.organization || '-'}</li>
          <li><strong>Role:</strong> {user.role}</li>
        </ul>
      )}
    </div>
  )
}
