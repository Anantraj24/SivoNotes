import React, { useState } from 'react';
import { 
  X, 
  Plus, 
  GraduationCap, 
  Code, 
  User, 
  BookOpen, 
  Briefcase, 
  Heart, 
  ShoppingBag, 
  Flame, 
  Sparkles, 
  Folder as FolderIcon 
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { PastelColorKey } from '../types';

export const ICONS_MAP: { [key: string]: React.FC<{ className?: string }> } = {
  GraduationCap,
  Code,
  User,
  BookOpen,
  Briefcase,
  Heart,
  ShoppingBag,
  Flame,
  Sparkles,
  Folder: FolderIcon,
};

const COLOR_OPTIONS: { key: PastelColorKey; label: string; bg: string; text: string; border: string }[] = [
  { key: 'lavender', label: 'Lavender', bg: 'bg-pastel-lavender-bg', text: 'text-pastel-lavender', border: 'border-pastel-lavender' },
  { key: 'sky', label: 'Sky', bg: 'bg-pastel-sky-bg', text: 'text-pastel-sky', border: 'border-pastel-sky' },
  { key: 'mint', label: 'Mint', bg: 'bg-pastel-mint-bg', text: 'text-pastel-mint', border: 'border-pastel-mint' },
  { key: 'amber', label: 'Amber', bg: 'bg-pastel-amber-bg', text: 'text-pastel-amber', border: 'border-pastel-amber' },
  { key: 'yellow', label: 'Yellow', bg: 'bg-pastel-yellow-bg', text: 'text-pastel-yellow', border: 'border-pastel-yellow' },
  { key: 'coral', label: 'Coral', bg: 'bg-pastel-coral-bg', text: 'text-pastel-coral', border: 'border-pastel-coral' },
  { key: 'rose', label: 'Rose', bg: 'bg-pastel-rose-bg', text: 'text-pastel-rose', border: 'border-pastel-rose' },
];

export const FolderModal: React.FC = () => {
  const { isFolderModalOpen, setIsFolderModalOpen, addFolder } = useApp();

  const [name, setName] = useState('');
  const [selectedIcon, setSelectedIcon] = useState('Folder');
  const [selectedColor, setSelectedColor] = useState<PastelColorKey>('lavender');

  if (!isFolderModalOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;

    addFolder(name.trim(), selectedIcon, selectedColor);
    setName('');
    setIsFolderModalOpen(false);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 bg-black/50 backdrop-blur-sm animate-fade-in">
      <div className="w-full max-w-md bg-white dark:bg-sivo-dark-surface rounded-3xl border border-sivo-border dark:border-sivo-dark-border shadow-2xl p-6 animate-slide-up">
        
        <div className="flex items-center justify-between pb-4 border-b border-sivo-border/60 dark:border-sivo-dark-border/60">
          <h3 className="text-lg font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
            Create Custom Folder
          </h3>
          <button
            onClick={() => setIsFolderModalOpen(false)}
            className="p-2 rounded-full hover:bg-sivo-surface-variant dark:hover:bg-sivo-dark-surface-variant text-sivo-text-secondary hover:text-sivo-text-primary"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="mt-4 space-y-4">
          <div>
            <label className="block text-xs font-semibold text-sivo-text-secondary dark:text-sivo-dark-text-secondary mb-1">
              Folder Name *
            </label>
            <input
              type="text"
              placeholder="e.g. College, Projects, Finance..."
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full px-4 py-2.5 rounded-2xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border dark:border-sivo-dark-border text-sm outline-none focus:border-sivo-primary text-sivo-text-primary dark:text-sivo-dark-text-primary"
              autoFocus
              required
            />
          </div>

          {/* Icon Chooser */}
          <div>
            <label className="block text-xs font-semibold text-sivo-text-secondary dark:text-sivo-dark-text-secondary mb-2">
              Choose Icon
            </label>
            <div className="grid grid-cols-5 gap-2">
              {Object.keys(ICONS_MAP).map((iconKey) => {
                const IconComp = ICONS_MAP[iconKey];
                const isSelected = selectedIcon === iconKey;
                return (
                  <button
                    key={iconKey}
                    type="button"
                    onClick={() => setSelectedIcon(iconKey)}
                    className={`flex items-center justify-center p-3 rounded-2xl border transition-all ${
                      isSelected
                        ? 'bg-sivo-primary text-white border-sivo-primary shadow-sm scale-105'
                        : 'border-sivo-border dark:border-sivo-dark-border text-sivo-text-secondary hover:bg-sivo-surface-variant'
                    }`}
                  >
                    <IconComp className="w-4 h-4" />
                  </button>
                );
              })}
            </div>
          </div>

          {/* Color Palette Chooser */}
          <div>
            <label className="block text-xs font-semibold text-sivo-text-secondary dark:text-sivo-dark-text-secondary mb-2">
              Choose Pastel Accent
            </label>
            <div className="flex items-center justify-between gap-2">
              {COLOR_OPTIONS.map((c) => {
                const isSelected = selectedColor === c.key;
                return (
                  <button
                    key={c.key}
                    type="button"
                    onClick={() => setSelectedColor(c.key)}
                    className={`flex flex-col items-center gap-1 p-2 rounded-xl border transition-all flex-1 ${c.bg} ${
                      isSelected ? `ring-2 ring-sivo-primary ${c.border} scale-105` : 'border-transparent'
                    }`}
                  >
                    <div className={`w-4 h-4 rounded-full ${c.text} border-2 border-current flex items-center justify-center`}>
                      {isSelected && <div className="w-1.5 h-1.5 rounded-full bg-current" />}
                    </div>
                  </button>
                );
              })}
            </div>
          </div>

          <div className="pt-3 flex justify-end gap-2">
            <button
              type="button"
              onClick={() => setIsFolderModalOpen(false)}
              className="px-4 py-2 rounded-full text-xs font-semibold text-sivo-text-secondary hover:bg-sivo-surface-variant transition-all"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="flex items-center gap-1.5 px-5 py-2.5 rounded-full bg-sivo-primary hover:bg-sivo-primary-variant text-white text-xs font-semibold shadow-md shadow-sivo-primary/30 transition-all"
            >
              <Plus className="w-4 h-4" />
              Create Folder
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
