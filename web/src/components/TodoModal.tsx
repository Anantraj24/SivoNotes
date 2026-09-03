import React, { useState } from 'react';
import { X, Calendar, Clock, Repeat, Flag, Folder as FolderIcon, Plus } from 'lucide-react';
import { useApp } from '../context/AppContext';
import { TodoPriority, TodoRepeat } from '../types';

export const TodoModal: React.FC = () => {
  const { isTodoModalOpen, setIsTodoModalOpen, addTodo, folders, selectedFolderId } = useApp();

  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [dueDate, setDueDate] = useState(new Date().toISOString().split('T')[0]);
  const [dueTime, setDueTime] = useState('18:00');
  const [priority, setPriority] = useState<TodoPriority>('medium');
  const [repeat, setRepeat] = useState<TodoRepeat>('none');
  const [folderId, setFolderId] = useState(selectedFolderId || '');

  if (!isTodoModalOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) return;

    addTodo({
      title: title.trim(),
      description: description.trim() || undefined,
      dueDate,
      dueTime,
      priority,
      repeat,
      folderId: folderId || undefined,
    });

    // Reset & close
    setTitle('');
    setDescription('');
    setIsTodoModalOpen(false);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 bg-black/50 backdrop-blur-sm animate-fade-in">
      <div className="w-full max-w-lg bg-white dark:bg-sivo-dark-surface rounded-3xl border border-sivo-border dark:border-sivo-dark-border shadow-2xl p-6 animate-slide-up">
        
        <div className="flex items-center justify-between pb-4 border-b border-sivo-border/60 dark:border-sivo-dark-border/60">
          <h3 className="text-lg font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
            Add Daily Todo
          </h3>
          <button
            onClick={() => setIsTodoModalOpen(false)}
            className="p-2 rounded-full hover:bg-sivo-surface-variant dark:hover:bg-sivo-dark-surface-variant text-sivo-text-secondary hover:text-sivo-text-primary"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="mt-4 space-y-4">
          <div>
            <label className="block text-xs font-semibold text-sivo-text-secondary dark:text-sivo-dark-text-secondary mb-1">
              Task Title *
            </label>
            <input
              type="text"
              placeholder="e.g. Complete DSA questions..."
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full px-4 py-2.5 rounded-2xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border dark:border-sivo-dark-border text-sm outline-none focus:border-sivo-primary text-sivo-text-primary dark:text-sivo-dark-text-primary"
              autoFocus
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-sivo-text-secondary dark:text-sivo-dark-text-secondary mb-1">
              Description (Optional)
            </label>
            <textarea
              placeholder="Add details, notes, or steps..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={2}
              className="w-full px-4 py-2 rounded-2xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border dark:border-sivo-dark-border text-xs outline-none focus:border-sivo-primary text-sivo-text-primary dark:text-sivo-dark-text-primary resize-none"
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-sivo-text-secondary dark:text-sivo-dark-text-secondary mb-1 flex items-center gap-1">
                <Calendar className="w-3.5 h-3.5 text-sivo-primary" />
                Date
              </label>
              <input
                type="date"
                value={dueDate}
                onChange={(e) => setDueDate(e.target.value)}
                className="w-full px-3 py-2 rounded-xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border dark:border-sivo-dark-border text-xs text-sivo-text-primary dark:text-sivo-dark-text-primary outline-none focus:border-sivo-primary"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-sivo-text-secondary dark:text-sivo-dark-text-secondary mb-1 flex items-center gap-1">
                <Clock className="w-3.5 h-3.5 text-sivo-primary" />
                Time
              </label>
              <input
                type="time"
                value={dueTime}
                onChange={(e) => setDueTime(e.target.value)}
                className="w-full px-3 py-2 rounded-xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border dark:border-sivo-dark-border text-xs text-sivo-text-primary dark:text-sivo-dark-text-primary outline-none focus:border-sivo-primary"
              />
            </div>
          </div>

          {/* Repeat and Priority */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-sivo-text-secondary dark:text-sivo-dark-text-secondary mb-1 flex items-center gap-1">
                <Repeat className="w-3.5 h-3.5 text-sivo-primary" />
                Repeat
              </label>
              <select
                value={repeat}
                onChange={(e) => setRepeat(e.target.value as TodoRepeat)}
                className="w-full px-3 py-2 rounded-xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border dark:border-sivo-dark-border text-xs text-sivo-text-primary dark:text-sivo-dark-text-primary outline-none focus:border-sivo-primary cursor-pointer"
              >
                <option value="none">Once (No repeat)</option>
                <option value="daily">Daily</option>
                <option value="weekdays">Weekdays (Mon-Fri)</option>
                <option value="weekly">Weekly</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-sivo-text-secondary dark:text-sivo-dark-text-secondary mb-1 flex items-center gap-1">
                <Flag className="w-3.5 h-3.5 text-sivo-primary" />
                Priority
              </label>
              <select
                value={priority}
                onChange={(e) => setPriority(e.target.value as TodoPriority)}
                className="w-full px-3 py-2 rounded-xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border dark:border-sivo-dark-border text-xs text-sivo-text-primary dark:text-sivo-dark-text-primary outline-none focus:border-sivo-primary cursor-pointer"
              >
                <option value="low">Low Priority</option>
                <option value="medium">Medium Priority</option>
                <option value="high">High Priority</option>
              </select>
            </div>
          </div>

          {/* Folder */}
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
              onClick={() => setIsTodoModalOpen(false)}
              className="px-4 py-2 rounded-full text-xs font-semibold text-sivo-text-secondary hover:bg-sivo-surface-variant transition-all"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="flex items-center gap-1.5 px-5 py-2.5 rounded-full bg-sivo-primary hover:bg-sivo-primary-variant text-white text-xs font-semibold shadow-md shadow-sivo-primary/30 transition-all"
            >
              <Plus className="w-4 h-4" />
              Add Task
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
