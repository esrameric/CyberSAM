/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        'defense-dark': '#0a0e27',
        'defense-darker': '#050812',
        'defense-accent': '#0f7ba0',
        'defense-danger': '#d32f2f',
        'defense-warning': '#f57c00',
        'defense-success': '#388e3c',
      },
      fontFamily: {
        'sans': ['Inter', 'system-ui', 'sans-serif'],
        'mono': ['Fira Code', 'monospace'],
      },
    },
  },
  plugins: [],
}
