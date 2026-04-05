import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  build: {
    outDir: 'dist',
    sourcemap: false,
    minify: 'terser',
    rollupOptions: {
      output: {
        entryFileNames: 'js/[name].[hash].js',
        chunkFileNames: 'js/[name].[hash].js',
        assetFileNames: ({ name }: { name: string }) => {
          if (/\.(jpg|jpeg|png|gif|svg)$/.test(name ?? '')) {
            return 'images/[name].[hash][extname]'
          } else if (/\.css$/.test(name ?? '')) {
            return 'css/[name].[hash][extname]'
          }
          return '[name].[hash][extname]'
        }
      }
    }
  },
  server: {
    port: 5173,
    host: true
  }
})
