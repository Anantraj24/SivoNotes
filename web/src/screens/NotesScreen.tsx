import React, { useState } from 'react';
import { 
  Search, 
  Plus, 
  Pin, 
  Trash2, 
  Tag as TagIcon, 
  Folder as FolderIcon,
  FileText,
  SlidersHorizontal
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { Note } from '../types';

export const NotesScreen: React.FC = () => {
  const { 
    notes, 
    folders, 
    setEditingNote, 
    setIsNoteEditorOpen, 
    togglePinNote, 
    deleteNote 
  } = useApp();

  const [activeFilter, setActiveFilter] = useState<'all' | 'pinned' | 'recent'>('all');
  const [selectedFolderFilter, setSelectedFolderFilter] = useState<string>('all');
  const [query, setQuery] = useState('');

  // Filter notes
  const filteredNotes = notes.filter((n) => {
    if (activeFilter === 'pinned' && !n.isPinned) return false;
    if (selectedFolderFilter !== 'all' && n.folderId !== selectedFolderFilter) return false;
    if (query.trim()) {
      const q = query.toLowerCase();
      const matchTitle = n.title.toLowerCase().includes(q);
      const matchContent = n.content.toLowerCase().includes(q);
      const matchTags = n.tags.some(t => t.toLowerCase().includes(q));
      if (!matchTitle && !matchContent && !matchTags) return false;
    }
    return true;
  });

  // Sort notes
  const sortedNotes = [...filteredNotes].sort((a, b) => {
    if (activeFilter === 'recent') return b.updatedAt - a.updatedAt;
    // Pinned notes come first in 'all' view
    if (a.isPinned !== b.isPinned) return a.isPinned ? -1 : 1;
    return b.updatedAt - a.updatedAt;
  });

  const pinnedNotes = sortedNotes.filter(n => n.isPinned);
  const otherNotes = sortedNotes.filter(n => !n.isPinned);

  const getFolder = (folderId?: string) => folders.find(f => f.id === folderId);

  const renderNoteCard = (note: Note) => {
    const folder = getFolder(note.folderId);
    return (
      <div
        key={note.id}
        onClick={() => {
          setEditingNote(note);
          setIsNoteEditorOpen(true);
        }}
        className={`p-4 rounded-2xl bg-white dark:bg-sivo-dark-surface border transition-all cursor-pointer flex flex-col justify-between group hover:shadow-sivo-md ${
          note.isPinned 
            ? 'border-sivo-primary/40 dark:border-sivo-primary/30 shadow-sivo-sm bg-gradient-to-br from-white to-sivo-primary-container/20 dark:from-sivo-dark-surface dark:to-sivo-primary/10' 
            : 'border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm hover:border-sivo-primary/40'
        }`}
      >
        <div>
          <div className="flex items-start justify-between gap-2 mb-2">
            <h4 className="text-base font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary group-hover:text-sivo-primary transition-colors line-clamp-1 font-['Outfit']">
              {note.title}
            </h4>
            <div className="flex items-center gap-1 opacity-80 group-hover:opacity-100">
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  togglePinNote(note.id);
                }}
                className={`p-1.5 rounded-lg border transition-all ${
                  note.isPinned 
                    ? 'bg-sivo-primary text-white border-sivo-primary' 
                    : 'text-sivo-text-muted hover:text-sivo-primary border-transparent hover:bg-sivo-surface-variant'
                }`}
                title={note.isPinned ? 'Unpin' : 'Pin'}
              >
                <Pin className="w-3 h-3 rotate-45" />
              </button>
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  deleteNote(note.id);
                }}
                className="p-1.5 rounded-lg text-sivo-text-muted hover:text-sivo-error hover:bg-sivo-error-container/60 transition-all opacity-0 group-hover:opacity-100"
                title="Delete"
              >
                <Trash2 className="w-3 h-3" />
              </button>
            </div>
          </div>

          <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary line-clamp-3 leading-relaxed whitespace-pre-wrap">
            {note.content.replace(/[#*`_]/g, '') || 'No content...'}
          </p>

          {/* Tags */}
          {note.tags.length > 0 && (
            <div className="flex flex-wrap gap-1 mt-3">
              {note.tags.slice(0, 3).map(tag => (
                <span
                  key={tag}
                  className="text-[10px] font-medium px-2 py-0.5 rounded-full bg-sivo-surface-variant dark:bg-sivo-dark-surface-variant text-sivo-text-secondary dark:text-sivo-dark-text-secondary"
                >
                  #{tag}
                </span>
              ))}
              {note.tags.length > 3 && (
                <span className="text-[10px] text-sivo-text-muted self-center">
                  +{note.tags.length - 3}
                </span>
              )}
            </div>
          )}
        </div>

        <div className="flex items-center justify-between mt-4 pt-2.5 border-t border-sivo-border/40 dark:border-sivo-dark-border/40 text-[10px] text-sivo-text-muted">
          {folder ? (
            <span className="px-2 py-0.5 rounded-full bg-sivo-primary-container/80 text-sivo-on-primary-container dark:bg-sivo-primary/20 dark:text-sivo-primary-light font-semibold">
              {folder.name}
            </span>
          ) : (
            <span className="flex items-center gap-1 text-sivo-text-muted">
              <FolderIcon className="w-2.5 h-2.5" /> General
            </span>
          )}
          <span>
            {new Date(note.updatedAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })}
          </span>
        </div>
      </div>
    );
  };

  return (
    <div className="space-y-5 pb-24 max-w-4xl mx-auto px-4 sm:px-6 pt-4 animate-fade-in">
      {/* Top Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold tracking-tight text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
            Notes
          </h2>
          <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary">
            {notes.length} {notes.length === 1 ? 'note' : 'notes'} saved offline
          </p>
        </div>

        <button
          onClick={() => {
            setEditingNote(null);
            setIsNoteEditorOpen(true);
          }}
          className="flex items-center gap-1.5 px-4 py-2 rounded-full bg-sivo-primary hover:bg-sivo-primary-variant text-white text-xs font-semibold shadow-md shadow-sivo-primary/30 transition-all"
        >
          <Plus className="w-4 h-4" />
          New Note
        </button>
      </div>

      {/* Search and Filters */}
      <div className="space-y-3">
        {/* Search */}
        <div className="relative">
          <Search className="w-4 h-4 text-sivo-text-muted absolute left-3.5 top-3" />
          <input
            type="text"
            placeholder="Search notes by title, content, or tags..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            className="w-full pl-10 pr-4 py-2.5 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border text-xs outline-none focus:border-sivo-primary text-sivo-text-primary dark:text-sivo-dark-text-primary shadow-sivo-sm"
          />
        </div>

        {/* Filter Tabs (All | Pinned | Recent) & Folder Filter */}
        <div className="flex flex-wrap items-center justify-between gap-2">
          <div className="flex items-center gap-1 bg-sivo-surface-variant/80 dark:bg-sivo-dark-surface-variant/80 p-1 rounded-2xl">
            {(['all', 'pinned', 'recent'] as const).map((filter) => (
              <button
                key={filter}
                onClick={() => setActiveFilter(filter)}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold capitalize transition-all ${
                  activeFilter === filter
                    ? 'bg-white dark:bg-sivo-dark-surface text-sivo-primary shadow-sivo-sm'
                    : 'text-sivo-text-secondary hover:text-sivo-text-primary'
                }`}
              >
                {filter}
              </button>
            ))}
          </div>

          {/* Folder Pills */}
          <div className="flex items-center gap-1.5 overflow-x-auto no-scrollbar py-1">
            <button
              onClick={() => setSelectedFolderFilter('all')}
              className={`px-3 py-1 rounded-full text-xs font-semibold whitespace-nowrap transition-all ${
                selectedFolderFilter === 'all'
                  ? 'bg-sivo-primary text-white'
                  : 'bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border text-sivo-text-secondary'
              }`}
            >
              All Folders
            </button>
            {folders.map(f => (
              <button
                key={f.id}
                onClick={() => setSelectedFolderFilter(f.id)}
                className={`px-3 py-1 rounded-full text-xs font-semibold whitespace-nowrap transition-all ${
                  selectedFolderFilter === f.id
                    ? 'bg-sivo-primary text-white'
                    : 'bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border text-sivo-text-secondary'
                }`}
              >
                {f.name}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Empty State */}
      {sortedNotes.length === 0 && (
        <div className="p-12 text-center rounded-3xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm my-6">
          <div className="w-12 h-12 rounded-2xl bg-sivo-primary-container text-sivo-primary flex items-center justify-center mx-auto mb-3">
            <FileText className="w-6 h-6" />
          </div>
          <h3 className="text-base font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
            No notes found
          </h3>
          <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary mt-1 mb-4">
            {query ? 'No notes matched your search filter.' : 'Capture your first thought, study guide, or idea.'}
          </p>
          <button
            onClick={() => {
              setEditingNote(null);
              setIsNoteEditorOpen(true);
            }}
            className="px-4 py-2 rounded-full bg-sivo-primary text-white text-xs font-semibold shadow-md"
          >
            Create Note
          </button>
        </div>
      )}

      {/* Notes Grid */}
      {sortedNotes.length > 0 && (
        <div className="space-y-6">
          {activeFilter === 'all' && pinnedNotes.length > 0 && (
            <div>
              <div className="flex items-center gap-1.5 text-xs font-bold uppercase tracking-wider text-sivo-primary mb-3">
                <Pin className="w-3.5 h-3.5 rotate-45" />
                <span>Pinned</span>
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
                {pinnedNotes.map(renderNoteCard)}
              </div>
            </div>
          )}

          {activeFilter === 'all' && pinnedNotes.length > 0 && otherNotes.length > 0 && (
            <div className="flex items-center gap-1.5 text-xs font-bold uppercase tracking-wider text-sivo-text-muted mb-3 pt-2">
              <span>Other Notes</span>
            </div>
          )}

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
            {(activeFilter === 'all' && pinnedNotes.length > 0 ? otherNotes : sortedNotes).map(renderNoteCard)}
          </div>
        </div>
      )}
    </div>
  );
};
