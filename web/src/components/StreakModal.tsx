import React from 'react';
import { X, Flame, Trophy, CheckCircle2, TrendingUp } from 'lucide-react';
import { useApp } from '../context/AppContext';

export const StreakModal: React.FC = () => {
  const { isStreakModalOpen, setIsStreakModalOpen, streak, todos } = useApp();

  if (!isStreakModalOpen) return null;

  const totalTasks = todos.length;
  const completedTasks = todos.filter(t => t.isCompleted).length;
  const completionRate = totalTasks > 0 ? Math.round((completedTasks / totalTasks) * 100) : 0;

  // Generate last 14 days for activity heatmap
  const daysList: { dateStr: string; dayNum: number; dayName: string; isActive: boolean }[] = [];
  const today = new Date();
  
  for (let i = 13; i >= 0; i--) {
    const d = new Date();
    d.setDate(today.getDate() - i);
    const dateStr = d.toISOString().split('T')[0];
    const dayNum = d.getDate();
    const dayName = d.toLocaleDateString('en-US', { weekday: 'narrow' });
    const isActive = streak.activeDates.includes(dateStr);
    daysList.push({ dateStr, dayNum, dayName, isActive });
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 bg-black/50 backdrop-blur-sm animate-fade-in">
      <div className="w-full max-w-md bg-white dark:bg-sivo-dark-surface rounded-3xl border border-sivo-border dark:border-sivo-dark-border shadow-2xl p-6 animate-slide-up">
        
        <div className="flex items-center justify-between pb-4 border-b border-sivo-border/60 dark:border-sivo-dark-border/60">
          <div className="flex items-center gap-2">
            <div className="p-2 rounded-2xl bg-amber-500/10 text-amber-500">
              <Flame className="w-5 h-5 fill-current" />
            </div>
            <div>
              <h3 className="text-lg font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
                Streak & Progress
              </h3>
              <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary">
                Keep your daily momentum alive
              </p>
            </div>
          </div>
          <button
            onClick={() => setIsStreakModalOpen(false)}
            className="p-2 rounded-full hover:bg-sivo-surface-variant dark:hover:bg-sivo-dark-surface-variant text-sivo-text-secondary hover:text-sivo-text-primary"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Streak Stats Cards */}
        <div className="grid grid-cols-2 gap-3 my-5">
          <div className="p-4 rounded-2xl bg-gradient-to-br from-amber-500/10 via-orange-500/5 to-transparent border border-amber-500/20 flex flex-col items-center justify-center text-center">
            <div className="w-10 h-10 rounded-full bg-amber-500/20 text-amber-500 flex items-center justify-center mb-1.5 shadow-sm">
              <Flame className="w-6 h-6 fill-current animate-pulse" />
            </div>
            <span className="text-3xl font-extrabold text-amber-500 font-['Outfit']">
              {streak.currentStreak}
            </span>
            <span className="text-xs font-semibold text-sivo-text-primary dark:text-sivo-dark-text-primary mt-0.5">
              Current Streak
            </span>
            <span className="text-[10px] text-sivo-text-muted">Days in a row</span>
          </div>

          <div className="p-4 rounded-2xl bg-gradient-to-br from-sivo-primary/10 via-purple-500/5 to-transparent border border-sivo-primary/20 flex flex-col items-center justify-center text-center">
            <div className="w-10 h-10 rounded-full bg-sivo-primary-container text-sivo-primary flex items-center justify-center mb-1.5 shadow-sm">
              <Trophy className="w-5 h-5" />
            </div>
            <span className="text-3xl font-extrabold text-sivo-primary font-['Outfit']">
              {streak.bestStreak}
            </span>
            <span className="text-xs font-semibold text-sivo-text-primary dark:text-sivo-dark-text-primary mt-0.5">
              Best Record
            </span>
            <span className="text-[10px] text-sivo-text-muted">Historical peak</span>
          </div>
        </div>

        {/* 14-day Activity Calendar */}
        <div className="p-4 rounded-2xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border/60 dark:border-sivo-dark-border/60 mb-5">
          <span className="text-xs font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary mb-3 block">
            Recent 14-Day Activity
          </span>
          <div className="grid grid-cols-7 gap-2 text-center">
            {daysList.map((d) => (
              <div key={d.dateStr} className="flex flex-col items-center gap-1">
                <span className="text-[10px] text-sivo-text-muted font-medium">{d.dayName}</span>
                <div
                  className={`w-7 h-7 rounded-xl flex items-center justify-center text-[11px] font-bold transition-all ${
                    d.isActive
                      ? 'bg-amber-500 text-white shadow-sm shadow-amber-500/30'
                      : 'bg-sivo-surface-variant dark:bg-sivo-dark-surface-variant text-sivo-text-secondary dark:text-sivo-dark-text-secondary'
                  }`}
                  title={d.dateStr}
                >
                  {d.dayNum}
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Today's Tasks Progress Bar */}
        <div className="p-4 rounded-2xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border/60 dark:border-sivo-dark-border/60">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary flex items-center gap-1.5">
              <TrendingUp className="w-3.5 h-3.5 text-sivo-primary" />
              Today's Completion Rate
            </span>
            <span className="text-xs font-extrabold text-sivo-primary">
              {completionRate}%
            </span>
          </div>

          <div className="w-full bg-sivo-surface-variant dark:bg-sivo-dark-surface-variant rounded-full h-2.5 overflow-hidden">
            <div
              className="bg-gradient-to-r from-sivo-primary to-pastel-mint h-2.5 rounded-full transition-all duration-500"
              style={{ width: `${completionRate}%` }}
            />
          </div>

          <div className="flex items-center justify-between mt-2.5 text-[11px] text-sivo-text-secondary dark:text-sivo-dark-text-secondary">
            <span className="flex items-center gap-1">
              <CheckCircle2 className="w-3.5 h-3.5 text-pastel-mint" />
              {completedTasks} of {totalTasks} tasks done
            </span>
            <span>Total completed: {streak.totalCompleted}</span>
          </div>
        </div>

        <div className="mt-5 flex justify-end">
          <button
            onClick={() => setIsStreakModalOpen(false)}
            className="w-full py-2.5 rounded-full bg-sivo-primary hover:bg-sivo-primary-variant text-white text-xs font-semibold shadow-md transition-all"
          >
            Keep Going!
          </button>
        </div>
      </div>
    </div>
  );
};
