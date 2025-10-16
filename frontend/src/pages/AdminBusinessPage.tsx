import { useEffect, useState } from 'react'
import { adminCreateUser, createBusiness, getMyBusiness } from '../lib/api'
import { useAuth } from '../state/AuthContext'
import { useNavigate } from 'react-router-dom'

type Role = 'ADMIN' | 'USER' | 'MANAGER' | 'DEVELOPER' | 'TEAM_LEADER'

export default function AdminBusinessPage() {
  const { token } = useAuth()
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [businesses, setBusinesses] = useState<string[]>([])
  const [selectedBusiness, setSelectedBusiness] = useState<string | null>(null)
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [creating, setCreating] = useState(false)
  const [users, setUsers] = useState<any[]>([])
  const [userError, setUserError] = useState<string | null>(null)
  const [creatingUser, setCreatingUser] = useState(false)
  const [userForm, setUserForm] = useState<{name: string; email: string; city: string; organization: string; role: Role}>({ name: '', email: '', city: '', organization: '', role: 'USER' })
  const navigate = useNavigate()

  useEffect(() => {
    async function load() {
      if (!token) return
      setLoading(true)
      setError(null)
      try {
        const res = await getMyBusiness(token)
        const list: string[] = []
        if (res.statusCode === 200 && res.message) list.push(res.message)
        setBusinesses(list)
        setSelectedBusiness(list[0] || null)
      } catch (e: any) {
        setError(e?.message || 'Failed to load business')
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [token])

  return (
    <div className="card">
      <h2>Business</h2>
      {loading && <div>Loading…</div>}
      {error && <div className="error">{error}</div>}

      {!loading && businesses.length > 0 && (
        <>
          <div style={{ marginBottom: 12 }}>
            <label>
              Select business
              <select value={selectedBusiness ?? ''} onChange={e => setSelectedBusiness(e.target.value || null)} style={{ marginLeft: 8 }}>
                {businesses.map(b => <option key={b} value={b}>{b}</option>)}
              </select>
            </label>
          </div>
          {selectedBusiness && (
            <div className="card">
              <h3>{selectedBusiness}</h3>
              <p>Users in this business:</p>
              {userError && <div className="error">{userError}</div>}
              <UsersList token={token!} businessName={selectedBusiness} users={users} setUsers={setUsers} setUserError={setUserError} />
              <div className="card" style={{ marginTop: 16 }}>
                <h4>Add user to this business</h4>
                <div className="form" style={{ display: 'grid', gap: 8 }}>
                  <label>Name<input value={userForm.name} onChange={e => setUserForm({ ...userForm, name: e.target.value })} placeholder="Name" /></label>
                  <label>Email<input value={userForm.email} onChange={e => setUserForm({ ...userForm, email: e.target.value })} placeholder="Email" type="email" /></label>
                  <label>City<input value={userForm.city} onChange={e => setUserForm({ ...userForm, city: e.target.value })} placeholder="City" /></label>
                  <label>Organization<input value={userForm.organization} onChange={e => setUserForm({ ...userForm, organization: e.target.value })} placeholder="Organization / Business" /></label>
                  <label>Role
                    <select value={userForm.role} onChange={e => setUserForm({ ...userForm, role: e.target.value as Role })}>
                      <option value="USER">USER</option>
                      <option value="DEVELOPER">DEVELOPER</option>
                      <option value="MANAGER">MANAGER</option>
                      <option value="ADMIN">ADMIN</option>
                    </select>
                  </label>
                  <div>
                    <button className="btn" disabled={creatingUser} onClick={async () => {
                      if (!token) return
                      setCreatingUser(true)
                      setUserError(null)
                      try {
                        const res = await adminCreateUser(token, { ...userForm, city: userForm.city || undefined, organization: userForm.organization || undefined } as any)
                        if (res.statusCode === 200 && res.ourUsers) {
                          // reload list so user appears in correct category and reflects server-side scoping
                          try {
                            const reload = await fetch(`/business/my/users`, { headers: { 'Authorization': `Bearer ${token}`, 'Accept': 'application/json' } })
                            if (reload.ok) {
                              const data = await reload.json()
                              setUsers(Array.isArray(data.ourUsersList) ? data.ourUsersList : [])
                            }
                          } catch {}
                          setUserForm({ name: '', email: '', city: '', organization: '', role: 'USER' })
                        } else {
                          setUserError(res.message || 'Create failed')
                        }
                      } catch (e: any) {
                        setUserError(e?.message || 'Network error')
                      } finally {
                        setCreatingUser(false)
                      }
                    }}>{creatingUser ? 'Creating…' : 'Add user'}</button>
                  </div>
                </div>
              </div>
            </div>
          )}
        </>
      )}

      {!loading && businesses.length === 0 && (
        <div className="card" style={{ marginTop: 16 }}>
          <h3>Create a business</h3>
          <div className="form" style={{ display: 'grid', gap: 8 }}>
            <label>Business name<input value={name} onChange={e => setName(e.target.value)} placeholder="Business name" /></label>
            <label>Description<textarea value={description} onChange={e => setDescription(e.target.value)} placeholder="Optional" /></label>
            <div>
              <button className="btn" disabled={creating || !name.trim()} onClick={async () => {
                if (!token) return
                setCreating(true)
                setError(null)
                try {
                  const res = await createBusiness(token, name.trim(), description.trim() || undefined)
                  if (res.statusCode === 200) {
                    try {
                      const r = await getMyBusiness(token)
                      const list: string[] = []
                      if (r.statusCode === 200 && r.message) list.push(r.message)
                      setBusinesses(list)
                      setSelectedBusiness(list[0] || null)
                    } catch {
                      setBusinesses([name.trim()])
                      setSelectedBusiness(name.trim())
                    }
                    setName('')
                    setDescription('')
                  } else {
                    setError(res.message || 'Create failed')
                  }
                } catch (e: any) {
                  const msg = e?.message || 'Network error'
                  setError(msg.includes('403') ? 'Forbidden: Only admins can create a business.' : msg)
                } finally {
                  setCreating(false)
                }
              }}>{creating ? 'Creating…' : 'Create business'}</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

function UsersList({ token, businessName, users, setUsers, setUserError }: { token: string; businessName: string; users: any[]; setUsers: (f: any) => void; setUserError: (s: string | null) => void }) {
  // Since backend doesn't expose list by name directly, we rely on business context: current user's business
  // and reuse /admin/get-all-users which is scoped server-side.
  useEffect(() => {
    let cancelled = false
    async function load() {
      try {
        const res = await fetch(`/business/my/users`, { headers: { 'Authorization': `Bearer ${token}`, 'Accept': 'application/json' } })
        if (!res.ok) throw new Error(`${res.status}`)
        const data = await res.json()
        if (!cancelled) setUsers(Array.isArray(data.ourUsersList) ? data.ourUsersList : [])
      } catch (e: any) {
        if (!cancelled) setUserError(e?.message || 'Failed to load users')
      }
    }
    load()
    return () => { cancelled = true }
  }, [token, businessName, setUsers, setUserError])

  if (!users.length) return <div>No users yet.</div>
  return (
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
        {users.map((u: any) => (
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
  )
}
