import React, { useState } from 'react';
import { Star, Plus, CheckCircle2, Circle, Trash2, Folder as FolderIcon } from 'lucide-react';
import { useApp } from '../context/AppContext';

export const ImportantPointsScreen: React.FC = () => {
  const { points, addPoint, togglePoint, deletePoint, folders, setIsPointModalOpen } = useApp();

  const [quickInput, setQuickInput] = useState('');
  const [selectedFolder, setSelectedFolder] = useState<string>('all');

  const handleQuickAdd = (e: React.FormEvent) => {
    e.preventDefault();
    if (!quickInput.trim()) return;
    addPoint(quickInput.trim(), selectedFolder !== 'all' ? selectedFolder : undefined);
    setQuickInput('');
  };

  const filteredPoints = points.filter(p => {
    if (selectedFolder !== 'all' && p.folderId !== selectedFolder) return false;
    return true;
  });

  const getFolder = (folderId?: string) => folders.find(f => f.id === folderId);

  const activePoints = filteredPoints.filter(p => !p.isCompleted);
  const completedPoints = filteredPoints.filter(p => p.isCompleted);

  return (
    <div className="space-y-5 pb-24 max-w-4xl mx-auto px-4 sm:px-6 pt-4 animate-fade-in">
      {/* Top Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2.5">
          <div className="p-2 rounded-2xl bg-pastel-yellow-bg text-pastel-yellow">
            <Star className="w-5 h-5 fill-current" />
          </div>
          <div>
            <h2 className="text-2xl font-bold tracking-tight text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
              Important Points
            </h2>
            <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary">
              Crucial facts, exam formulas, and key concepts
            </p>
          </div>
        </div>

        <button
          onClick={() => setIsPointModalOpen(true)}
          className="flex items-center gap-1.5 px-4 py-2 rounded-full bg-pastel-yellow hover:bg-amber-600 text-white text-xs font-semibold shadow-md transition-all"
        >
          <Plus className="w-4 h-4" />
          Add Point
        </button>
      </div>

      {/* Fast Inline Capture Bar */}
      <form onSubmit={handleQuickAdd} className="flex items-center gap-2">
        <input
          type="text"
          placeholder="Fast capture: type an important fact & hit Enter..."
          value={quickInput}
          onChange={(e) => setQuickInput(e.target.value)}
          className="flex-1 px-4 py-3 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm text-xs outline-none focus:border-pastel-yellow text-sivo-text-primary dark:text-sivo-dark-text-primary"
        />
        <button
          type="submit"
          disabled={!quickInput.trim()}
          className="px-5 py-3 rounded-2xl bg-pastel-yellow hover:bg-amber-600 disabled:opacity-50 text-white text-xs font-semibold transition-all shadow-sivo-sm"
        >
          Save
        </button>
      </form>

      {/* Folder Chips */}
      <div className="flex items-center gap-1.5 overflow-x-auto no-scrollbar py-1">
        <button
          onClick={() => setSelectedFolder('all')}
          className={`px-3 py-1 rounded-full text-xs font-semibold whitespace-nowrap transition-all ${
            selectedFolder === 'all'
              ? 'bg-pastel-yellow text-white'
              : 'bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border text-sivo-text-secondary'
          }`}
        >
          All Folders ({points.length})
        </button>
        {folders.map(f => {
          const count = points.filter(p => p.folderId === f.id).length;
          return (
            <button
              key={f.id}
              onClick={() => setSelectedFolder(f.id)}
              className={`px-3 py-1 rounded-full text-xs font-semibold whitespace-nowrap transition-all ${
                selectedFolder === f.id
                  ? 'bg-pastel-yellow text-white'
                  : 'bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border text-sivo-text-secondary'
              }`}
            >
              {f.name} ({count})
            </button>
          );
        })}
      </div>

      {/* Points Checklist Container */}
      <div className="space-y-3">
        {filteredPoints.length === 0 ? (
          <div className="p-12 text-center rounded-3xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm my-4">
            <div className="w-12 h-12 rounded-2xl bg-pastel-yellow-bg text-pastel-yellow flex items-center justify-center mx-auto mb-3">
              <Star className="w-6 h-6" />
            </div>
            <h3 className="text-base font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
              No points stored yet
            </h3>
            <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary mt-1">
              Store small facts or key points that you want to remember without making a full note.
            </p>
          </div>
        ) : (
          <div className="space-y-2">
            {/* Active Points */}
            {activePoints.map((point) => {
              const folder = getFolder(point.folderId);
              return (
                <div
                  key={point.id}
                  onClick={() => togglePoint(point.id)}
                  className="p-3.5 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm hover:border-pastel-yellow/60 transition-all cursor-pointer flex items-center justify-between gap-3 group"
                >
                  <div className="flex items-center gap-3 flex-1">
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        togglePoint(point.id);
                      }}
                      className="w-5 h-5 rounded-lg border border-sivo-border dark:border-sivo-dark-border bg-white dark:bg-sivo-dark-surface group-hover:border-pastel-yellow flex items-center justify-center transition-all"
                    >
                      <Circle className="w-3.5 h-3.5 opacity-0 group-hover:opacity-40 text-pastel-yellow" />
                    </button>
                    <span className="text-xs sm:text-sm font-medium text-sivo-text-primary dark:text-sivo-dark-text-primary">
                      {point.text}
                    </span>
                  </div>

                  <div className="flex items-center gap-2">
                    {folder && (
                      <span className="text-[10px] font-semibold px-2 py-0.5 rounded-full bg-pastel-yellow-bg text-pastel-yellow border border-pastel-yellow/30">
                        {folder.name}
                      </span>
                    )}
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        deletePoint(point.id);
                      }}
                      className="p-1 rounded-lg text-sivo-text-muted hover:text-sivo-error opacity-0 group-hover:opacity-100 transition-all"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>
              );
            })}

            {/* Completed Points (strikethrough + subtle emphasis) */}
            {completedPoints.length > 0 && (
              <div className="pt-4 space-y-2">
                <span className="text-xs font-bold uppercase tracking-wider text-sivo-text-muted block">
                  Reviewed Points ({completedPoints.length})
                </span>
                {completedPoints.map((point) => (
                  <div
                    key={point.id}
                    onClick={() => togglePoint(point.id)}
                    className="p-3.5 rounded-2xl bg-sivo-surface-variant/40 dark:bg-sivo-dark-surface-variant/30 border border-sivo-border/40 dark:border-sivo-dark-border/40 transition-all cursor-pointer flex items-center justify-between gap-3 group opacity-70 hover:opacity-100"
                  >
                    <div className="flex items-center gap-3 flex-1">
                      <div className="w-5 h-5 rounded-lg bg-pastel-mint text-white flex items-center justify-center">
                        <CheckCircle2 className="w-3.5 h-3.5" />
                      </div>
                      <span className="text-xs sm:text-sm font-medium line-through text-sivo-text-muted">
                        {point.text}
                      </span>
                    </div>

                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        deletePoint(point.id);
                      }}
                      className="p-1 rounded-lg text-sivo-text-muted hover:text-sivo-error opacity-0 group-hover:opacity-100 transition-all"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};
