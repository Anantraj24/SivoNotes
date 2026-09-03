import { 
  Folder, 
  Note, 
  Todo, 
  ImportantPoint, 
  Reminder, 
  StreakData, 
  AppSettings,
  VaultPasswordEntry,
  VaultPrivateNote
} from '../types';

const STORAGE_KEYS = {
  FOLDERS: 'sivo_folders_v1',
  NOTES: 'sivo_notes_v1',
  TODOS: 'sivo_todos_v1',
  POINTS: 'sivo_points_v1',
  REMINDERS: 'sivo_reminders_v1',
  STREAK: 'sivo_streak_v1',
  SETTINGS: 'sivo_settings_v1',
  VAULT_PIN_HASH: 'sivo_vault_pin_hash_v1',
  VAULT_ENCRYPTED_DATA: 'sivo_vault_encrypted_data_v1',
};

const INITIAL_FOLDERS: Folder[] = [
  { id: 'f-college', name: 'College', icon: 'GraduationCap', colorKey: 'lavender', createdAt: Date.now() - 86400000 * 10 },
  { id: 'f-projects', name: 'Projects', icon: 'Code', colorKey: 'sky', createdAt: Date.now() - 86400000 * 8 },
  { id: 'f-personal', name: 'Personal', icon: 'User', colorKey: 'mint', createdAt: Date.now() - 86400000 * 5 },
  { id: 'f-exam', name: 'Exam Prep', icon: 'BookOpen', colorKey: 'amber', createdAt: Date.now() - 86400000 * 3 },
];

const INITIAL_NOTES: Note[] = [
  {
    id: 'n-1',
    title: 'Java Important Questions',
    content: `## Key Concepts to Revise\n- **ArrayList vs LinkedList**: Memory layout and random access differences.\n- **HashMap internals**: Array of buckets with LinkedList / Red-Black Tree in Java 8+.\n- **Inheritance & Polymorphism**: Method overriding vs overloading.\n- **Garbage Collection**: G1GC vs ZGC basics.`,
    folderId: 'f-college',
    tags: ['Java', 'Exam', 'Computer Science'],
    isPinned: true,
    createdAt: Date.now() - 7200000,
    updatedAt: Date.now() - 3600000,
  },
  {
    id: 'n-2',
    title: 'SIH Project Ideas',
    content: `### Smart India Hackathon Architecture Ideas\n- Offline-first personal knowledge management\n- Local biometric encryption & AES-GCM zero-trust storage\n- Minimalist design with high aesthetic visual rhythm.`,
    folderId: 'f-projects',
    tags: ['Hackathon', 'Ideas'],
    isPinned: true,
    createdAt: Date.now() - 86400000 * 2,
    updatedAt: Date.now() - 86400000,
  },
  {
    id: 'n-3',
    title: 'Book Recommendations',
    content: `- Atomic Habits by James Clear\n- Clean Code by Robert C. Martin\n- Designing Data-Intensive Applications by Martin Kleppmann`,
    folderId: 'f-personal',
    tags: ['Reading', 'Growth'],
    isPinned: false,
    createdAt: Date.now() - 86400000 * 4,
    updatedAt: Date.now() - 86400000 * 4,
  }
];

const INITIAL_POINTS: ImportantPoint[] = [
  { id: 'p-1', text: 'Java String is immutable (stored in String Constant Pool)', folderId: 'f-exam', isCompleted: false, createdAt: Date.now() - 100000 },
  { id: 'p-2', text: 'Array index starts from 0 in C/Java/JS', folderId: 'f-exam', isCompleted: false, createdAt: Date.now() - 200000 },
  { id: 'p-3', text: 'HashMap allows one null key and multiple null values', folderId: 'f-exam', isCompleted: true, createdAt: Date.now() - 300000 },
  { id: 'p-4', text: 'OSI model has 7 layers (Physical to Application)', folderId: 'f-college', isCompleted: false, createdAt: Date.now() - 400000 },
];

const todayStr = new Date().toISOString().split('T')[0];

const INITIAL_TODOS: Todo[] = [
  { id: 't-1', title: 'Complete DSA questions', description: 'Solve 2 LeetCode Medium problems on Trees', dueDate: todayStr, dueTime: '11:00', priority: 'high', repeat: 'daily', folderId: 'f-college', isCompleted: true, completedAt: Date.now() - 3600000, createdAt: Date.now() - 86400000 },
  { id: 't-2', title: 'Read 10 pages of Clean Architecture', description: '', dueDate: todayStr, dueTime: '14:00', priority: 'medium', repeat: 'daily', folderId: 'f-personal', isCompleted: true, completedAt: Date.now() - 1800000, createdAt: Date.now() - 86400000 },
  { id: 't-3', title: 'Study Java — 1 hour', description: 'Revise multithreading and ExecutorService', dueDate: todayStr, dueTime: '17:00', priority: 'high', repeat: 'weekdays', folderId: 'f-college', isCompleted: false, createdAt: Date.now() },
  { id: 't-4', title: 'Work on SivoNotes Web Prototype', description: 'Build and verify all Stitch screens', dueDate: todayStr, dueTime: '19:30', priority: 'high', repeat: 'none', folderId: 'f-projects', isCompleted: false, createdAt: Date.now() },
  { id: 't-5', title: 'Buy groceries and fruit', description: 'Apples, milk, oats', dueDate: todayStr, dueTime: '21:00', priority: 'low', repeat: 'none', folderId: 'f-personal', isCompleted: false, createdAt: Date.now() },
];

const INITIAL_REMINDERS: Reminder[] = [
  { id: 'r-1', title: 'Submit College Assignment', date: todayStr, time: '10:00', repeat: 'none', note: 'Upload PDF to classroom portal', isCompleted: false, createdAt: Date.now() - 86400000 },
  { id: 'r-2', title: 'College Project Meeting', date: new Date(Date.now() + 86400000).toISOString().split('T')[0], time: '09:30', repeat: 'weekly', note: 'Present architecture diagram', isCompleted: false, createdAt: Date.now() },
];

const INITIAL_STREAK: StreakData = {
  currentStreak: 7,
  bestStreak: 21,
  activeDates: [
    new Date(Date.now() - 86400000 * 6).toISOString().split('T')[0],
    new Date(Date.now() - 86400000 * 5).toISOString().split('T')[0],
    new Date(Date.now() - 86400000 * 4).toISOString().split('T')[0],
    new Date(Date.now() - 86400000 * 3).toISOString().split('T')[0],
    new Date(Date.now() - 86400000 * 2).toISOString().split('T')[0],
    new Date(Date.now() - 86400000 * 1).toISOString().split('T')[0],
    todayStr,
  ],
  totalCompleted: 24,
};

const INITIAL_SETTINGS: AppSettings = {
  darkMode: false,
  userName: 'Anant',
  autoLockMinutes: 5,
  hasVaultPin: false,
};

export const StorageService = {
  getFolders(): Folder[] {
    const raw = localStorage.getItem(STORAGE_KEYS.FOLDERS);
    if (!raw) {
      localStorage.setItem(STORAGE_KEYS.FOLDERS, JSON.stringify(INITIAL_FOLDERS));
      return INITIAL_FOLDERS;
    }
    return JSON.parse(raw);
  },
  saveFolders(folders: Folder[]): void {
    localStorage.setItem(STORAGE_KEYS.FOLDERS, JSON.stringify(folders));
  },

  getNotes(): Note[] {
    const raw = localStorage.getItem(STORAGE_KEYS.NOTES);
    if (!raw) {
      localStorage.setItem(STORAGE_KEYS.NOTES, JSON.stringify(INITIAL_NOTES));
      return INITIAL_NOTES;
    }
    return JSON.parse(raw);
  },
  saveNotes(notes: Note[]): void {
    localStorage.setItem(STORAGE_KEYS.NOTES, JSON.stringify(notes));
  },

  getPoints(): ImportantPoint[] {
    const raw = localStorage.getItem(STORAGE_KEYS.POINTS);
    if (!raw) {
      localStorage.setItem(STORAGE_KEYS.POINTS, JSON.stringify(INITIAL_POINTS));
      return INITIAL_POINTS;
    }
    return JSON.parse(raw);
  },
  savePoints(points: ImportantPoint[]): void {
    localStorage.setItem(STORAGE_KEYS.POINTS, JSON.stringify(points));
  },

  getTodos(): Todo[] {
    const raw = localStorage.getItem(STORAGE_KEYS.TODOS);
    if (!raw) {
      localStorage.setItem(STORAGE_KEYS.TODOS, JSON.stringify(INITIAL_TODOS));
      return INITIAL_TODOS;
    }
    return JSON.parse(raw);
  },
  saveTodos(todos: Todo[]): void {
    localStorage.setItem(STORAGE_KEYS.TODOS, JSON.stringify(todos));
  },

  getReminders(): Reminder[] {
    const raw = localStorage.getItem(STORAGE_KEYS.REMINDERS);
    if (!raw) {
      localStorage.setItem(STORAGE_KEYS.REMINDERS, JSON.stringify(INITIAL_REMINDERS));
      return INITIAL_REMINDERS;
    }
    return JSON.parse(raw);
  },
  saveReminders(reminders: Reminder[]): void {
    localStorage.setItem(STORAGE_KEYS.REMINDERS, JSON.stringify(reminders));
  },

  getStreak(): StreakData {
    const raw = localStorage.getItem(STORAGE_KEYS.STREAK);
    if (!raw) {
      localStorage.setItem(STORAGE_KEYS.STREAK, JSON.stringify(INITIAL_STREAK));
      return INITIAL_STREAK;
    }
    return JSON.parse(raw);
  },
  saveStreak(streak: StreakData): void {
    localStorage.setItem(STORAGE_KEYS.STREAK, JSON.stringify(streak));
  },

  getSettings(): AppSettings {
    const raw = localStorage.getItem(STORAGE_KEYS.SETTINGS);
    const pinHash = localStorage.getItem(STORAGE_KEYS.VAULT_PIN_HASH);
    if (!raw) {
      const s = { ...INITIAL_SETTINGS, hasVaultPin: !!pinHash };
      localStorage.setItem(STORAGE_KEYS.SETTINGS, JSON.stringify(s));
      return s;
    }
    const parsed = JSON.parse(raw);
    parsed.hasVaultPin = !!pinHash;
    return parsed;
  },
  saveSettings(settings: AppSettings): void {
    localStorage.setItem(STORAGE_KEYS.SETTINGS, JSON.stringify(settings));
  },

  getVaultPinHash(): string | null {
    return localStorage.getItem(STORAGE_KEYS.VAULT_PIN_HASH);
  },
  saveVaultPinHash(hash: string): void {
    localStorage.setItem(STORAGE_KEYS.VAULT_PIN_HASH, hash);
  },

  getVaultEncryptedPayload(): string | null {
    return localStorage.getItem(STORAGE_KEYS.VAULT_ENCRYPTED_DATA);
  },
  saveVaultEncryptedPayload(cipherText: string): void {
    localStorage.setItem(STORAGE_KEYS.VAULT_ENCRYPTED_DATA, cipherText);
  },

  // Export all unencrypted data to JSON backup
  exportAllData() {
    return {
      version: 1,
      exportedAt: new Date().toISOString(),
      folders: this.getFolders(),
      notes: this.getNotes(),
      points: this.getPoints(),
      todos: this.getTodos(),
      reminders: this.getReminders(),
      streak: this.getStreak(),
      settings: this.getSettings(),
    };
  },

  importAllData(data: any): boolean {
    try {
      if (data.folders) this.saveFolders(data.folders);
      if (data.notes) this.saveNotes(data.notes);
      if (data.points) this.savePoints(data.points);
      if (data.todos) this.saveTodos(data.todos);
      if (data.reminders) this.saveReminders(data.reminders);
      if (data.streak) this.saveStreak(data.streak);
      if (data.settings) this.saveSettings(data.settings);
      return true;
    } catch {
      return false;
    }
  },

  resetAllData(): void {
    localStorage.clear();
  }
};
