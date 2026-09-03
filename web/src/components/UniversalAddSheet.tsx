import React from 'react';
import { 
  FileText, 
  CheckSquare, 
  Bell, 
  Star, 
  FolderPlus, 
  Lock, 
  X 
} from 'lucide-react';
import { useApp } from '../context/AppContext';

export const UniversalAddSheet: React.FC = () => {
  const { 
    isUniversalAddOpen, 
    setIsUniversalAddOpen,
    setEditingNote,
    setIsNoteEditorOpen,
    setIsTodoModalOpen,
    setIsReminderModalOpen,
    setIsPointModalOpen,
    setIsFolderModalOpen,
    setActiveTab
  } = useApp();

  if (!isUniversalAddOpen) return null;

  const options = [
    {
      id: 'note',
      title: 'Note',
      desc: 'Rich text & ideas',
      icon: FileText,
      color: 'bg-pastel-lavender-bg text-pastel-lavender border-pastel-lavender/30',
      action: () => {
        setEditingNote(null);
        setIsUniversalAddOpen(false);
        setIsNoteEditorOpen(true);
      }
    },
    {
      id: 'todo',
      title: 'Daily Todo',
      desc: 'Actionable tasks & streaks',
      icon: CheckSquare,
      color: 'bg-pastel-mint-bg text-pastel-mint border-pastel-mint/30',
      action: () => {
        setIsUniversalAddOpen(false);
        setIsTodoModalOpen(true);
      }
    },
    {
      id: 'point',
      title: 'Important Point',
      desc: 'Quick facts to remember',
      icon: Star,
      color: 'bg-pastel-yellow-bg text-pastel-yellow border-pastel-yellow/30',
      action: () => {
        setIsUniversalAddOpen(false);
        setIsPointModalOpen(true);
      }
    },
    {
      id: 'reminder',
      title: 'Reminder',
      desc: 'Time-based alert',
      icon: Bell,
      color: 'bg-pastel-coral-bg text-pastel-coral border-pastel-coral/30',
      action: () => {
        setIsUniversalAddOpen(false);
        setIsReminderModalOpen(true);
      }
    },
    {
      id: 'folder',
      title: 'Custom Folder',
      desc: 'Organize your spaces',
      icon: FolderPlus,
      color: 'bg-pastel-sky-bg text-pastel-sky border-pastel-sky/30',
      action: () => {
        setIsUniversalAddOpen(false);
        setIsFolderModalOpen(true);
      }
    },
    {
      id: 'vault',
      title: 'Vault Entry',
      desc: 'Encrypted passwords & notes',
      icon: Lock,
      color: 'bg-sivo-primary-container text-sivo-primary border-sivo-primary/30',
      action: () => {
        setIsUniversalAddOpen(false);
        setActiveTab('vault');
      }
    }
  ];

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/40 backdrop-blur-sm animate-fade-in">
      <div 
        className="w-full sm:max-w-md bg-white dark:bg-sivo-dark-surface rounded-t-3xl sm:rounded-3xl border border-sivo-border dark:border-sivo-dark-border shadow-2xl p-6 animate-slide-up"
      >
        <div className="flex items-center justify-between pb-4 border-b border-sivo-border/60 dark:border-sivo-dark-border/60">
          <div>
            <h3 className="text-lg font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
              Create New
            </h3>
            <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary">
              What would you like to capture?
            </p>
          </div>
          <button
            onClick={() => setIsUniversalAddOpen(false)}
            className="p-2 rounded-full hover:bg-sivo-surface-variant dark:hover:bg-sivo-dark-surface-variant text-sivo-text-secondary hover:text-sivo-text-primary"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="grid grid-cols-2 gap-3 py-4">
          {options.map((opt) => {
            const Icon = opt.icon;
            return (
              <button
                key={opt.id}
                onClick={opt.action}
                className="flex flex-col items-start p-3.5 rounded-2xl border border-sivo-border/70 dark:border-sivo-dark-border/70 bg-sivo-bg/50 dark:bg-sivo-dark-bg/50 hover:bg-sivo-surface-variant/70 dark:hover:bg-sivo-dark-surface-variant/70 hover:scale-[1.02] active:scale-[0.98] transition-all text-left group"
              >
                <div className={`p-2 rounded-xl mb-2.5 border ${opt.color} group-hover:shadow-sm`}>
                  <Icon className="w-4 h-4" />
                </div>
                <span className="text-sm font-semibold text-sivo-text-primary dark:text-sivo-dark-text-primary">
                  {opt.title}
                </span>
                <span className="text-[11px] text-sivo-text-secondary dark:text-sivo-dark-text-secondary line-clamp-1">
                  {opt.desc}
                </span>
              </button>
            );
          })}
        </div>
      </div>
    </div>
  );
};
