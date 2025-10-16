import { useEffect, useState } from 'react'
import { getAllUsers, User } from '../lib/api'
import { useAuth } from '../state/AuthContext'

export default function AdminUsersPage() {
  const { token } = useAuth()
  const [users, setUsers] = useState<User[]>([])
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    async function load() {
      if (!token) return
      const res = await getAllUsers(token)
      if (res.statusCode === 200 && res.ourUsersList) setUsers(res.ourUsersList)
      else setError(res.message || 'Failed to load users')
    }
    load()
  }, [token])

  return (
    <div className="card">
      <h2>All Users</h2>
      {error && <div className="error">{error}</div>}
      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>City</th>
            <th>Organization</th>
            <th>Role</th>
          </tr>
        </thead>
        <tbody>
          {users.map(u => (
            <tr key={u.id}>
              <td>{u.id}</td>
              <td>{u.name}</td>
              <td>{u.email}</td>
              <td>{u.city}</td>
              <td>{u.organization || '-'}</td>
              <td>{u.role}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
