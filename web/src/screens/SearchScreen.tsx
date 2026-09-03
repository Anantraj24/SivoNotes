import React, { useState } from 'react';
import { 
  Search as SearchIcon, 
  FileText, 
  CheckSquare, 
  Star, 
  Bell, 
  Folder as FolderIcon,
  ChevronRight,
  Pin
} from 'lucide-react';
import { useApp } from '../context/AppContext';

export const SearchScreen: React.FC = () => {
  const { 
    notes, 
    todos, 
    points, 
    reminders, 
    folders, 
    setEditingNote, 
    setIsNoteEditorOpen, 
    setActiveTab, 
    toggleTodo, 
    togglePoint 
  } = useApp();

  const [query, setQuery] = useState('');

  const q = query.trim().toLowerCase();

  const matchedNotes = q ? notes.filter(n => 
    n.title.toLowerCase().includes(q) || 
    n.content.toLowerCase().includes(q) || 
    n.tags.some(t => t.toLowerCase().includes(q))
  ) : [];

  const matchedTodos = q ? todos.filter(t => 
    t.title.toLowerCase().includes(q) || 
    (t.description && t.description.toLowerCase().includes(q))
  ) : [];

  const matchedPoints = q ? points.filter(p => 
    p.text.toLowerCase().includes(q)
  ) : [];

  const matchedReminders = q ? reminders.filter(r => 
    r.title.toLowerCase().includes(q) || 
    (r.note && r.note.toLowerCase().includes(q))
  ) : [];

  const matchedFolders = q ? folders.filter(f => 
    f.name.toLowerCase().includes(q)
  ) : [];

  const totalResults = matchedNotes.length + matchedTodos.length + matchedPoints.length + matchedReminders.length + matchedFolders.length;

  return (
    <div className="space-y-6 pb-24 max-w-4xl mx-auto px-4 sm:px-6 pt-4 animate-fade-in">
      {/* Header & Search Bar */}
      <div className="space-y-3">
        <h2 className="text-2xl font-bold tracking-tight text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
          Global Search
        </h2>
        <div className="relative">
          <SearchIcon className="w-4 h-4 text-sivo-text-muted absolute left-4 top-3.5" />
          <input
            type="text"
            placeholder="Type anything (e.g. 'java', 'assignment', 'exam')..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            className="w-full pl-11 pr-4 py-3 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border text-sm outline-none focus:border-sivo-primary text-sivo-text-primary dark:text-sivo-dark-text-primary shadow-sivo-sm"
            autoFocus
          />
        </div>
      </div>

      {/* Zero State / Results */}
      {!query ? (
        <div className="p-12 text-center rounded-3xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm my-6">
          <div className="w-12 h-12 rounded-2xl bg-sivo-primary-container text-sivo-primary flex items-center justify-center mx-auto mb-3">
            <SearchIcon className="w-6 h-6" />
          </div>
          <h3 className="text-base font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
            Search Across Everything
          </h3>
          <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary mt-1 max-w-sm mx-auto">
            Find your notes, tasks, key points, reminders, and custom folders instantly. Locked vault content is kept confidential.
          </p>
        </div>
      ) : totalResults === 0 ? (
        <div className="p-12 text-center rounded-3xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm my-6">
          <p className="text-sm text-sivo-text-secondary">No results found for "{query}".</p>
        </div>
      ) : (
        <div className="space-y-6">
          <span className="text-xs font-bold uppercase tracking-wider text-sivo-text-muted block">
            Found {totalResults} {totalResults === 1 ? 'result' : 'results'}
          </span>

          {/* NOTES RESULTS */}
          {matchedNotes.length > 0 && (
            <div>
              <span className="text-xs font-bold uppercase tracking-wider text-sivo-primary mb-2.5 flex items-center gap-1.5">
                <FileText className="w-3.5 h-3.5" /> Notes ({matchedNotes.length})
              </span>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                {matchedNotes.map((note) => (
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
                      {note.content.replace(/[#*`_]/g, '')}
                    </p>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* TODOS RESULTS */}
          {matchedTodos.length > 0 && (
            <div>
              <span className="text-xs font-bold uppercase tracking-wider text-pastel-mint mb-2.5 flex items-center gap-1.5">
                <CheckSquare className="w-3.5 h-3.5" /> Todos ({matchedTodos.length})
              </span>
              <div className="space-y-2">
                {matchedTodos.map((todo) => (
                  <div
                    key={todo.id}
                    onClick={() => toggleTodo(todo.id)}
                    className="p-3.5 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm flex items-center justify-between cursor-pointer"
                  >
                    <span className={`text-xs font-medium ${
                      todo.isCompleted ? 'line-through text-sivo-text-muted' : 'text-sivo-text-primary dark:text-sivo-dark-text-primary'
                    }`}>
                      {todo.title}
                    </span>
                    <span className="text-[10px] uppercase font-bold text-sivo-text-muted">
                      {todo.dueDate || 'Today'}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* IMPORTANT POINTS RESULTS */}
          {matchedPoints.length > 0 && (
            <div>
              <span className="text-xs font-bold uppercase tracking-wider text-pastel-yellow mb-2.5 flex items-center gap-1.5">
                <Star className="w-3.5 h-3.5" /> Important Points ({matchedPoints.length})
              </span>
              <div className="space-y-2">
                {matchedPoints.map((point) => (
                  <div
                    key={point.id}
                    onClick={() => togglePoint(point.id)}
                    className="p-3 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm flex items-center justify-between cursor-pointer"
                  >
                    <span className={`text-xs font-medium ${
                      point.isCompleted ? 'line-through text-sivo-text-muted' : 'text-sivo-text-primary dark:text-sivo-dark-text-primary'
                    }`}>
                      {point.text}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* REMINDERS RESULTS */}
          {matchedReminders.length > 0 && (
            <div>
              <span className="text-xs font-bold uppercase tracking-wider text-pastel-coral mb-2.5 flex items-center gap-1.5">
                <Bell className="w-3.5 h-3.5" /> Reminders ({matchedReminders.length})
              </span>
              <div className="space-y-2">
                {matchedReminders.map((rem) => (
                  <div
                    key={rem.id}
                    className="p-3 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm flex items-center justify-between"
                  >
                    <span className="text-xs font-medium text-sivo-text-primary dark:text-sivo-dark-text-primary">
                      {rem.title}
                    </span>
                    <span className="text-[10px] text-pastel-coral font-bold">
                      {rem.date} @ {rem.time}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* FOLDERS RESULTS */}
          {matchedFolders.length > 0 && (
            <div>
              <span className="text-xs font-bold uppercase tracking-wider text-pastel-sky mb-2.5 flex items-center gap-1.5">
                <FolderIcon className="w-3.5 h-3.5" /> Folders ({matchedFolders.length})
              </span>
              <div className="grid grid-cols-2 gap-2">
                {matchedFolders.map((f) => (
                  <div
                    key={f.id}
                    onClick={() => setActiveTab('folders')}
                    className="p-3 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm flex items-center justify-between cursor-pointer"
                  >
                    <span className="text-xs font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary">
                      {f.name}
                    </span>
                    <ChevronRight className="w-3.5 h-3.5 text-sivo-text-muted" />
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};
