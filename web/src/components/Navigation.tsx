import React from 'react';
import { Home, FileText, CheckSquare, Folder as FolderIcon, Plus } from 'lucide-react';
import { useApp } from '../context/AppContext';
import { ActiveTab } from '../types';

export const Navigation: React.FC = () => {
  const { activeTab, setActiveTab, setIsUniversalAddOpen } = useApp();

  const navItems: { id: ActiveTab; label: string; icon: React.FC<{ className?: string }> }[] = [
    { id: 'home', label: 'Home', icon: Home },
    { id: 'notes', label: 'Notes', icon: FileText },
    { id: 'todos', label: 'Todos', icon: CheckSquare },
    { id: 'folders', label: 'Folders', icon: FolderIcon },
  ];

  return (
    <div className="fixed bottom-0 left-0 right-0 z-40 p-4 pb-6 flex justify-center pointer-events-none">
      <nav className="pointer-events-auto flex items-center gap-1.5 bg-white/90 dark:bg-sivo-dark-surface/90 backdrop-blur-xl border border-sivo-border/80 dark:border-sivo-dark-border/80 shadow-sivo-floating rounded-full px-4 py-2">
        {navItems.slice(0, 2).map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              className={`flex items-center gap-2 px-3.5 py-2 rounded-full text-xs font-semibold transition-all duration-200 ${
                isActive
                  ? 'bg-sivo-primary text-white shadow-sm shadow-sivo-primary/30 scale-[1.02]'
                  : 'text-sivo-text-secondary dark:text-sivo-dark-text-secondary hover:text-sivo-primary dark:hover:text-white hover:bg-sivo-surface-variant/60 dark:hover:bg-sivo-dark-surface-variant/60'
              }`}
            >
              <Icon className="w-4 h-4" />
              <span>{item.label}</span>
            </button>
          );
        })}

        {/* Floating Universal Add Action */}
        <button
          onClick={() => setIsUniversalAddOpen(true)}
          className="mx-1 flex items-center justify-center w-11 h-11 rounded-full bg-sivo-primary hover:bg-sivo-primary-variant text-white shadow-lg shadow-sivo-primary/40 hover:scale-105 active:scale-95 transition-all duration-200 group"
          title="Create New Item"
        >
          <Plus className="w-5 h-5 group-hover:rotate-90 transition-transform duration-200" />
        </button>

        {navItems.slice(2).map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              className={`flex items-center gap-2 px-3.5 py-2 rounded-full text-xs font-semibold transition-all duration-200 ${
                isActive
                  ? 'bg-sivo-primary text-white shadow-sm shadow-sivo-primary/30 scale-[1.02]'
                  : 'text-sivo-text-secondary dark:text-sivo-dark-text-secondary hover:text-sivo-primary dark:hover:text-white hover:bg-sivo-surface-variant/60 dark:hover:bg-sivo-dark-surface-variant/60'
              }`}
            >
              <Icon className="w-4 h-4" />
              <span>{item.label}</span>
            </button>
          );
        })}
      </nav>
    </div>
  );
};
