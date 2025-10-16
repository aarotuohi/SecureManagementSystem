export type LoginRequest = { email: string; password: string }
export type RegisterRequest = { name: string; email: string; password: string; city: string; organization?: string; role: 'ADMIN' | 'USER' }

export type Role = 'ADMIN' | 'USER' | 'MANAGER' | 'DEVELOPER' | 'TEAM_LEADER'

export type AuthResponse = {
  statusCode: number
  token?: string
  refreshToken?: string
  role?: Role
  expirationTime?: string
  message?: string
  error?: string
}

export type User = {
  id: number
  email: string
  name: string
  city: string
  organization?: string
  role: Role
}

export type UsersResponse = {
  statusCode: number
  message?: string
  ourUsersList?: User[]
}

const API_BASE = (import.meta as any).env?.VITE_API_BASE_URL || ''

const jsonHeaders = (token?: string) => ({
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  ...(token ? { Authorization: `Bearer ${token}` } : {}),
})

async function fetchJson<T = any>(input: RequestInfo | URL, init?: RequestInit): Promise<T> {
  const res = await fetch(input, init)
  const ct = res.headers.get('content-type') || ''
  if (!res.ok) {
    if (ct.includes('application/json')) {
      try {
        const data: any = await res.json()
        const msg = data?.message || data?.error || ''
        throw new Error(msg ? `${res.status} ${res.statusText} - ${msg}` : `${res.status} ${res.statusText}`)
      } catch {
        throw new Error(`${res.status} ${res.statusText}`)
      }
    }
    const text = await res.text()
    throw new Error(`Unexpected response (${res.status} ${res.statusText}). ${text.slice(0, 200)}`)
  }
  if (ct.includes('application/json')) {
    return res.json() as Promise<T>
  }
  throw new Error(`Unexpected response (${res.status} ${res.statusText}).`)
}

export async function login(body: LoginRequest): Promise<AuthResponse> {
  return fetchJson<AuthResponse>(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: jsonHeaders(),
    body: JSON.stringify(body),
  })
}

export async function register(body: RegisterRequest): Promise<AuthResponse> {
  return fetchJson<AuthResponse>(`${API_BASE}/auth/register`, {
    method: 'POST',
    headers: jsonHeaders(),
    body: JSON.stringify(body),
  })
}

export async function getMyProfile(token: string) {
  return fetchJson<{ statusCode: number; ourUsers?: User; message?: string }>(`${API_BASE}/adminuser/get-profile`, {
    headers: jsonHeaders(token),
  })
}

export async function getAllUsers(token: string) {
  return fetchJson<UsersResponse>(`${API_BASE}/admin/get-all-users`, {
    headers: jsonHeaders(token),
  })
}

export async function getAllManagerUsers(token: string) {
  return fetchJson<UsersResponse>(`${API_BASE}/manager/get-all-manager-users`, {
    headers: jsonHeaders(token),
  })
}

export async function getAllDeveloperUsers(token: string) {
  return fetchJson<UsersResponse>(`${API_BASE}/developer/get-all-developer-users`, {
    headers: jsonHeaders(token),
  })
}

type UpdatableUserFields = Partial<Pick<User, 'email' | 'name' | 'city' | 'role'>> & { password?: string }

export async function updateManagerUser(token: string, userId: number, body: UpdatableUserFields) {
  return fetchJson<{ statusCode: number; message?: string; ourUsers?: User }>(`${API_BASE}/manager/update/${userId}`, {
    method: 'PUT',
    headers: jsonHeaders(token),
    body: JSON.stringify(body),
  })
}

export async function updateDeveloperUser(token: string, userId: number, body: UpdatableUserFields) {
  return fetchJson<{ statusCode: number; message?: string; ourUsers?: User }>(`${API_BASE}/developer/update/${userId}`, {
    method: 'PUT',
    headers: jsonHeaders(token),
    body: JSON.stringify(body),
  })
}

export async function adminCreateUser(token: string, body: { name: string; email: string; password: string; city?: string; organization?: string; role: Role }) {
  return fetchJson<{ statusCode: number; message?: string; ourUsers?: User }>(`${API_BASE}/admin/create`, {
    method: 'POST',
    headers: jsonHeaders(token),
    body: JSON.stringify(body),
  })
}

export async function createBusiness(token: string, name: string, description?: string) {
  return fetchJson<{ statusCode: number; message?: string }>(`${API_BASE}/business/create`, {
    method: 'POST',
    headers: jsonHeaders(token),
    body: JSON.stringify({ name, message: description ?? '' }),
  })
}

export async function getMyBusiness(token: string) {
  return fetchJson<{ statusCode: number; message?: string }>(`${API_BASE}/business/my`, {
    headers: jsonHeaders(token),
  })
}
