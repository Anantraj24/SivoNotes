import React, { useState } from 'react';
import { X, Calendar, Clock, Repeat, Bell, Plus } from 'lucide-react';
import { useApp } from '../context/AppContext';
import { ReminderRepeat } from '../types';

export const ReminderModal: React.FC = () => {
  const { isReminderModalOpen, setIsReminderModalOpen, addReminder } = useApp();

  const [title, setTitle] = useState('');
  const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
  const [time, setTime] = useState('10:00');
  const [repeat, setRepeat] = useState<ReminderRepeat>('none');
  const [note, setNote] = useState('');

  if (!isReminderModalOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) return;

    addReminder({
      title: title.trim(),
      date,
      time,
      repeat,
      note: note.trim() || undefined,
    });

    setTitle('');
    setNote('');
    setIsReminderModalOpen(false);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 bg-black/50 backdrop-blur-sm animate-fade-in">
      <div className="w-full max-w-lg bg-white dark:bg-sivo-dark-surface rounded-3xl border border-sivo-border dark:border-sivo-dark-border shadow-2xl p-6 animate-slide-up">
        
        <div className="flex items-center justify-between pb-4 border-b border-sivo-border/60 dark:border-sivo-dark-border/60">
          <h3 className="text-lg font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
            Set Reminder
          </h3>
          <button
            onClick={() => setIsReminderModalOpen(false)}
            className="p-2 rounded-full hover:bg-sivo-surface-variant dark:hover:bg-sivo-dark-surface-variant text-sivo-text-secondary hover:text-sivo-text-primary"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="mt-4 space-y-4">
          <div>
            <label className="block text-xs font-semibold text-sivo-text-secondary dark:text-sivo-dark-text-secondary mb-1">
              Reminder Title *
            </label>
            <input
              type="text"
              placeholder="e.g. Submit college assignment..."
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full px-4 py-2.5 rounded-2xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border dark:border-sivo-dark-border text-sm outline-none focus:border-sivo-primary text-sivo-text-primary dark:text-sivo-dark-text-primary"
              autoFocus
              required
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
                value={date}
                onChange={(e) => setDate(e.target.value)}
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
                value={time}
                onChange={(e) => setTime(e.target.value)}
                className="w-full px-3 py-2 rounded-xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border dark:border-sivo-dark-border text-xs text-sivo-text-primary dark:text-sivo-dark-text-primary outline-none focus:border-sivo-primary"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-sivo-text-secondary dark:text-sivo-dark-text-secondary mb-1 flex items-center gap-1">
              <Repeat className="w-3.5 h-3.5 text-sivo-primary" />
              Repeat Interval
            </label>
            <select
              value={repeat}
              onChange={(e) => setRepeat(e.target.value as ReminderRepeat)}
              className="w-full px-3 py-2 rounded-xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border dark:border-sivo-dark-border text-xs text-sivo-text-primary dark:text-sivo-dark-text-primary outline-none focus:border-sivo-primary cursor-pointer"
            >
              <option value="none">Once</option>
              <option value="daily">Daily</option>
              <option value="weekly">Weekly</option>
              <option value="monthly">Monthly</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-sivo-text-secondary dark:text-sivo-dark-text-secondary mb-1">
              Additional Note (Optional)
            </label>
            <input
              type="text"
              placeholder="e.g. Upload PDF to portal"
              value={note}
              onChange={(e) => setNote(e.target.value)}
              className="w-full px-4 py-2 rounded-2xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border dark:border-sivo-dark-border text-xs outline-none focus:border-sivo-primary text-sivo-text-primary dark:text-sivo-dark-text-primary"
            />
          </div>

          <div className="pt-3 flex justify-end gap-2">
            <button
              type="button"
              onClick={() => setIsReminderModalOpen(false)}
              className="px-4 py-2 rounded-full text-xs font-semibold text-sivo-text-secondary hover:bg-sivo-surface-variant transition-all"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="flex items-center gap-1.5 px-5 py-2.5 rounded-full bg-sivo-primary hover:bg-sivo-primary-variant text-white text-xs font-semibold shadow-md shadow-sivo-primary/30 transition-all"
            >
              <Bell className="w-4 h-4" />
              Set Reminder
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
