export type LoginRequest = { email: string; password: string }
export type RegisterRequest = { name: string; email: string; password: string; city: string; role: 'ADMIN' | 'USER' }

export type AuthResponse = {
  statusCode: number
  token?: string
  refreshToken?: string
  role?: 'ADMIN' | 'USER'
  expirationTime?: string
  message?: string
  error?: string
}

export type User = {
  id: number
  email: string
  name: string
  city: string
  role: 'ADMIN' | 'USER'
}

export type UsersResponse = {
  statusCode: number
  message?: string
  ourUsersList?: User[]
}

const jsonHeaders = (token?: string) => ({
  'Content-Type': 'application/json',
  ...(token ? { Authorization: `Bearer ${token}` } : {}),
})

export async function login(body: LoginRequest): Promise<AuthResponse> {
  const res = await fetch('/auth/login', {
    method: 'POST',
    headers: jsonHeaders(),
    body: JSON.stringify(body),
  })
  return res.json()
}

export async function register(body: RegisterRequest): Promise<AuthResponse> {
  const res = await fetch('/auth/register', {
    method: 'POST',
    headers: jsonHeaders(),
    body: JSON.stringify(body),
  })
  return res.json()
}

export async function getMyProfile(token: string) {
  const res = await fetch('/adminuser/get-profile', {
    headers: jsonHeaders(token),
  })
  return res.json() as Promise<{ statusCode: number; ourUsers?: User; message?: string }>
}

export async function getAllUsers(token: string) {
  const res = await fetch('/admin/get-all-users', {
    headers: jsonHeaders(token),
  })
  return res.json() as Promise<UsersResponse>
}
