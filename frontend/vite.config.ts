import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/auth': 'http://localhost:8080',
      '/admin': 'http://localhost:8080',
      '/adminuser': 'http://localhost:8080',
      '/user': 'http://localhost:8080'
    }
  }
})
