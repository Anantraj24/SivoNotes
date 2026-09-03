export type PastelColorKey = 'coral' | 'mint' | 'amber' | 'yellow' | 'sky' | 'lavender' | 'rose';

export interface Folder {
  id: string;
  name: string;
  icon: string;
  colorKey: PastelColorKey;
  createdAt: number;
}

export interface Note {
  id: string;
  title: string;
  content: string;
  folderId?: string;
  tags: string[];
  isPinned: boolean;
  createdAt: number;
  updatedAt: number;
}

export type TodoPriority = 'low' | 'medium' | 'high';
export type TodoRepeat = 'none' | 'daily' | 'weekdays' | 'weekly' | 'custom';

export interface Todo {
  id: string;
  title: string;
  description?: string;
  dueDate?: string; // YYYY-MM-DD
  dueTime?: string; // HH:mm
  priority: TodoPriority;
  repeat: TodoRepeat;
  folderId?: string;
  isCompleted: boolean;
  completedAt?: number;
  createdAt: number;
}

export interface ImportantPoint {
  id: string;
  text: string;
  folderId?: string;
  isCompleted: boolean;
  createdAt: number;
}

export type ReminderRepeat = 'none' | 'daily' | 'weekly' | 'monthly';

export interface Reminder {
  id: string;
  title: string;
  date: string; // YYYY-MM-DD
  time: string; // HH:mm
  repeat: ReminderRepeat;
  note?: string;
  isCompleted: boolean;
  createdAt: number;
}

export interface VaultPasswordEntry {
  id: string;
  title: string;
  username: string;
  password: string;
  url?: string;
  notes?: string;
  createdAt: number;
}

export interface VaultPrivateNote {
  id: string;
  title: string;
  content: string;
  tags: string[];
  createdAt: number;
  updatedAt: number;
}

export interface StreakData {
  currentStreak: number;
  bestStreak: number;
  activeDates: string[]; // ['2026-09-01', ...]
  totalCompleted: number;
}

export interface AppSettings {
  darkMode: boolean;
  userName: string;
  defaultFolderId?: string;
  autoLockMinutes: number;
  hasVaultPin: boolean;
}

export type ActiveTab = 'home' | 'notes' | 'todos' | 'points' | 'reminders' | 'folders' | 'vault' | 'search' | 'settings';
