import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import confetti from 'canvas-confetti';
import { 
  Folder, 
  Note, 
  Todo, 
  ImportantPoint, 
  Reminder, 
  StreakData, 
  AppSettings, 
  ActiveTab,
  VaultPasswordEntry,
  VaultPrivateNote,
  PastelColorKey
} from '../types';
import { StorageService } from '../services/storage';
import { hashPin, encryptData, decryptData } from '../services/crypto';

interface AppContextType {
  // Navigation
  activeTab: ActiveTab;
  setActiveTab: (tab: ActiveTab) => void;
  
  // Settings & Theme
  settings: AppSettings;
  updateSettings: (newSettings: Partial<AppSettings>) => void;
  toggleDarkMode: () => void;

  // Folders
  folders: Folder[];
  selectedFolderId: string | null;
  setSelectedFolderId: (id: string | null) => void;
  addFolder: (name: string, icon: string, colorKey: PastelColorKey) => Folder;
  updateFolder: (id: string, name: string, icon: string, colorKey: PastelColorKey) => void;
  deleteFolder: (id: string) => void;

  // Notes
  notes: Note[];
  editingNote: Note | null;
  setEditingNote: (note: Note | null) => void;
  addNote: (title: string, content: string, folderId?: string, tags?: string[], isPinned?: boolean) => Note;
  updateNote: (id: string, updates: Partial<Note>) => void;
  deleteNote: (id: string) => void;
  togglePinNote: (id: string) => void;

  // Important Points
  points: ImportantPoint[];
  addPoint: (text: string, folderId?: string) => ImportantPoint;
  togglePoint: (id: string) => void;
  deletePoint: (id: string) => void;

  // Todos & Streak
  todos: Todo[];
  streak: StreakData;
  addTodo: (todo: Omit<Todo, 'id' | 'isCompleted' | 'createdAt'>) => Todo;
  toggleTodo: (id: string) => void;
  updateTodo: (id: string, updates: Partial<Todo>) => void;
  deleteTodo: (id: string) => void;

  // Reminders
  reminders: Reminder[];
  addReminder: (reminder: Omit<Reminder, 'id' | 'isCompleted' | 'createdAt'>) => Reminder;
  toggleReminder: (id: string) => void;
  snoozeReminder: (id: string, minutes?: number) => void;
  deleteReminder: (id: string) => void;

  // Vault
  isVaultUnlocked: boolean;
  vaultPasswords: VaultPasswordEntry[];
  vaultNotes: VaultPrivateNote[];
  setupVault: (pin: string) => Promise<boolean>;
  unlockVault: (pin: string) => Promise<boolean>;
  lockVault: () => void;
  addVaultPassword: (entry: Omit<VaultPasswordEntry, 'id' | 'createdAt'>) => Promise<void>;
  deleteVaultPassword: (id: string) => Promise<void>;
  addVaultNote: (note: Omit<VaultPrivateNote, 'id' | 'createdAt' | 'updatedAt'>) => Promise<void>;
  updateVaultNote: (id: string, note: Partial<VaultPrivateNote>) => Promise<void>;
  deleteVaultNote: (id: string) => Promise<void>;
  changeVaultPin: (oldPin: string, newPin: string) => Promise<boolean>;

  // Global Search
  searchQuery: string;
  setSearchQuery: (query: string) => void;

  // Global Modals
  isUniversalAddOpen: boolean;
  setIsUniversalAddOpen: (open: boolean) => void;
  isNoteEditorOpen: boolean;
  setIsNoteEditorOpen: (open: boolean) => void;
  isStreakModalOpen: boolean;
  setIsStreakModalOpen: (open: boolean) => void;
  isFolderModalOpen: boolean;
  setIsFolderModalOpen: (open: boolean) => void;
  isTodoModalOpen: boolean;
  setIsTodoModalOpen: (open: boolean) => void;
  isReminderModalOpen: boolean;
  setIsReminderModalOpen: (open: boolean) => void;
  isPointModalOpen: boolean;
  setIsPointModalOpen: (open: boolean) => void;

  // Backup
  exportData: () => void;
  importData: (jsonData: any) => boolean;
  resetAll: () => void;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

export const AppProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  // Navigation
  const [activeTab, setActiveTab] = useState<ActiveTab>('home');
  const [searchQuery, setSearchQuery] = useState('');

  // Core Entity State
  const [folders, setFolders] = useState<Folder[]>(() => StorageService.getFolders());
  const [selectedFolderId, setSelectedFolderId] = useState<string | null>(null);
  const [notes, setNotes] = useState<Note[]>(() => StorageService.getNotes());
  const [points, setPoints] = useState<ImportantPoint[]>(() => StorageService.getPoints());
  const [todos, setTodos] = useState<Todo[]>(() => StorageService.getTodos());
  const [reminders, setReminders] = useState<Reminder[]>(() => StorageService.getReminders());
  const [streak, setStreak] = useState<StreakData>(() => StorageService.getStreak());
  const [settings, setSettings] = useState<AppSettings>(() => StorageService.getSettings());

  // Editing Note
  const [editingNote, setEditingNote] = useState<Note | null>(null);

  // Vault State
  const [currentPin, setCurrentPin] = useState<string | null>(null);
  const [isVaultUnlocked, setIsVaultUnlocked] = useState(false);
  const [vaultPasswords, setVaultPasswords] = useState<VaultPasswordEntry[]>([]);
  const [vaultNotes, setVaultNotes] = useState<VaultPrivateNote[]>([]);

  // Modals
  const [isUniversalAddOpen, setIsUniversalAddOpen] = useState(false);
  const [isNoteEditorOpen, setIsNoteEditorOpen] = useState(false);
  const [isStreakModalOpen, setIsStreakModalOpen] = useState(false);
  const [isFolderModalOpen, setIsFolderModalOpen] = useState(false);
  const [isTodoModalOpen, setIsTodoModalOpen] = useState(false);
  const [isReminderModalOpen, setIsReminderModalOpen] = useState(false);
  const [isPointModalOpen, setIsPointModalOpen] = useState(false);

  // Apply theme
  useEffect(() => {
    if (settings.darkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [settings.darkMode]);

  // Sync to local storage
  const updateSettings = (newSettings: Partial<AppSettings>) => {
    setSettings(prev => {
      const updated = { ...prev, ...newSettings };
      StorageService.saveSettings(updated);
      return updated;
    });
  };

  const toggleDarkMode = () => {
    updateSettings({ darkMode: !settings.darkMode });
  };

  // Folder Actions
  const addFolder = (name: string, icon: string, colorKey: PastelColorKey): Folder => {
    const newFolder: Folder = {
      id: 'f-' + Date.now(),
      name,
      icon,
      colorKey,
      createdAt: Date.now(),
    };
    const updated = [newFolder, ...folders];
    setFolders(updated);
    StorageService.saveFolders(updated);
    return newFolder;
  };

  const updateFolder = (id: string, name: string, icon: string, colorKey: PastelColorKey) => {
    const updated = folders.map(f => f.id === id ? { ...f, name, icon, colorKey } : f);
    setFolders(updated);
    StorageService.saveFolders(updated);
  };

  const deleteFolder = (id: string) => {
    const updated = folders.filter(f => f.id !== id);
    setFolders(updated);
    StorageService.saveFolders(updated);
    if (selectedFolderId === id) setSelectedFolderId(null);
  };

  // Note Actions
  const addNote = (title: string, content: string, folderId?: string, tags: string[] = [], isPinned = false): Note => {
    const newNote: Note = {
      id: 'n-' + Date.now(),
      title: title || 'Untitled Note',
      content,
      folderId: folderId || selectedFolderId || undefined,
      tags,
      isPinned,
      createdAt: Date.now(),
      updatedAt: Date.now(),
    };
    const updated = [newNote, ...notes];
    setNotes(updated);
    StorageService.saveNotes(updated);
    return newNote;
  };

  const updateNote = (id: string, updates: Partial<Note>) => {
    const updated = notes.map(n => n.id === id ? { ...n, ...updates, updatedAt: Date.now() } : n);
    setNotes(updated);
    StorageService.saveNotes(updated);
    if (editingNote?.id === id) {
      setEditingNote(prev => prev ? { ...prev, ...updates, updatedAt: Date.now() } : null);
    }
  };

  const deleteNote = (id: string) => {
    const updated = notes.filter(n => n.id !== id);
    setNotes(updated);
    StorageService.saveNotes(updated);
    if (editingNote?.id === id) {
      setEditingNote(null);
      setIsNoteEditorOpen(false);
    }
  };

  const togglePinNote = (id: string) => {
    const updated = notes.map(n => n.id === id ? { ...n, isPinned: !n.isPinned } : n);
    setNotes(updated);
    StorageService.saveNotes(updated);
  };

  // Important Points Actions
  const addPoint = (text: string, folderId?: string): ImportantPoint => {
    const newPoint: ImportantPoint = {
      id: 'p-' + Date.now(),
      text,
      folderId: folderId || selectedFolderId || undefined,
      isCompleted: false,
      createdAt: Date.now(),
    };
    const updated = [newPoint, ...points];
    setPoints(updated);
    StorageService.savePoints(updated);
    return newPoint;
  };

  const togglePoint = (id: string) => {
    const updated = points.map(p => p.id === id ? { ...p, isCompleted: !p.isCompleted } : p);
    setPoints(updated);
    StorageService.savePoints(updated);
  };

  const deletePoint = (id: string) => {
    const updated = points.filter(p => p.id !== id);
    setPoints(updated);
    StorageService.savePoints(updated);
  };

  // Todos & Streak calculation
  const addTodo = (todoData: Omit<Todo, 'id' | 'isCompleted' | 'createdAt'>): Todo => {
    const newTodo: Todo = {
      ...todoData,
      id: 't-' + Date.now(),
      folderId: todoData.folderId || selectedFolderId || undefined,
      isCompleted: false,
      createdAt: Date.now(),
    };
    const updated = [newTodo, ...todos];
    setTodos(updated);
    StorageService.saveTodos(updated);
    return newTodo;
  };

  const toggleTodo = (id: string) => {
    const today = new Date().toISOString().split('T')[0];
    let justCompleted = false;

    const updated = todos.map(t => {
      if (t.id === id) {
        const nextState = !t.isCompleted;
        if (nextState) justCompleted = true;
        return {
          ...t,
          isCompleted: nextState,
          completedAt: nextState ? Date.now() : undefined,
        };
      }
      return t;
    });

    setTodos(updated);
    StorageService.saveTodos(updated);

    if (justCompleted) {
      // Trigger subtle celebration confetti
      try {
        confetti({
          particleCount: 40,
          spread: 60,
          origin: { y: 0.8 },
          colors: ['#6C5CE7', '#00B894', '#FF7675', '#F39C12']
        });
      } catch {}

      // Update Streak
      const activeSet = new Set(streak.activeDates);
      if (!activeSet.has(today)) {
        activeSet.add(today);
        const newDates = Array.from(activeSet);
        const newCurrent = streak.currentStreak + 1;
        const newBest = Math.max(newCurrent, streak.bestStreak);
        const newStreak: StreakData = {
          currentStreak: newCurrent,
          bestStreak: newBest,
          activeDates: newDates,
          totalCompleted: streak.totalCompleted + 1,
        };
        setStreak(newStreak);
        StorageService.saveStreak(newStreak);
      } else {
        const newStreak = { ...streak, totalCompleted: streak.totalCompleted + 1 };
        setStreak(newStreak);
        StorageService.saveStreak(newStreak);
      }
    }
  };

  const updateTodo = (id: string, updates: Partial<Todo>) => {
    const updated = todos.map(t => t.id === id ? { ...t, ...updates } : t);
    setTodos(updated);
    StorageService.saveTodos(updated);
  };

  const deleteTodo = (id: string) => {
    const updated = todos.filter(t => t.id !== id);
    setTodos(updated);
    StorageService.saveTodos(updated);
  };

  // Reminders Actions
  const addReminder = (remData: Omit<Reminder, 'id' | 'isCompleted' | 'createdAt'>): Reminder => {
    const newRem: Reminder = {
      ...remData,
      id: 'r-' + Date.now(),
      isCompleted: false,
      createdAt: Date.now(),
    };
    const updated = [newRem, ...reminders];
    setReminders(updated);
    StorageService.saveReminders(updated);
    return newRem;
  };

  const toggleReminder = (id: string) => {
    const updated = reminders.map(r => r.id === id ? { ...r, isCompleted: !r.isCompleted } : r);
    setReminders(updated);
    StorageService.saveReminders(updated);
  };

  const snoozeReminder = (id: string, minutes = 15) => {
    const updated = reminders.map(r => {
      if (r.id === id) {
        const now = new Date();
        now.setMinutes(now.getMinutes() + minutes);
        const hours = String(now.getHours()).padStart(2, '0');
        const mins = String(now.getMinutes()).padStart(2, '0');
        return {
          ...r,
          time: `${hours}:${mins}`,
          date: now.toISOString().split('T')[0],
        };
      }
      return r;
    });
    setReminders(updated);
    StorageService.saveReminders(updated);
  };

  const deleteReminder = (id: string) => {
    const updated = reminders.filter(r => r.id !== id);
    setReminders(updated);
    StorageService.saveReminders(updated);
  };

  // Vault Actions & Encryption
  const saveVaultEncrypted = async (passwords: VaultPasswordEntry[], vaultNotesList: VaultPrivateNote[], pin: string) => {
    const payload = JSON.stringify({ passwords, notes: vaultNotesList });
    const cipherText = await encryptData(payload, pin);
    StorageService.saveVaultEncryptedPayload(cipherText);
  };

  const setupVault = async (pin: string): Promise<boolean> => {
    try {
      const pinH = await hashPin(pin);
      StorageService.saveVaultPinHash(pinH);
      setCurrentPin(pin);
      setIsVaultUnlocked(true);
      setVaultPasswords([]);
      setVaultNotes([]);
      await saveVaultEncrypted([], [], pin);
      updateSettings({ hasVaultPin: true });
      return true;
    } catch {
      return false;
    }
  };

  const unlockVault = async (pin: string): Promise<boolean> => {
    try {
      const storedHash = StorageService.getVaultPinHash();
      const enteredHash = await hashPin(pin);
      if (storedHash !== enteredHash) {
        return false;
      }

      const payload = StorageService.getVaultEncryptedPayload();
      if (payload) {
        const decryptedJson = await decryptData(payload, pin);
        const parsed = JSON.parse(decryptedJson);
        setVaultPasswords(parsed.passwords || []);
        setVaultNotes(parsed.notes || []);
      } else {
        setVaultPasswords([]);
        setVaultNotes([]);
      }

      setCurrentPin(pin);
      setIsVaultUnlocked(true);
      return true;
    } catch {
      return false;
    }
  };

  const lockVault = () => {
    setIsVaultUnlocked(false);
    setCurrentPin(null);
    setVaultPasswords([]);
    setVaultNotes([]);
  };

  const addVaultPassword = async (entry: Omit<VaultPasswordEntry, 'id' | 'createdAt'>) => {
    if (!currentPin) return;
    const newEntry: VaultPasswordEntry = {
      ...entry,
      id: 'vp-' + Date.now(),
      createdAt: Date.now(),
    };
    const updated = [newEntry, ...vaultPasswords];
    setVaultPasswords(updated);
    await saveVaultEncrypted(updated, vaultNotes, currentPin);
  };

  const deleteVaultPassword = async (id: string) => {
    if (!currentPin) return;
    const updated = vaultPasswords.filter(p => p.id !== id);
    setVaultPasswords(updated);
    await saveVaultEncrypted(updated, vaultNotes, currentPin);
  };

  const addVaultNote = async (note: Omit<VaultPrivateNote, 'id' | 'createdAt' | 'updatedAt'>) => {
    if (!currentPin) return;
    const newNote: VaultPrivateNote = {
      ...note,
      id: 'vn-' + Date.now(),
      createdAt: Date.now(),
      updatedAt: Date.now(),
    };
    const updated = [newNote, ...vaultNotes];
    setVaultNotes(updated);
    await saveVaultEncrypted(vaultPasswords, updated, currentPin);
  };

  const updateVaultNote = async (id: string, noteUpdates: Partial<VaultPrivateNote>) => {
    if (!currentPin) return;
    const updated = vaultNotes.map(n => n.id === id ? { ...n, ...noteUpdates, updatedAt: Date.now() } : n);
    setVaultNotes(updated);
    await saveVaultEncrypted(vaultPasswords, updated, currentPin);
  };

  const deleteVaultNote = async (id: string) => {
    if (!currentPin) return;
    const updated = vaultNotes.filter(n => n.id !== id);
    setVaultNotes(updated);
    await saveVaultEncrypted(vaultPasswords, updated, currentPin);
  };

  const changeVaultPin = async (oldPin: string, newPin: string): Promise<boolean> => {
    try {
      const storedHash = StorageService.getVaultPinHash();
      const enteredHash = await hashPin(oldPin);
      if (storedHash !== enteredHash) return false;

      const newHash = await hashPin(newPin);
      StorageService.saveVaultPinHash(newHash);
      setCurrentPin(newPin);
      await saveVaultEncrypted(vaultPasswords, vaultNotes, newPin);
      return true;
    } catch {
      return false;
    }
  };

  // Backup / Export
  const exportData = () => {
    const data = StorageService.exportAllData();
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `sivo-notes-backup-${new Date().toISOString().split('T')[0]}.json`;
    a.click();
    URL.revokeObjectURL(url);
  };

  const importData = (jsonData: any): boolean => {
    const success = StorageService.importAllData(jsonData);
    if (success) {
      setFolders(StorageService.getFolders());
      setNotes(StorageService.getNotes());
      setPoints(StorageService.getPoints());
      setTodos(StorageService.getTodos());
      setReminders(StorageService.getReminders());
      setStreak(StorageService.getStreak());
      setSettings(StorageService.getSettings());
    }
    return success;
  };

  const resetAll = () => {
    StorageService.resetAllData();
    window.location.reload();
  };

  return (
    <AppContext.Provider
      value={{
        activeTab,
        setActiveTab,
        settings,
        updateSettings,
        toggleDarkMode,
        folders,
        selectedFolderId,
        setSelectedFolderId,
        addFolder,
        updateFolder,
        deleteFolder,
        notes,
        editingNote,
        setEditingNote,
        addNote,
        updateNote,
        deleteNote,
        togglePinNote,
        points,
        addPoint,
        togglePoint,
        deletePoint,
        todos,
        streak,
        addTodo,
        toggleTodo,
        updateTodo,
        deleteTodo,
        reminders,
        addReminder,
        toggleReminder,
        snoozeReminder,
        deleteReminder,
        isVaultUnlocked,
        vaultPasswords,
        vaultNotes,
        setupVault,
        unlockVault,
        lockVault,
        addVaultPassword,
        deleteVaultPassword,
        addVaultNote,
        updateVaultNote,
        deleteVaultNote,
        changeVaultPin,
        searchQuery,
        setSearchQuery,
        isUniversalAddOpen,
        setIsUniversalAddOpen,
        isNoteEditorOpen,
        setIsNoteEditorOpen,
        isStreakModalOpen,
        setIsStreakModalOpen,
        isFolderModalOpen,
        setIsFolderModalOpen,
        isTodoModalOpen,
        setIsTodoModalOpen,
        isReminderModalOpen,
        setIsReminderModalOpen,
        isPointModalOpen,
        setIsPointModalOpen,
        exportData,
        importData,
        resetAll,
      }}
    >
      {children}
    </AppContext.Provider>
  );
};

export const useApp = () => {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within an AppProvider');
  }
  return context;
};
