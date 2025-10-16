import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5174,
    host: '127.0.0.1',
    proxy: {
      '/auth': 'http://127.0.0.1:8081',
      '/admin': 'http://127.0.0.1:8081',
      '/adminuser': 'http://127.0.0.1:8081',
      '/user': 'http://127.0.0.1:8081',
      '/manager': 'http://127.0.0.1:8081',
      '/developer': 'http://127.0.0.1:8081'
    }
  }
})
