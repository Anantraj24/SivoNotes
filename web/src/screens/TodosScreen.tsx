import React, { useState } from 'react';
import { 
  Plus, 
  CheckCircle2, 
  Circle, 
  Calendar, 
  Clock, 
  Repeat, 
  Trash2, 
  Flame, 
  Folder as FolderIcon,
  CheckSquare
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { TodoPriority } from '../types';

export const TodosScreen: React.FC = () => {
  const { 
    todos, 
    toggleTodo, 
    deleteTodo, 
    setIsTodoModalOpen, 
    streak, 
    setIsStreakModalOpen,
    folders 
  } = useApp();

  const [activeTab, setActiveTab] = useState<'today' | 'upcoming' | 'completed'>('today');
  const todayStr = new Date().toISOString().split('T')[0];

  const todayTodos = todos.filter(t => (t.dueDate === todayStr || !t.dueDate) && !t.isCompleted);
  const upcomingTodos = todos.filter(t => t.dueDate && t.dueDate > todayStr && !t.isCompleted);
  const completedTodos = todos.filter(t => t.isCompleted);

  const displayedTodos = 
    activeTab === 'today' ? todayTodos :
    activeTab === 'upcoming' ? upcomingTodos :
    completedTodos;

  const totalTasks = todos.length;
  const totalCompleted = completedTodos.length;
  const rate = totalTasks > 0 ? Math.round((totalCompleted / totalTasks) * 100) : 0;

  const getPriorityStyle = (priority: TodoPriority) => {
    switch (priority) {
      case 'high':
        return 'bg-pastel-coral-bg text-pastel-coral border-pastel-coral/30';
      case 'medium':
        return 'bg-pastel-amber-bg text-pastel-amber border-pastel-amber/30';
      case 'low':
      default:
        return 'bg-pastel-sky-bg text-pastel-sky border-pastel-sky/30';
    }
  };

  const getFolder = (folderId?: string) => folders.find(f => f.id === folderId);

  return (
    <div className="space-y-5 pb-24 max-w-4xl mx-auto px-4 sm:px-6 pt-4 animate-fade-in">
      {/* Top Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold tracking-tight text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
            Daily Todos
          </h2>
          <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary">
            {todayTodos.length} tasks remaining for today
          </p>
        </div>

        <button
          onClick={() => setIsTodoModalOpen(true)}
          className="flex items-center gap-1.5 px-4 py-2 rounded-full bg-sivo-primary hover:bg-sivo-primary-variant text-white text-xs font-semibold shadow-md shadow-sivo-primary/30 transition-all"
        >
          <Plus className="w-4 h-4" />
          Add Todo
        </button>
      </div>

      {/* Streak & Progress Banner */}
      <div 
        onClick={() => setIsStreakModalOpen(true)}
        className="p-4 sm:p-5 rounded-3xl bg-gradient-to-r from-amber-500/10 via-orange-500/10 to-sivo-primary/10 border border-amber-500/25 shadow-sivo-sm hover:shadow-sivo-md transition-all cursor-pointer flex items-center justify-between"
      >
        <div className="flex items-center gap-3">
          <div className="p-3 rounded-2xl bg-amber-500/20 text-amber-500">
            <Flame className="w-6 h-6 fill-current animate-pulse" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-lg font-extrabold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
                {streak.currentStreak} Day Streak
              </span>
              <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-amber-500 text-white">
                Best: {streak.bestStreak}d
              </span>
            </div>
            <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary mt-0.5">
              {totalCompleted} of {totalTasks} completed overall ({rate}%)
            </p>
          </div>
        </div>

        <span className="text-xs font-bold text-sivo-primary hidden sm:inline-block">
          View analytics →
        </span>
      </div>

      {/* Tab Switcher */}
      <div className="flex items-center gap-1 bg-sivo-surface-variant/80 dark:bg-sivo-dark-surface-variant/80 p-1 rounded-2xl w-fit">
        <button
          onClick={() => setActiveTab('today')}
          className={`px-4 py-2 rounded-xl text-xs font-semibold transition-all ${
            activeTab === 'today'
              ? 'bg-white dark:bg-sivo-dark-surface text-sivo-primary shadow-sivo-sm'
              : 'text-sivo-text-secondary hover:text-sivo-text-primary'
          }`}
        >
          Today ({todayTodos.length})
        </button>

        <button
          onClick={() => setActiveTab('upcoming')}
          className={`px-4 py-2 rounded-xl text-xs font-semibold transition-all ${
            activeTab === 'upcoming'
              ? 'bg-white dark:bg-sivo-dark-surface text-sivo-primary shadow-sivo-sm'
              : 'text-sivo-text-secondary hover:text-sivo-text-primary'
          }`}
        >
          Upcoming ({upcomingTodos.length})
        </button>

        <button
          onClick={() => setActiveTab('completed')}
          className={`px-4 py-2 rounded-xl text-xs font-semibold transition-all ${
            activeTab === 'completed'
              ? 'bg-white dark:bg-sivo-dark-surface text-sivo-primary shadow-sivo-sm'
              : 'text-sivo-text-secondary hover:text-sivo-text-primary'
          }`}
        >
          Completed ({completedTodos.length})
        </button>
      </div>

      {/* Todo List */}
      <div className="space-y-2.5">
        {displayedTodos.length === 0 ? (
          <div className="p-12 text-center rounded-3xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm my-4">
            <div className="w-12 h-12 rounded-2xl bg-pastel-mint-bg text-pastel-mint flex items-center justify-center mx-auto mb-3">
              <CheckSquare className="w-6 h-6" />
            </div>
            <h3 className="text-base font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
              {activeTab === 'completed' ? 'No completed tasks yet' : "You're all caught up!"}
            </h3>
            <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary mt-1 mb-4">
              {activeTab === 'completed' ? 'Finish tasks to build your streak.' : 'Add a task for today to maintain momentum.'}
            </p>
            {activeTab !== 'completed' && (
              <button
                onClick={() => setIsTodoModalOpen(true)}
                className="px-4 py-2 rounded-full bg-sivo-primary text-white text-xs font-semibold shadow-md"
              >
                Add Todo
              </button>
            )}
          </div>
        ) : (
          displayedTodos.map((todo) => {
            const folder = getFolder(todo.folderId);
            return (
              <div
                key={todo.id}
                onClick={() => toggleTodo(todo.id)}
                className={`p-4 rounded-2xl bg-white dark:bg-sivo-dark-surface border transition-all cursor-pointer flex items-start justify-between gap-3 group hover:shadow-sivo-md ${
                  todo.isCompleted 
                    ? 'border-sivo-border/40 dark:border-sivo-dark-border/40 opacity-70 bg-sivo-surface-variant/30' 
                    : 'border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm hover:border-sivo-primary/50'
                }`}
              >
                <div className="flex items-start gap-3 flex-1">
                  {/* Checkbox */}
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      toggleTodo(todo.id);
                    }}
                    className={`mt-0.5 w-5 h-5 rounded-lg border flex items-center justify-center transition-all ${
                      todo.isCompleted
                        ? 'bg-pastel-mint border-pastel-mint text-white'
                        : 'border-sivo-border dark:border-sivo-dark-border bg-white dark:bg-sivo-dark-surface group-hover:border-sivo-primary'
                    }`}
                  >
                    {todo.isCompleted ? (
                      <CheckCircle2 className="w-3.5 h-3.5" />
                    ) : (
                      <Circle className="w-3.5 h-3.5 opacity-0 group-hover:opacity-40" />
                    )}
                  </button>

                  {/* Todo Details */}
                  <div className="space-y-1 flex-1">
                    <h4 className={`text-sm font-semibold transition-all ${
                      todo.isCompleted 
                        ? 'line-through text-sivo-text-muted dark:text-sivo-dark-text-muted' 
                        : 'text-sivo-text-primary dark:text-sivo-dark-text-primary'
                    }`}>
                      {todo.title}
                    </h4>

                    {todo.description && (
                      <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary line-clamp-2">
                        {todo.description}
                      </p>
                    )}

                    {/* Metadata Badges */}
                    <div className="flex flex-wrap items-center gap-2 pt-1 text-[10px]">
                      {/* Priority Badge */}
                      <span className={`px-2 py-0.5 rounded-full border font-semibold uppercase tracking-wider ${getPriorityStyle(todo.priority)}`}>
                        {todo.priority}
                      </span>

                      {/* Due date / Time */}
                      {todo.dueDate && (
                        <span className="flex items-center gap-1 text-sivo-text-muted">
                          <Calendar className="w-3 h-3" />
                          {todo.dueDate === todayStr ? 'Today' : todo.dueDate}
                        </span>
                      )}

                      {todo.dueTime && (
                        <span className="flex items-center gap-1 text-sivo-text-muted">
                          <Clock className="w-3 h-3" />
                          {todo.dueTime}
                        </span>
                      )}

                      {/* Repeat badge */}
                      {todo.repeat !== 'none' && (
                        <span className="flex items-center gap-1 text-sivo-primary font-medium bg-sivo-primary-container px-2 py-0.5 rounded-full">
                          <Repeat className="w-3 h-3" />
                          {todo.repeat}
                        </span>
                      )}

                      {/* Folder */}
                      {folder && (
                        <span className="flex items-center gap-1 text-sivo-text-muted">
                          <FolderIcon className="w-3 h-3" />
                          {folder.name}
                        </span>
                      )}
                    </div>
                  </div>
                </div>

                {/* Delete button */}
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    deleteTodo(todo.id);
                  }}
                  className="p-1.5 rounded-lg text-sivo-text-muted hover:text-sivo-error hover:bg-sivo-error-container/60 transition-all opacity-0 group-hover:opacity-100"
                  title="Delete Todo"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};
