import { useEffect, useState } from 'react'
import { adminCreateUser, getAllUsers, Role, User } from '../lib/api'
import { useAuth } from '../state/AuthContext'

export default function AdminUsersPage() {
  const { token } = useAuth()
  const [users, setUsers] = useState<User[]>([])
  const [error, setError] = useState<string | null>(null)
  const [creating, setCreating] = useState(false)
  const [form, setForm] = useState<{name: string; email: string; password: string; city: string; organization: string; role: Role}>({ name: '', email: '', password: '', city: '', organization: '', role: 'USER' })

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
      <div className="card" style={{ marginBottom: 16 }}>
        <h3>Create User (Admin only)</h3>
        <div className="form" style={{ display: 'grid', gap: 8 }}>
          <label>Name<input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} placeholder="Name" /></label>
          <label>Email<input value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} placeholder="Email" type="email" /></label>
          <label>Password<input value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} placeholder="Password" type="password" /></label>
          <label>City<input value={form.city} onChange={e => setForm({ ...form, city: e.target.value })} placeholder="City" /></label>
          <label>Organization<input value={form.organization} onChange={e => setForm({ ...form, organization: e.target.value })} placeholder="Organization / Business" /></label>
          <label>Role
            <select value={form.role} onChange={e => setForm({ ...form, role: e.target.value as Role })}>
              <option value="USER">USER</option>
              <option value="DEVELOPER">DEVELOPER</option>
              <option value="MANAGER">MANAGER</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </label>
          <div>
            <button className="btn" disabled={creating} onClick={async () => {
              if (!token) return
              setCreating(true)
              try {
                const res = await adminCreateUser(token, { ...form, city: form.city || undefined, organization: form.organization || undefined })
                if (res.statusCode === 200) {
                  const reload = await getAllUsers(token)
                  if (reload.statusCode === 200 && reload.ourUsersList) setUsers(reload.ourUsersList)
                  setForm({ name: '', email: '', password: '', city: '', organization: '', role: 'USER' })
                } else {
                  setError(res.message || 'Create failed')
                }
              } catch (e: any) {
                setError(e?.message || 'Network error')
              } finally {
                setCreating(false)
              }
            }}>{creating ? 'Creating…' : 'Create'}</button>
          </div>
        </div>
      </div>
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
