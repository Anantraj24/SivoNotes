import React, { useState } from 'react';
import { Bell, Plus, Clock, Calendar, Repeat, CheckCircle2, Circle, Trash2, ArrowRight } from 'lucide-react';
import { useApp } from '../context/AppContext';
import { Reminder } from '../types';

export const RemindersScreen: React.FC = () => {
  const { reminders, toggleReminder, snoozeReminder, deleteReminder, setIsReminderModalOpen } = useApp();

  const [activeFilter, setActiveFilter] = useState<'active' | 'completed'>('active');

  const todayStr = new Date().toISOString().split('T')[0];
  const tomorrowStr = new Date(Date.now() + 86400000).toISOString().split('T')[0];

  const activeReminders = reminders.filter(r => !r.isCompleted);
  const completedReminders = reminders.filter(r => r.isCompleted);

  const todayGroup = activeReminders.filter(r => r.date <= todayStr);
  const tomorrowGroup = activeReminders.filter(r => r.date === tomorrowStr);
  const upcomingGroup = activeReminders.filter(r => r.date > tomorrowStr);

  const renderReminderCard = (reminder: Reminder) => (
    <div
      key={reminder.id}
      className={`p-4 rounded-2xl bg-white dark:bg-sivo-dark-surface border transition-all flex items-start justify-between gap-3 group hover:shadow-sivo-md ${
        reminder.isCompleted
          ? 'border-sivo-border/40 dark:border-sivo-dark-border/40 opacity-70'
          : 'border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm hover:border-pastel-coral/50'
      }`}
    >
      <div className="flex items-start gap-3 flex-1">
        <button
          onClick={() => toggleReminder(reminder.id)}
          className={`mt-0.5 w-5 h-5 rounded-lg border flex items-center justify-center transition-all ${
            reminder.isCompleted
              ? 'bg-pastel-mint border-pastel-mint text-white'
              : 'border-sivo-border dark:border-sivo-dark-border bg-white dark:bg-sivo-dark-surface group-hover:border-pastel-coral'
          }`}
        >
          {reminder.isCompleted ? (
            <CheckCircle2 className="w-3.5 h-3.5" />
          ) : (
            <Circle className="w-3.5 h-3.5 opacity-0 group-hover:opacity-40" />
          )}
        </button>

        <div className="space-y-1 flex-1">
          <h4 className={`text-sm font-semibold ${
            reminder.isCompleted ? 'line-through text-sivo-text-muted' : 'text-sivo-text-primary dark:text-sivo-dark-text-primary'
          }`}>
            {reminder.title}
          </h4>

          {reminder.note && (
            <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary">
              {reminder.note}
            </p>
          )}

          <div className="flex flex-wrap items-center gap-2 pt-1 text-[10px]">
            <span className="flex items-center gap-1 text-pastel-coral font-medium bg-pastel-coral-bg px-2 py-0.5 rounded-full border border-pastel-coral/30">
              <Clock className="w-3 h-3" />
              {reminder.time}
            </span>

            <span className="flex items-center gap-1 text-sivo-text-muted">
              <Calendar className="w-3 h-3" />
              {reminder.date === todayStr ? 'Today' : reminder.date === tomorrowStr ? 'Tomorrow' : reminder.date}
            </span>

            {reminder.repeat !== 'none' && (
              <span className="flex items-center gap-1 text-sivo-primary font-medium bg-sivo-primary-container px-2 py-0.5 rounded-full">
                <Repeat className="w-3 h-3" />
                {reminder.repeat}
              </span>
            )}
          </div>
        </div>
      </div>

      <div className="flex items-center gap-1">
        {!reminder.isCompleted && (
          <button
            onClick={() => snoozeReminder(reminder.id, 15)}
            className="px-2.5 py-1 rounded-xl text-[10px] font-semibold bg-sivo-surface-variant hover:bg-sivo-primary-container hover:text-sivo-primary text-sivo-text-secondary transition-all"
            title="Snooze 15 minutes"
          >
            +15m
          </button>
        )}
        <button
          onClick={() => deleteReminder(reminder.id)}
          className="p-1.5 rounded-lg text-sivo-text-muted hover:text-sivo-error hover:bg-sivo-error-container/60 transition-all opacity-0 group-hover:opacity-100"
          title="Delete Reminder"
        >
          <Trash2 className="w-4 h-4" />
        </button>
      </div>
    </div>
  );

  return (
    <div className="space-y-5 pb-24 max-w-4xl mx-auto px-4 sm:px-6 pt-4 animate-fade-in">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2.5">
          <div className="p-2 rounded-2xl bg-pastel-coral-bg text-pastel-coral">
            <Bell className="w-5 h-5" />
          </div>
          <div>
            <h2 className="text-2xl font-bold tracking-tight text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
              Reminders
            </h2>
            <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary">
              Local offline alerts & schedule
            </p>
          </div>
        </div>

        <button
          onClick={() => setIsReminderModalOpen(true)}
          className="flex items-center gap-1.5 px-4 py-2 rounded-full bg-pastel-coral hover:bg-rose-600 text-white text-xs font-semibold shadow-md transition-all"
        >
          <Plus className="w-4 h-4" />
          Set Reminder
        </button>
      </div>

      {/* Filter Tabs */}
      <div className="flex items-center gap-1 bg-sivo-surface-variant/80 dark:bg-sivo-dark-surface-variant/80 p-1 rounded-2xl w-fit">
        <button
          onClick={() => setActiveFilter('active')}
          className={`px-4 py-1.5 rounded-xl text-xs font-semibold transition-all ${
            activeFilter === 'active'
              ? 'bg-white dark:bg-sivo-dark-surface text-pastel-coral shadow-sivo-sm'
              : 'text-sivo-text-secondary hover:text-sivo-text-primary'
          }`}
        >
          Upcoming ({activeReminders.length})
        </button>
        <button
          onClick={() => setActiveFilter('completed')}
          className={`px-4 py-1.5 rounded-xl text-xs font-semibold transition-all ${
            activeFilter === 'completed'
              ? 'bg-white dark:bg-sivo-dark-surface text-pastel-coral shadow-sivo-sm'
              : 'text-sivo-text-secondary hover:text-sivo-text-primary'
          }`}
        >
          Done ({completedReminders.length})
        </button>
      </div>

      {/* Reminders List */}
      {activeFilter === 'active' ? (
        activeReminders.length === 0 ? (
          <div className="p-12 text-center rounded-3xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm my-4">
            <div className="w-12 h-12 rounded-2xl bg-pastel-coral-bg text-pastel-coral flex items-center justify-center mx-auto mb-3">
              <Bell className="w-6 h-6" />
            </div>
            <h3 className="text-base font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
              No upcoming reminders
            </h3>
            <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary mt-1 mb-4">
              Never forget deadlines, assignments, or daily routines.
            </p>
            <button
              onClick={() => setIsReminderModalOpen(true)}
              className="px-4 py-2 rounded-full bg-pastel-coral text-white text-xs font-semibold shadow-md"
            >
              Set Reminder
            </button>
          </div>
        ) : (
          <div className="space-y-6">
            {/* Today Group */}
            {todayGroup.length > 0 && (
              <div>
                <span className="text-xs font-bold uppercase tracking-wider text-pastel-coral mb-2.5 block">
                  Today
                </span>
                <div className="space-y-2.5">
                  {todayGroup.map(renderReminderCard)}
                </div>
              </div>
            )}

            {/* Tomorrow Group */}
            {tomorrowGroup.length > 0 && (
              <div>
                <span className="text-xs font-bold uppercase tracking-wider text-sivo-text-secondary dark:text-sivo-dark-text-secondary mb-2.5 block">
                  Tomorrow
                </span>
                <div className="space-y-2.5">
                  {tomorrowGroup.map(renderReminderCard)}
                </div>
              </div>
            )}

            {/* Upcoming Group */}
            {upcomingGroup.length > 0 && (
              <div>
                <span className="text-xs font-bold uppercase tracking-wider text-sivo-text-muted mb-2.5 block">
                  Later This Week
                </span>
                <div className="space-y-2.5">
                  {upcomingGroup.map(renderReminderCard)}
                </div>
              </div>
            )}
          </div>
        )
      ) : (
        <div className="space-y-2.5">
          {completedReminders.length === 0 ? (
            <div className="p-12 text-center rounded-3xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm my-4">
              <p className="text-xs text-sivo-text-muted">No completed reminders yet.</p>
            </div>
          ) : (
            completedReminders.map(renderReminderCard)
          )}
        </div>
      )}
    </div>
  );
};
