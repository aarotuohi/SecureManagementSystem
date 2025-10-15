import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    host: '127.0.0.1',
    proxy: {
      '/auth': 'http://localhost:8081',
      '/admin': 'http://localhost:8081',
      '/adminuser': 'http://localhost:8081',
      '/user': 'http://localhost:8081',
      '/manager': 'http://localhost:8081',
      '/developer': 'http://localhost:8081'
    }
  }
})
