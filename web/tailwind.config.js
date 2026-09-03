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
        sivo: {
          primary: '#6C5CE7',
          'primary-variant': '#5B4BD8',
          'primary-light': '#A29BFE',
          'primary-container': '#EDE9FE',
          'on-primary-container': '#4338CA',
          secondary: '#8C7AE6',
          'secondary-container': '#F3F0FF',
          // Light
          bg: '#F8F7FC',
          surface: '#FFFFFF',
          'surface-variant': '#F0EEF8',
          'text-primary': '#2D3436',
          'text-secondary': '#636E72',
          'text-muted': '#B2BEC3',
          border: '#EAE7F4',
          // Dark
          'dark-bg': '#13121A',
          'dark-surface': '#1C1A27',
          'dark-surface-variant': '#262335',
          'dark-text-primary': '#F5F6FA',
          'dark-text-secondary': '#A4B0BE',
          'dark-text-muted': '#718093',
          'dark-border': '#2D2A3E',
          // Status
          success: '#00B894',
          warning: '#F39C12',
          error: '#D63031',
          'error-container': '#FFECEB',
        },
        pastel: {
          coral: '#FF7675',
          'coral-bg': '#FFECEB',
          mint: '#00B894',
          'mint-bg': '#E6F8F3',
          amber: '#E17055',
          'amber-bg': '#FEF3EE',
          yellow: '#F39C12',
          'yellow-bg': '#FEF9E7',
          sky: '#0984E3',
          'sky-bg': '#EAF4FC',
          lavender: '#6C5CE7',
          'lavender-bg': '#F0EEFC',
          rose: '#E84393',
          'rose-bg': '#FDEEF5',
        }
      },
      fontFamily: {
        sans: ['Plus Jakarta Sans', 'Inter', 'system-ui', 'sans-serif'],
      },
      boxShadow: {
        'sivo-sm': '0 2px 8px -1px rgba(108, 92, 231, 0.06), 0 1px 4px -1px rgba(0, 0, 0, 0.04)',
        'sivo-md': '0 8px 24px -4px rgba(108, 92, 231, 0.08), 0 2px 8px -2px rgba(0, 0, 0, 0.04)',
        'sivo-lg': '0 16px 36px -6px rgba(108, 92, 231, 0.12), 0 4px 12px -2px rgba(0, 0, 0, 0.06)',
        'sivo-floating': '0 12px 32px -4px rgba(108, 92, 231, 0.2), 0 4px 12px 0 rgba(0, 0, 0, 0.05)',
      },
      borderRadius: {
        '2xl': '1.25rem',
        '3xl': '1.75rem',
      }
    },
  },
  plugins: [],
}
