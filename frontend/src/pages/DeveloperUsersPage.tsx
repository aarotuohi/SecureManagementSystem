import { useEffect, useState } from 'react'
import { getAllDeveloperUsers, updateDeveloperUser, User } from '../lib/api'
import { useAuth } from '../state/AuthContext'

export default function DeveloperUsersPage() {
  const { token } = useAuth()
  const [users, setUsers] = useState<User[]>([])
  const [error, setError] = useState<string | null>(null)
  const [editing, setEditing] = useState<Record<number, Partial<User> & { password?: string }>>({})

  useEffect(() => {
    async function load() {
      if (!token) return
      const res = await getAllDeveloperUsers(token)
      if (res.statusCode === 200 && res.ourUsersList) setUsers(res.ourUsersList)
      else setError(res.message || 'Failed to load developer users')
    }
    load()
  }, [token])

  const startEdit = (u: User) => setEditing(prev => ({ ...prev, [u.id]: { name: u.name, email: u.email, city: u.city, role: u.role } }))
  const cancelEdit = (id: number) => setEditing(prev => { const n = { ...prev }; delete n[id]; return n })
  const setField = (id: number, field: string, value: any) => setEditing(prev => ({ ...prev, [id]: { ...prev[id], [field]: value } }))

  const save = async (id: number) => {
    if (!token) return
    const body = editing[id]
    const res = await updateDeveloperUser(token, id, body || {})
    if (res.statusCode === 200 && res.ourUsers) {
      setUsers(us => us.map(u => (u.id === id ? res.ourUsers! : u)))
      cancelEdit(id)
    } else {
      setError(res.message || 'Update failed')
    }
  }

  return (
    <div className="card">
      <h2>Developer Users</h2>
      {error && <div className="error">{error}</div>}
      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>City</th>
            <th>Role</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map(u => {
            const e = editing[u.id]
            return (
              <tr key={u.id}>
                <td>{u.id}</td>
                <td>{e ? <input value={e.name || ''} onChange={ev => setField(u.id, 'name', ev.target.value)} /> : u.name}</td>
                <td>{e ? <input value={e.email || ''} onChange={ev => setField(u.id, 'email', ev.target.value)} /> : u.email}</td>
                <td>{e ? <input value={e.city || ''} onChange={ev => setField(u.id, 'city', ev.target.value)} /> : u.city}</td>
                <td>{e ? (
                  <select value={e.role || u.role} onChange={ev => setField(u.id, 'role', ev.target.value)}>
                    <option value="ADMIN">ADMIN</option>
                    <option value="MANAGER">MANAGER</option>
                    <option value="DEVELOPER">DEVELOPER</option>
                    <option value="USER">USER</option>
                  </select>
                ) : u.role}
                </td>
                <td>
                  {e ? (
                    <>
                      <button className="btn" onClick={() => save(u.id)}>Save</button>
                      <button className="btn" onClick={() => cancelEdit(u.id)}>Cancel</button>
                    </>
                  ) : (
                    <button className="btn" onClick={() => startEdit(u)}>Edit</button>
                  )}
                </td>
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}
