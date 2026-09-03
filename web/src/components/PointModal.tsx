import React, { useState } from 'react';
import { X, Star, Folder as FolderIcon, Plus } from 'lucide-react';
import { useApp } from '../context/AppContext';

export const PointModal: React.FC = () => {
  const { isPointModalOpen, setIsPointModalOpen, addPoint, folders, selectedFolderId } = useApp();

  const [text, setText] = useState('');
  const [folderId, setFolderId] = useState(selectedFolderId || '');

  if (!isPointModalOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!text.trim()) return;

    addPoint(text.trim(), folderId || undefined);
    setText('');
    setIsPointModalOpen(false);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 bg-black/50 backdrop-blur-sm animate-fade-in">
      <div className="w-full max-w-md bg-white dark:bg-sivo-dark-surface rounded-3xl border border-sivo-border dark:border-sivo-dark-border shadow-2xl p-6 animate-slide-up">
        
        <div className="flex items-center justify-between pb-4 border-b border-sivo-border/60 dark:border-sivo-dark-border/60">
          <div className="flex items-center gap-2">
            <div className="p-1.5 rounded-xl bg-pastel-yellow-bg text-pastel-yellow">
              <Star className="w-4 h-4" />
            </div>
            <h3 className="text-lg font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
              Important Point
            </h3>
          </div>
          <button
            onClick={() => setIsPointModalOpen(false)}
            className="p-2 rounded-full hover:bg-sivo-surface-variant dark:hover:bg-sivo-dark-surface-variant text-sivo-text-secondary hover:text-sivo-text-primary"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="mt-4 space-y-4">
          <div>
            <label className="block text-xs font-semibold text-sivo-text-secondary dark:text-sivo-dark-text-secondary mb-1">
              Fact / Concept to Remember *
            </label>
            <textarea
              placeholder="e.g. Java String is immutable (stored in String Pool)"
              value={text}
              onChange={(e) => setText(e.target.value)}
              rows={3}
              className="w-full px-4 py-2.5 rounded-2xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border dark:border-sivo-dark-border text-sm outline-none focus:border-sivo-primary text-sivo-text-primary dark:text-sivo-dark-text-primary resize-none"
              autoFocus
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-sivo-text-secondary dark:text-sivo-dark-text-secondary mb-1 flex items-center gap-1">
              <FolderIcon className="w-3.5 h-3.5 text-sivo-primary" />
              Folder
            </label>
            <select
              value={folderId}
              onChange={(e) => setFolderId(e.target.value)}
              className="w-full px-3 py-2 rounded-xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border dark:border-sivo-dark-border text-xs text-sivo-text-primary dark:text-sivo-dark-text-primary outline-none focus:border-sivo-primary cursor-pointer"
            >
              <option value="">No Folder (General)</option>
              {folders.map(f => (
                <option key={f.id} value={f.id}>{f.name}</option>
              ))}
            </select>
          </div>

          <div className="pt-3 flex justify-end gap-2">
            <button
              type="button"
              onClick={() => setIsPointModalOpen(false)}
              className="px-4 py-2 rounded-full text-xs font-semibold text-sivo-text-secondary hover:bg-sivo-surface-variant transition-all"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="flex items-center gap-1.5 px-5 py-2.5 rounded-full bg-pastel-yellow hover:bg-amber-600 text-white text-xs font-semibold shadow-md transition-all"
            >
              <Plus className="w-4 h-4" />
              Save Point
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
