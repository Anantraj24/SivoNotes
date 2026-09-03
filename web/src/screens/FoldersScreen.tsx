import React, { useState } from 'react';
import { 
  FolderPlus, 
  Trash2, 
  ChevronRight, 
  ArrowLeft, 
  FileText, 
  CheckSquare, 
  Star, 
  Plus, 
  Pin,
  CheckCircle2,
  Circle
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { ICONS_MAP } from '../components/FolderModal';
import { Folder } from '../types';

export const FoldersScreen: React.FC = () => {
  const { 
    folders, 
    deleteFolder, 
    setIsFolderModalOpen, 
    notes, 
    todos, 
    points,
    setEditingNote,
    setIsNoteEditorOpen,
    toggleTodo,
    togglePoint
  } = useApp();

  const [activeFolderId, setActiveFolderId] = useState<string | null>(null);

  const activeFolder = folders.find(f => f.id === activeFolderId);

  const getFolderCounts = (folderId: string) => {
    const noteCount = notes.filter(n => n.folderId === folderId).length;
    const todoCount = todos.filter(t => t.folderId === folderId).length;
    const pointCount = points.filter(p => p.folderId === folderId).length;
    return { noteCount, todoCount, pointCount, total: noteCount + todoCount + pointCount };
  };

  const getColorClasses = (colorKey: string) => {
    switch (colorKey) {
      case 'sky':
        return { bg: 'bg-pastel-sky-bg', text: 'text-pastel-sky', border: 'border-pastel-sky/30' };
      case 'mint':
        return { bg: 'bg-pastel-mint-bg', text: 'text-pastel-mint', border: 'border-pastel-mint/30' };
      case 'amber':
        return { bg: 'bg-pastel-amber-bg', text: 'text-pastel-amber', border: 'border-pastel-amber/30' };
      case 'yellow':
        return { bg: 'bg-pastel-yellow-bg', text: 'text-pastel-yellow', border: 'border-pastel-yellow/30' };
      case 'coral':
        return { bg: 'bg-pastel-coral-bg', text: 'text-pastel-coral', border: 'border-pastel-coral/30' };
      case 'rose':
        return { bg: 'bg-pastel-rose-bg', text: 'text-pastel-rose', border: 'border-pastel-rose/30' };
      case 'lavender':
      default:
        return { bg: 'bg-pastel-lavender-bg', text: 'text-pastel-lavender', border: 'border-pastel-lavender/30' };
    }
  };

  // Folder Detail View (Master Doc Section 16)
  if (activeFolder) {
    const folderNotes = notes.filter(n => n.folderId === activeFolder.id);
    const folderTodos = todos.filter(t => t.folderId === activeFolder.id);
    const folderPoints = points.filter(p => p.folderId === activeFolder.id);
    const colors = getColorClasses(activeFolder.colorKey);
    const IconComp = ICONS_MAP[activeFolder.icon] || ICONS_MAP.Folder;

    return (
      <div className="space-y-6 pb-24 max-w-4xl mx-auto px-4 sm:px-6 pt-4 animate-fade-in">
        {/* Back and Title Header */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <button
              onClick={() => setActiveFolderId(null)}
              className="p-2 rounded-xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border text-sivo-text-secondary hover:text-sivo-text-primary shadow-sivo-sm"
            >
              <ArrowLeft className="w-4 h-4" />
            </button>
            <div className="flex items-center gap-2.5">
              <div className={`p-2 rounded-xl border ${colors.bg} ${colors.text} ${colors.border}`}>
                <IconComp className="w-5 h-5" />
              </div>
              <h2 className="text-2xl font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
                {activeFolder.name}
              </h2>
            </div>
          </div>

          <button
            onClick={() => {
              deleteFolder(activeFolder.id);
              setActiveFolderId(null);
            }}
            className="p-2 rounded-xl text-sivo-error hover:bg-sivo-error-container/60 transition-all"
            title="Delete Folder"
          >
            <Trash2 className="w-4 h-4" />
          </button>
        </div>

        {/* NOTES section */}
        <div>
          <div className="flex items-center justify-between mb-3">
            <h3 className="text-xs font-bold uppercase tracking-wider text-sivo-primary flex items-center gap-1.5">
              <FileText className="w-3.5 h-3.5" />
              Notes ({folderNotes.length})
            </h3>
          </div>
          {folderNotes.length === 0 ? (
            <p className="text-xs text-sivo-text-muted p-4 rounded-2xl bg-white/60 dark:bg-sivo-dark-surface/60 border border-sivo-border/40">
              No notes in this folder.
            </p>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              {folderNotes.map((note) => (
                <div
                  key={note.id}
                  onClick={() => {
                    setEditingNote(note);
                    setIsNoteEditorOpen(true);
                  }}
                  className="p-4 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm hover:border-sivo-primary/50 transition-all cursor-pointer"
                >
                  <div className="flex items-center justify-between mb-1">
                    <h4 className="text-sm font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary">
                      {note.title}
                    </h4>
                    {note.isPinned && <Pin className="w-3 h-3 text-sivo-primary rotate-45" />}
                  </div>
                  <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary line-clamp-2">
                    {note.content.replace(/[#*`_]/g, '') || 'No content...'}
                  </p>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* TODOS section */}
        <div>
          <div className="flex items-center justify-between mb-3">
            <h3 className="text-xs font-bold uppercase tracking-wider text-pastel-mint flex items-center gap-1.5">
              <CheckSquare className="w-3.5 h-3.5" />
              Todos ({folderTodos.length})
            </h3>
          </div>
          {folderTodos.length === 0 ? (
            <p className="text-xs text-sivo-text-muted p-4 rounded-2xl bg-white/60 dark:bg-sivo-dark-surface/60 border border-sivo-border/40">
              No tasks assigned to this folder.
            </p>
          ) : (
            <div className="space-y-2">
              {folderTodos.map((todo) => (
                <div
                  key={todo.id}
                  onClick={() => toggleTodo(todo.id)}
                  className="p-3.5 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm flex items-center gap-3 cursor-pointer"
                >
                  <div className={`w-4 h-4 rounded-md border flex items-center justify-center ${
                    todo.isCompleted ? 'bg-pastel-mint border-pastel-mint text-white' : 'border-sivo-border'
                  }`}>
                    {todo.isCompleted && <CheckCircle2 className="w-3 h-3" />}
                  </div>
                  <span className={`text-xs font-medium flex-1 ${
                    todo.isCompleted ? 'line-through text-sivo-text-muted' : 'text-sivo-text-primary dark:text-sivo-dark-text-primary'
                  }`}>
                    {todo.title}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* IMPORTANT POINTS section */}
        <div>
          <div className="flex items-center justify-between mb-3">
            <h3 className="text-xs font-bold uppercase tracking-wider text-pastel-yellow flex items-center gap-1.5">
              <Star className="w-3.5 h-3.5" />
              Important Points ({folderPoints.length})
            </h3>
          </div>
          {folderPoints.length === 0 ? (
            <p className="text-xs text-sivo-text-muted p-4 rounded-2xl bg-white/60 dark:bg-sivo-dark-surface/60 border border-sivo-border/40">
              No points tagged with this folder.
            </p>
          ) : (
            <div className="space-y-2">
              {folderPoints.map((point) => (
                <div
                  key={point.id}
                  onClick={() => togglePoint(point.id)}
                  className="p-3 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm flex items-center gap-3 cursor-pointer"
                >
                  <div className={`w-4 h-4 rounded-md border flex items-center justify-center ${
                    point.isCompleted ? 'bg-pastel-mint border-pastel-mint text-white' : 'border-sivo-border'
                  }`}>
                    {point.isCompleted && <CheckCircle2 className="w-3 h-3" />}
                  </div>
                  <span className={`text-xs font-medium ${
                    point.isCompleted ? 'line-through text-sivo-text-muted' : 'text-sivo-text-primary dark:text-sivo-dark-text-primary'
                  }`}>
                    {point.text}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    );
  }

  // Folders Grid View
  return (
    <div className="space-y-5 pb-24 max-w-4xl mx-auto px-4 sm:px-6 pt-4 animate-fade-in">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold tracking-tight text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
            Folders
          </h2>
          <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary">
            Organize notes, todos & points by context
          </p>
        </div>

        <button
          onClick={() => setIsFolderModalOpen(true)}
          className="flex items-center gap-1.5 px-4 py-2 rounded-full bg-sivo-primary hover:bg-sivo-primary-variant text-white text-xs font-semibold shadow-md shadow-sivo-primary/30 transition-all"
        >
          <Plus className="w-4 h-4" />
          New Folder
        </button>
      </div>

      {/* Folders Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3.5">
        {folders.map((folder) => {
          const counts = getFolderCounts(folder.id);
          const colors = getColorClasses(folder.colorKey);
          const IconComp = ICONS_MAP[folder.icon] || ICONS_MAP.Folder;

          return (
            <div
              key={folder.id}
              onClick={() => setActiveFolderId(folder.id)}
              className="p-5 rounded-3xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm hover:shadow-sivo-md hover:border-sivo-primary/50 transition-all cursor-pointer flex flex-col justify-between group"
            >
              <div>
                <div className="flex items-center justify-between mb-3">
                  <div className={`p-3 rounded-2xl border ${colors.bg} ${colors.text} ${colors.border} group-hover:scale-105 transition-transform`}>
                    <IconComp className="w-6 h-6" />
                  </div>
                  <ChevronRight className="w-4 h-4 text-sivo-text-muted group-hover:translate-x-1 transition-transform" />
                </div>

                <h3 className="text-base font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit'] mb-1">
                  {folder.name}
                </h3>
                <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary">
                  {counts.total} items stored
                </p>
              </div>

              {/* Item breakdown pills */}
              <div className="flex items-center gap-2 mt-4 pt-3 border-t border-sivo-border/50 dark:border-sivo-dark-border/50 text-[11px] text-sivo-text-muted">
                <span>{counts.noteCount} Notes</span>
                <span>•</span>
                <span>{counts.todoCount} Tasks</span>
                <span>•</span>
                <span>{counts.pointCount} Points</span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
