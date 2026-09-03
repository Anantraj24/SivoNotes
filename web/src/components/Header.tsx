import React from 'react';
import { Search, Moon, Sun, Settings, Lock, Sparkles } from 'lucide-react';
import { useApp } from '../context/AppContext';

export const Header: React.FC = () => {
  const { activeTab, setActiveTab, settings, toggleDarkMode } = useApp();

  return (
    <header className="sticky top-0 z-30 bg-sivo-bg/80 dark:bg-sivo-dark-bg/80 backdrop-blur-md border-b border-sivo-border/60 dark:border-sivo-dark-border/60 px-4 sm:px-8 py-3 transition-colors">
      <div className="max-w-4xl mx-auto flex items-center justify-between">
        {/* Brand */}
        <div 
          onClick={() => setActiveTab('home')}
          className="flex items-center gap-2.5 cursor-pointer group"
        >
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-sivo-primary to-sivo-primary-light flex items-center justify-center text-white shadow-md shadow-sivo-primary/25 group-hover:scale-105 transition-transform">
            <Sparkles className="w-5 h-5" />
          </div>
          <div>
            <h1 className="text-base font-bold tracking-tight text-sivo-text-primary dark:text-sivo-dark-text-primary flex items-center gap-1.5 font-['Outfit']">
              SIVO NOTES
              <span className="text-[10px] uppercase font-semibold tracking-wider px-1.5 py-0.5 rounded-full bg-sivo-primary-container text-sivo-on-primary-container dark:bg-sivo-primary/20 dark:text-sivo-primary-light">
                Offline
              </span>
            </h1>
          </div>
        </div>

        {/* Action icons */}
        <div className="flex items-center gap-1.5 sm:gap-2">
          {/* Search */}
          <button
            onClick={() => setActiveTab('search')}
            className={`p-2 rounded-xl transition-all ${
              activeTab === 'search'
                ? 'bg-sivo-primary-container text-sivo-primary dark:bg-sivo-primary/30 dark:text-sivo-primary-light'
                : 'text-sivo-text-secondary dark:text-sivo-dark-text-secondary hover:bg-sivo-surface-variant dark:hover:bg-sivo-dark-surface-variant hover:text-sivo-primary'
            }`}
            title="Global Search"
          >
            <Search className="w-4 h-4" />
          </button>

          {/* Vault Shortcut */}
          <button
            onClick={() => setActiveTab('vault')}
            className={`p-2 rounded-xl transition-all ${
              activeTab === 'vault'
                ? 'bg-sivo-primary-container text-sivo-primary dark:bg-sivo-primary/30 dark:text-sivo-primary-light'
                : 'text-sivo-text-secondary dark:text-sivo-dark-text-secondary hover:bg-sivo-surface-variant dark:hover:bg-sivo-dark-surface-variant hover:text-sivo-primary'
            }`}
            title="Secure Vault"
          >
            <Lock className="w-4 h-4" />
          </button>

          {/* Theme Toggle */}
          <button
            onClick={toggleDarkMode}
            className="p-2 rounded-xl text-sivo-text-secondary dark:text-sivo-dark-text-secondary hover:bg-sivo-surface-variant dark:hover:bg-sivo-dark-surface-variant hover:text-sivo-primary transition-all"
            title={settings.darkMode ? 'Light mode' : 'Dark mode'}
          >
            {settings.darkMode ? <Sun className="w-4 h-4 text-amber-400" /> : <Moon className="w-4 h-4" />}
          </button>

          {/* Settings */}
          <button
            onClick={() => setActiveTab('settings')}
            className={`p-2 rounded-xl transition-all ${
              activeTab === 'settings'
                ? 'bg-sivo-primary-container text-sivo-primary dark:bg-sivo-primary/30 dark:text-sivo-primary-light'
                : 'text-sivo-text-secondary dark:text-sivo-dark-text-secondary hover:bg-sivo-surface-variant dark:hover:bg-sivo-dark-surface-variant hover:text-sivo-primary'
            }`}
            title="Settings & Data"
          >
            <Settings className="w-4 h-4" />
          </button>
        </div>
      </div>
    </header>
  );
};
