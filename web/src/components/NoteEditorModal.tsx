import React, { useState, useEffect } from 'react';
import { 
  X, 
  Pin, 
  Trash2, 
  Tag as TagIcon, 
  Folder as FolderIcon, 
  Bold, 
  Italic, 
  List, 
  CheckSquare, 
  Heading1, 
  Check, 
  Clock 
} from 'lucide-react';
import { useApp } from '../context/AppContext';

export const NoteEditorModal: React.FC = () => {
  const { 
    isNoteEditorOpen, 
    setIsNoteEditorOpen, 
    editingNote, 
    addNote, 
    updateNote, 
    deleteNote, 
    folders 
  } = useApp();

  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [folderId, setFolderId] = useState<string>('');
  const [tags, setTags] = useState<string[]>([]);
  const [tagInput, setTagInput] = useState('');
  const [isPinned, setIsPinned] = useState(false);
  const [activeNoteId, setActiveNoteId] = useState<string | null>(null);
  const [saveStatus, setSaveStatus] = useState<'saved' | 'saving'>('saved');

  // Initialize or reset when opened
  useEffect(() => {
    if (isNoteEditorOpen) {
      if (editingNote) {
        setTitle(editingNote.title);
        setContent(editingNote.content);
        setFolderId(editingNote.folderId || '');
        setTags(editingNote.tags || []);
        setIsPinned(editingNote.isPinned || false);
        setActiveNoteId(editingNote.id);
      } else {
        setTitle('');
        setContent('');
        setFolderId('');
        setTags([]);
        setIsPinned(false);
        setActiveNoteId(null);
      }
    }
  }, [isNoteEditorOpen, editingNote]);

  // Auto-save logic
  useEffect(() => {
    if (!isNoteEditorOpen) return;
    if (!title && !content) return;

    setSaveStatus('saving');
    const timer = setTimeout(() => {
      if (activeNoteId) {
        updateNote(activeNoteId, {
          title: title || 'Untitled Note',
          content,
          folderId: folderId || undefined,
          tags,
          isPinned,
        });
      } else {
        const created = addNote(title, content, folderId || undefined, tags, isPinned);
        setActiveNoteId(created.id);
      }
      setSaveStatus('saved');
    }, 600);

    return () => clearTimeout(timer);
  }, [title, content, folderId, tags, isPinned]);

  if (!isNoteEditorOpen) return null;

  const handleAddTag = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && tagInput.trim()) {
      e.preventDefault();
      if (!tags.includes(tagInput.trim())) {
        setTags([...tags, tagInput.trim()]);
      }
      setTagInput('');
    }
  };

  const removeTag = (tagToRemove: string) => {
    setTags(tags.filter(t => t !== tagToRemove));
  };

  const insertFormat = (prefix: string, suffix = '') => {
    const textarea = document.getElementById('note-content-area') as HTMLTextAreaElement;
    if (!textarea) return;
    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const selected = content.substring(start, end);
    const replacement = prefix + (selected || 'text') + suffix;
    const newContent = content.substring(0, start) + replacement + content.substring(end);
    setContent(newContent);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-2 sm:p-4 bg-black/50 backdrop-blur-sm animate-fade-in">
      <div className="w-full max-w-2xl h-[90vh] bg-white dark:bg-sivo-dark-surface rounded-3xl border border-sivo-border dark:border-sivo-dark-border shadow-2xl flex flex-col overflow-hidden animate-slide-up">
        
        {/* Top bar */}
        <div className="flex items-center justify-between px-5 py-3.5 border-b border-sivo-border/60 dark:border-sivo-dark-border/60">
          <div className="flex items-center gap-2">
            <button
              onClick={() => setIsPinned(!isPinned)}
              className={`p-2 rounded-xl border transition-all ${
                isPinned 
                  ? 'bg-sivo-primary text-white border-sivo-primary shadow-sm' 
                  : 'text-sivo-text-secondary dark:text-sivo-dark-text-secondary border-sivo-border dark:border-sivo-dark-border hover:bg-sivo-surface-variant'
              }`}
              title={isPinned ? 'Unpin Note' : 'Pin Note'}
            >
              <Pin className="w-4 h-4" />
            </button>

            <span className="flex items-center gap-1.5 text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary">
              <Clock className="w-3.5 h-3.5 text-sivo-primary" />
              {saveStatus === 'saved' ? 'Saved offline' : 'Saving...'}
            </span>
          </div>

          <div className="flex items-center gap-1">
            {activeNoteId && (
              <button
                onClick={() => {
                  deleteNote(activeNoteId);
                  setIsNoteEditorOpen(false);
                }}
                className="p-2 rounded-xl text-sivo-error hover:bg-sivo-error-container dark:hover:bg-sivo-error/20 transition-all"
                title="Delete Note"
              >
                <Trash2 className="w-4 h-4" />
              </button>
            )}
            <button
              onClick={() => setIsNoteEditorOpen(false)}
              className="p-2 rounded-xl text-sivo-text-secondary dark:text-sivo-dark-text-secondary hover:bg-sivo-surface-variant dark:hover:bg-sivo-dark-surface-variant"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Content Body */}
        <div className="flex-1 overflow-y-auto p-6 space-y-4">
          {/* Title */}
          <input
            type="text"
            placeholder="Note Title..."
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="w-full text-2xl font-bold bg-transparent border-none outline-none placeholder:text-sivo-text-muted dark:placeholder:text-sivo-dark-text-muted text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']"
            autoFocus
          />

          {/* Folder and Tags row */}
          <div className="flex flex-wrap items-center gap-2 pt-1 pb-2 border-b border-sivo-border/40 dark:border-sivo-dark-border/40">
            {/* Folder Select */}
            <div className="flex items-center gap-1.5 bg-sivo-surface-variant/70 dark:bg-sivo-dark-surface-variant/70 px-2.5 py-1 rounded-xl text-xs">
              <FolderIcon className="w-3.5 h-3.5 text-sivo-primary" />
              <select
                value={folderId}
                onChange={(e) => setFolderId(e.target.value)}
                className="bg-transparent text-sivo-text-primary dark:text-sivo-dark-text-primary outline-none cursor-pointer"
              >
                <option value="">No Folder (General)</option>
                {folders.map(f => (
                  <option key={f.id} value={f.id}>{f.name}</option>
                ))}
              </select>
            </div>

            {/* Tag Pills */}
            {tags.map(t => (
              <span 
                key={t}
                className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-medium bg-sivo-primary-container text-sivo-on-primary-container dark:bg-sivo-primary/20 dark:text-sivo-primary-light"
              >
                #{t}
                <button onClick={() => removeTag(t)} className="hover:text-red-500">×</button>
              </span>
            ))}

            {/* Add Tag Input */}
            <div className="inline-flex items-center gap-1 bg-sivo-surface-variant/40 dark:bg-sivo-dark-surface-variant/40 px-2.5 py-1 rounded-xl text-xs">
              <TagIcon className="w-3 h-3 text-sivo-text-muted" />
              <input
                type="text"
                placeholder="Add tag (Press Enter)..."
                value={tagInput}
                onChange={(e) => setTagInput(e.target.value)}
                onKeyDown={handleAddTag}
                className="bg-transparent outline-none w-32 placeholder:text-sivo-text-muted text-sivo-text-primary dark:text-sivo-dark-text-primary"
              />
            </div>
          </div>

          {/* Markdown Quick Toolbar */}
          <div className="flex items-center gap-1 p-1 rounded-xl bg-sivo-surface-variant/40 dark:bg-sivo-dark-surface-variant/40 text-sivo-text-secondary dark:text-sivo-dark-text-secondary text-xs">
            <button
              onClick={() => insertFormat('**', '**')}
              className="p-1.5 hover:bg-white dark:hover:bg-sivo-dark-surface rounded-lg transition-all"
              title="Bold"
            >
              <Bold className="w-3.5 h-3.5" />
            </button>
            <button
              onClick={() => insertFormat('*', '*')}
              className="p-1.5 hover:bg-white dark:hover:bg-sivo-dark-surface rounded-lg transition-all"
              title="Italic"
            >
              <Italic className="w-3.5 h-3.5" />
            </button>
            <button
              onClick={() => insertFormat('### ')}
              className="p-1.5 hover:bg-white dark:hover:bg-sivo-dark-surface rounded-lg transition-all"
              title="Heading"
            >
              <Heading1 className="w-3.5 h-3.5" />
            </button>
            <button
              onClick={() => insertFormat('- ')}
              className="p-1.5 hover:bg-white dark:hover:bg-sivo-dark-surface rounded-lg transition-all"
              title="Bullet list"
            >
              <List className="w-3.5 h-3.5" />
            </button>
            <button
              onClick={() => insertFormat('- [ ] ')}
              className="p-1.5 hover:bg-white dark:hover:bg-sivo-dark-surface rounded-lg transition-all"
              title="Checklist item"
            >
              <CheckSquare className="w-3.5 h-3.5" />
            </button>
          </div>

          {/* Content TextArea */}
          <textarea
            id="note-content-area"
            placeholder="Start typing your thoughts, notes, checklist, or study points..."
            value={content}
            onChange={(e) => setContent(e.target.value)}
            className="w-full h-80 bg-transparent border-none outline-none resize-none text-sivo-text-primary dark:text-sivo-dark-text-primary text-sm leading-relaxed placeholder:text-sivo-text-muted dark:placeholder:text-sivo-dark-text-muted"
          />
        </div>

        {/* Footer Done */}
        <div className="p-4 border-t border-sivo-border/60 dark:border-sivo-dark-border/60 flex justify-end">
          <button
            onClick={() => setIsNoteEditorOpen(false)}
            className="flex items-center gap-1.5 px-5 py-2.5 rounded-full bg-sivo-primary hover:bg-sivo-primary-variant text-white text-sm font-semibold shadow-md shadow-sivo-primary/30 transition-all"
          >
            <Check className="w-4 h-4" />
            Done
          </button>
        </div>
      </div>
    </div>
  );
};
