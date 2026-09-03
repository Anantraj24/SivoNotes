import React from 'react';
import { AppProvider, useApp } from './context/AppContext';
import { Header } from './components/Header';
import { Navigation } from './components/Navigation';
import { UniversalAddSheet } from './components/UniversalAddSheet';
import { NoteEditorModal } from './components/NoteEditorModal';
import { TodoModal } from './components/TodoModal';
import { ReminderModal } from './components/ReminderModal';
import { PointModal } from './components/PointModal';
import { FolderModal } from './components/FolderModal';
import { StreakModal } from './components/StreakModal';

// Screens
import { HomeScreen } from './screens/HomeScreen';
import { NotesScreen } from './screens/NotesScreen';
import { TodosScreen } from './screens/TodosScreen';
import { ImportantPointsScreen } from './screens/ImportantPointsScreen';
import { RemindersScreen } from './screens/RemindersScreen';
import { FoldersScreen } from './screens/FoldersScreen';
import { VaultScreen } from './screens/VaultScreen';
import { SearchScreen } from './screens/SearchScreen';
import { SettingsScreen } from './screens/SettingsScreen';

const MainContent: React.FC = () => {
  const { activeTab } = useApp();

  return (
    <div className="min-h-screen flex flex-col bg-sivo-bg dark:bg-sivo-dark-bg text-sivo-text-primary dark:text-sivo-dark-text-primary">
      <Header />
      
      <main className="flex-1 max-w-4xl w-full mx-auto">
        {activeTab === 'home' && <HomeScreen />}
        {activeTab === 'notes' && <NotesScreen />}
        {activeTab === 'todos' && <TodosScreen />}
        {activeTab === 'points' && <ImportantPointsScreen />}
        {activeTab === 'reminders' && <RemindersScreen />}
        {activeTab === 'folders' && <FoldersScreen />}
        {activeTab === 'vault' && <VaultScreen />}
        {activeTab === 'search' && <SearchScreen />}
        {activeTab === 'settings' && <SettingsScreen />}
      </main>

      {/* Floating Bottom Navigation */}
      <Navigation />

      {/* Global Modals and Sheets */}
      <UniversalAddSheet />
      <NoteEditorModal />
      <TodoModal />
      <ReminderModal />
      <PointModal />
      <FolderModal />
      <StreakModal />
    </div>
  );
};

export function App() {
  return (
    <AppProvider>
      <MainContent />
    </AppProvider>
  );
}

export default App;
