import React, { useRef, useState } from 'react';
import { 
  User, 
  Moon, 
  Sun, 
  Download, 
  Upload, 
  Trash2, 
  ShieldCheck, 
  Info, 
  Check, 
  AlertTriangle 
} from 'lucide-react';
import { useApp } from '../context/AppContext';

export const SettingsScreen: React.FC = () => {
  const { 
    settings, 
    updateSettings, 
    toggleDarkMode, 
    exportData, 
    importData, 
    resetAll 
  } = useApp();

  const [name, setName] = useState(settings.userName);
  const [isSaved, setIsSaved] = useState(false);
  const [importMessage, setImportMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleSaveName = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;
    updateSettings({ userName: name.trim() });
    setIsSaved(true);
    setTimeout(() => setIsSaved(false), 2000);
  };

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = (event) => {
      try {
        const json = JSON.parse(event.target?.result as string);
        const success = importData(json);
        if (success) {
          setImportMessage({ type: 'success', text: 'Backup restored successfully!' });
        } else {
          setImportMessage({ type: 'error', text: 'Invalid backup file structure.' });
        }
      } catch {
        setImportMessage({ type: 'error', text: 'Failed to read backup file.' });
      }
    };
    reader.readAsText(file);
  };

  return (
    <div className="space-y-6 pb-24 max-w-4xl mx-auto px-4 sm:px-6 pt-4 animate-fade-in">
      <div>
        <h2 className="text-2xl font-bold tracking-tight text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
          Settings & Data
        </h2>
        <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary">
          Preferences, offline storage, and privacy controls
        </p>
      </div>

      {/* Profile Name Card */}
      <div className="p-5 rounded-3xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm space-y-3">
        <h3 className="text-sm font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary flex items-center gap-2 font-['Outfit']">
          <User className="w-4 h-4 text-sivo-primary" /> Profile & Greeting
        </h3>

        <form onSubmit={handleSaveName} className="flex items-center gap-2">
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Your name"
            className="flex-1 px-4 py-2.5 rounded-2xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border dark:border-sivo-dark-border text-xs outline-none focus:border-sivo-primary text-sivo-text-primary dark:text-sivo-dark-text-primary"
          />
          <button
            type="submit"
            className="px-5 py-2.5 rounded-2xl bg-sivo-primary hover:bg-sivo-primary-variant text-white text-xs font-semibold shadow-sm transition-all flex items-center gap-1.5"
          >
            {isSaved ? <Check className="w-3.5 h-3.5" /> : null}
            {isSaved ? 'Saved' : 'Update'}
          </button>
        </form>
      </div>

      {/* Appearance Card */}
      <div className="p-5 rounded-3xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm flex items-center justify-between">
        <div>
          <h3 className="text-sm font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
            Theme & Appearance
          </h3>
          <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary mt-0.5">
            Switch between light and dark visual modes
          </p>
        </div>

        <button
          onClick={toggleDarkMode}
          className="flex items-center gap-2 px-4 py-2 rounded-2xl bg-sivo-surface-variant dark:bg-sivo-dark-surface-variant text-xs font-semibold text-sivo-text-primary dark:text-sivo-dark-text-primary hover:bg-sivo-primary-container transition-all"
        >
          {settings.darkMode ? <Sun className="w-4 h-4 text-amber-400" /> : <Moon className="w-4 h-4 text-sivo-primary" />}
          <span>{settings.darkMode ? 'Dark Mode' : 'Light Mode'}</span>
        </button>
      </div>

      {/* Data Backup & Export Card */}
      <div className="p-5 rounded-3xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm space-y-4">
        <div>
          <h3 className="text-sm font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
            Data Backup & Restore
          </h3>
          <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary mt-0.5">
            Export all your offline notes, tasks, folders, and points as a JSON file.
          </p>
        </div>

        {importMessage && (
          <div className={`p-3 rounded-2xl text-xs flex items-center gap-2 ${
            importMessage.type === 'success' ? 'bg-pastel-mint-bg text-pastel-mint' : 'bg-sivo-error-container text-sivo-error'
          }`}>
            <Info className="w-4 h-4 shrink-0" />
            <span>{importMessage.text}</span>
          </div>
        )}

        <div className="flex flex-wrap gap-2.5">
          <button
            onClick={exportData}
            className="flex items-center gap-2 px-4 py-2.5 rounded-2xl bg-sivo-primary hover:bg-sivo-primary-variant text-white text-xs font-semibold shadow-sm transition-all"
          >
            <Download className="w-4 h-4" />
            Download Backup (.json)
          </button>

          <input
            ref={fileInputRef}
            type="file"
            accept=".json"
            onChange={handleFileUpload}
            className="hidden"
          />

          <button
            onClick={() => fileInputRef.current?.click()}
            className="flex items-center gap-2 px-4 py-2.5 rounded-2xl bg-sivo-surface-variant dark:bg-sivo-dark-surface-variant hover:bg-sivo-border text-sivo-text-primary dark:text-sivo-dark-text-primary text-xs font-semibold transition-all"
          >
            <Upload className="w-4 h-4" />
            Restore from Backup
          </button>
        </div>
      </div>

      {/* Security & Vault Info */}
      <div className="p-5 rounded-3xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="p-2.5 rounded-2xl bg-pastel-mint-bg text-pastel-mint">
            <ShieldCheck className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-sm font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
              Offline Zero-Trust Security
            </h3>
            <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary">
              Vault data is encrypted with client-side Web Crypto AES-GCM.
            </p>
          </div>
        </div>
      </div>

      {/* Danger Zone: Reset All Data */}
      <div className="p-5 rounded-3xl bg-sivo-error-container/40 dark:bg-sivo-error/10 border border-sivo-error/20 space-y-3">
        <div className="flex items-center gap-2 text-sivo-error">
          <AlertTriangle className="w-4 h-4" />
          <h3 className="text-sm font-bold font-['Outfit']">
            Danger Zone
          </h3>
        </div>
        <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary">
          Clear all locally stored notes, tasks, folders, and vault data from this browser.
        </p>
        <button
          onClick={() => {
            if (window.confirm('Are you sure you want to reset all data? This cannot be undone.')) {
              resetAll();
            }
          }}
          className="flex items-center gap-1.5 px-4 py-2 rounded-2xl bg-sivo-error hover:bg-red-700 text-white text-xs font-semibold transition-all shadow-sm"
        >
          <Trash2 className="w-3.5 h-3.5" />
          Reset All Data
        </button>
      </div>

      {/* About */}
      <div className="text-center pt-4 text-xs text-sivo-text-muted space-y-1">
        <p className="font-semibold text-sivo-text-secondary dark:text-sivo-dark-text-secondary font-['Outfit']">
          SIVO NOTES v1.0 (Web Edition)
        </p>
        <p>Stitch Design ID `10675117566042067319` • Offline First • Zero Cloud Dependency</p>
      </div>
    </div>
  );
};
