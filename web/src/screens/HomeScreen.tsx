import React from 'react';
import { 
  Search, 
  FileText, 
  CheckSquare, 
  Star, 
  Bell, 
  Flame, 
  Lock, 
  ChevronRight, 
  CheckCircle2, 
  ArrowUpRight,
  Pin,
  Clock
} from 'lucide-react';
import { useApp } from '../context/AppContext';

export const HomeScreen: React.FC = () => {
  const { 
    settings, 
    setActiveTab, 
    notes, 
    todos, 
    reminders, 
    streak, 
    folders,
    setEditingNote,
    setIsNoteEditorOpen,
    setIsStreakModalOpen,
    toggleTodo,
    toggleReminder
  } = useApp();

  // Get current time greeting
  const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good morning';
    if (hour < 17) return 'Good afternoon';
    return 'Good evening';
  };

  const todayStr = new Date().toISOString().split('T')[0];
  const todayTodos = todos.filter(t => t.dueDate === todayStr || !t.dueDate);
  const completedToday = todayTodos.filter(t => t.isCompleted).length;
  const progressPercent = todayTodos.length > 0 ? Math.round((completedToday / todayTodos.length) * 100) : 0;

  // Next reminder
  const nextReminder = reminders.find(r => !r.isCompleted);

  // Recent notes (limit to 3)
  const recentNotes = [...notes].sort((a, b) => b.updatedAt - a.updatedAt).slice(0, 3);

  const getFolder = (folderId?: string) => folders.find(f => f.id === folderId);

  return (
    <div className="space-y-6 pb-24 max-w-4xl mx-auto px-4 sm:px-6 pt-4 animate-fade-in">
      {/* Greeting Header */}
      <div>
        <h2 className="text-2xl sm:text-3xl font-bold tracking-tight text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
          {getGreeting()}, {settings.userName}
        </h2>
        <p className="text-sm text-sivo-text-secondary dark:text-sivo-dark-text-secondary mt-0.5">
          Let's get things done today.
        </p>
      </div>

      {/* Global Search Quick Trigger */}
      <div 
        onClick={() => setActiveTab('search')}
        className="flex items-center gap-3 px-4 py-3.5 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm cursor-pointer hover:border-sivo-primary/50 transition-all group"
      >
        <Search className="w-4 h-4 text-sivo-text-muted group-hover:text-sivo-primary transition-colors" />
        <span className="text-sm text-sivo-text-muted">
          Search notes, todos, points, reminders...
        </span>
      </div>

      {/* Quick Action Pills */}
      <div>
        <div className="flex items-center gap-2 overflow-x-auto pb-1 no-scrollbar">
          <button
            onClick={() => {
              setEditingNote(null);
              setIsNoteEditorOpen(true);
            }}
            className="flex items-center gap-2 px-4 py-2.5 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm hover:border-sivo-primary/60 text-xs font-semibold text-sivo-text-primary dark:text-sivo-dark-text-primary hover:scale-[1.02] active:scale-[0.98] transition-all whitespace-nowrap"
          >
            <div className="p-1 rounded-lg bg-pastel-lavender-bg text-pastel-lavender">
              <FileText className="w-3.5 h-3.5" />
            </div>
            <span>Quick Note</span>
          </button>

          <button
            onClick={() => setActiveTab('todos')}
            className="flex items-center gap-2 px-4 py-2.5 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm hover:border-pastel-mint/60 text-xs font-semibold text-sivo-text-primary dark:text-sivo-dark-text-primary hover:scale-[1.02] active:scale-[0.98] transition-all whitespace-nowrap"
          >
            <div className="p-1 rounded-lg bg-pastel-mint-bg text-pastel-mint">
              <CheckSquare className="w-3.5 h-3.5" />
            </div>
            <span>Today's Todos</span>
          </button>

          <button
            onClick={() => setActiveTab('points')}
            className="flex items-center gap-2 px-4 py-2.5 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm hover:border-pastel-yellow/60 text-xs font-semibold text-sivo-text-primary dark:text-sivo-dark-text-primary hover:scale-[1.02] active:scale-[0.98] transition-all whitespace-nowrap"
          >
            <div className="p-1 rounded-lg bg-pastel-yellow-bg text-pastel-yellow">
              <Star className="w-3.5 h-3.5" />
            </div>
            <span>Important Points</span>
          </button>

          <button
            onClick={() => setActiveTab('reminders')}
            className="flex items-center gap-2 px-4 py-2.5 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm hover:border-pastel-coral/60 text-xs font-semibold text-sivo-text-primary dark:text-sivo-dark-text-primary hover:scale-[1.02] active:scale-[0.98] transition-all whitespace-nowrap"
          >
            <div className="p-1 rounded-lg bg-pastel-coral-bg text-pastel-coral">
              <Bell className="w-3.5 h-3.5" />
            </div>
            <span>Reminders</span>
          </button>
        </div>
      </div>

      {/* Today's Progress & Streak Section */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        {/* Progress Card */}
        <div 
          onClick={() => setActiveTab('todos')}
          className="sm:col-span-2 p-5 rounded-3xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm hover:shadow-sivo-md transition-all cursor-pointer group"
        >
          <div className="flex items-center justify-between mb-3">
            <span className="text-xs font-bold uppercase tracking-wider text-sivo-text-secondary dark:text-sivo-dark-text-secondary">
              Today's Progress
            </span>
            <span className="text-xs font-bold text-sivo-primary flex items-center gap-1 group-hover:translate-x-0.5 transition-transform">
              View all <ChevronRight className="w-3.5 h-3.5" />
            </span>
          </div>

          <div className="flex items-end justify-between mb-2">
            <div>
              <span className="text-2xl font-extrabold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
                {completedToday} of {todayTodos.length}
              </span>
              <span className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary ml-1.5 font-medium">
                completed
              </span>
            </div>
            <span className="text-sm font-bold text-sivo-primary">
              {progressPercent}%
            </span>
          </div>

          <div className="w-full bg-sivo-surface-variant dark:bg-sivo-dark-surface-variant rounded-full h-2.5 overflow-hidden">
            <div 
              className="bg-gradient-to-r from-sivo-primary to-pastel-mint h-2.5 rounded-full transition-all duration-500"
              style={{ width: `${progressPercent}%` }}
            />
          </div>

          {/* Quick Todo items snapshot */}
          <div className="mt-4 space-y-2">
            {todayTodos.slice(0, 2).map((todo) => (
              <div 
                key={todo.id}
                onClick={(e) => {
                  e.stopPropagation();
                  toggleTodo(todo.id);
                }}
                className="flex items-center gap-2.5 p-2 rounded-xl hover:bg-sivo-surface-variant/60 dark:hover:bg-sivo-dark-surface-variant/60 transition-colors"
              >
                <div className={`w-4 h-4 rounded-md border flex items-center justify-center transition-all ${
                  todo.isCompleted 
                    ? 'bg-pastel-mint border-pastel-mint text-white' 
                    : 'border-sivo-border dark:border-sivo-dark-border bg-white dark:bg-sivo-dark-surface'
                }`}>
                  {todo.isCompleted && <CheckCircle2 className="w-3 h-3" />}
                </div>
                <span className={`text-xs font-medium flex-1 truncate ${
                  todo.isCompleted 
                    ? 'line-through text-sivo-text-muted dark:text-sivo-dark-text-muted' 
                    : 'text-sivo-text-primary dark:text-sivo-dark-text-primary'
                }`}>
                  {todo.title}
                </span>
                {todo.dueTime && (
                  <span className="text-[10px] text-sivo-text-muted">
                    {todo.dueTime}
                  </span>
                )}
              </div>
            ))}
          </div>
        </div>

        {/* Streak Pill Card */}
        <div 
          onClick={() => setIsStreakModalOpen(true)}
          className="p-5 rounded-3xl bg-gradient-to-br from-amber-500/10 via-orange-500/5 to-transparent border border-amber-500/20 shadow-sivo-sm hover:scale-[1.02] active:scale-[0.98] transition-all cursor-pointer flex flex-col justify-between"
        >
          <div className="flex items-center justify-between">
            <div className="p-2.5 rounded-2xl bg-amber-500/20 text-amber-500">
              <Flame className="w-5 h-5 fill-current animate-pulse" />
            </div>
            <ArrowUpRight className="w-4 h-4 text-amber-600/70" />
          </div>

          <div className="my-2">
            <div className="flex items-baseline gap-1.5">
              <span className="text-3xl font-black text-amber-500 font-['Outfit']">
                {streak.currentStreak}
              </span>
              <span className="text-xs font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary">
                Day Streak
              </span>
            </div>
            <p className="text-[11px] text-sivo-text-secondary dark:text-sivo-dark-text-secondary mt-0.5">
              Best record: {streak.bestStreak} days
            </p>
          </div>

          <div className="text-[11px] font-semibold text-amber-600 dark:text-amber-400 bg-amber-500/10 py-1 px-2.5 rounded-full text-center">
            🔥 Keep it going!
          </div>
        </div>
      </div>

      {/* Next Reminder Card */}
      {nextReminder && (
        <div className="p-4 sm:p-5 rounded-3xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm flex items-center justify-between gap-4">
          <div className="flex items-center gap-3.5">
            <div className="p-3 rounded-2xl bg-pastel-coral-bg text-pastel-coral">
              <Bell className="w-5 h-5" />
            </div>
            <div>
              <span className="text-[10px] font-bold uppercase tracking-wider text-pastel-coral block">
                Next Reminder
              </span>
              <h4 className="text-sm font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary">
                {nextReminder.title}
              </h4>
              <span className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary flex items-center gap-1 mt-0.5">
                <Clock className="w-3 h-3" />
                {nextReminder.date === todayStr ? 'Today' : nextReminder.date} at {nextReminder.time}
              </span>
            </div>
          </div>

          <button
            onClick={() => toggleReminder(nextReminder.id)}
            className="px-3.5 py-2 rounded-xl text-xs font-semibold bg-pastel-mint-bg text-pastel-mint hover:bg-pastel-mint hover:text-white transition-all whitespace-nowrap"
          >
            Mark Done
          </button>
        </div>
      )}

      {/* Recent Notes Section */}
      <div>
        <div className="flex items-center justify-between mb-3">
          <h3 className="text-base font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
            Recent Notes
          </h3>
          <button
            onClick={() => setActiveTab('notes')}
            className="text-xs font-semibold text-sivo-primary hover:underline flex items-center gap-1"
          >
            All Notes ({notes.length}) <ChevronRight className="w-3.5 h-3.5" />
          </button>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
          {recentNotes.map((note) => {
            const folder = getFolder(note.folderId);
            return (
              <div
                key={note.id}
                onClick={() => {
                  setEditingNote(note);
                  setIsNoteEditorOpen(true);
                }}
                className="p-4 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm hover:shadow-sivo-md hover:border-sivo-primary/50 transition-all cursor-pointer flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-start justify-between gap-2 mb-1.5">
                    <h4 className="text-sm font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary line-clamp-1">
                      {note.title}
                    </h4>
                    {note.isPinned && (
                      <Pin className="w-3 h-3 text-sivo-primary shrink-0 rotate-45" />
                    )}
                  </div>
                  <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary line-clamp-2 leading-relaxed">
                    {note.content.replace(/[#*`_]/g, '') || 'No content...'}
                  </p>
                </div>

                <div className="flex items-center justify-between mt-3 pt-2 border-t border-sivo-border/40 dark:border-sivo-dark-border/40 text-[10px] text-sivo-text-muted">
                  {folder ? (
                    <span className="px-2 py-0.5 rounded-full bg-sivo-primary-container/60 text-sivo-on-primary-container dark:bg-sivo-primary/20 dark:text-sivo-primary-light font-medium">
                      {folder.name}
                    </span>
                  ) : (
                    <span>General</span>
                  )}
                  <span>
                    {new Date(note.updatedAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric' })}
                  </span>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Vault Shortcut Banner */}
      <div 
        onClick={() => setActiveTab('vault')}
        className="p-4 sm:p-5 rounded-3xl bg-gradient-to-r from-sivo-primary/15 via-purple-600/10 to-sivo-primary/5 border border-sivo-primary/30 shadow-sivo-sm hover:shadow-sivo-md transition-all cursor-pointer flex items-center justify-between"
      >
        <div className="flex items-center gap-3.5">
          <div className="p-3 rounded-2xl bg-sivo-primary text-white shadow-md shadow-sivo-primary/30">
            <Lock className="w-5 h-5" />
          </div>
          <div>
            <h4 className="text-sm font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary flex items-center gap-1.5 font-['Outfit']">
              Private Vault
              <span className="text-[10px] bg-sivo-primary text-white px-2 py-0.5 rounded-full font-semibold">
                AES-GCM
              </span>
            </h4>
            <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary">
              Store encrypted passwords and private notes locally.
            </p>
          </div>
        </div>

        <ChevronRight className="w-5 h-5 text-sivo-primary" />
      </div>
    </div>
  );
};
