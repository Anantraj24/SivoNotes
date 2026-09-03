# SIVO NOTES --- Antigravity Master Development Document

**Stitch Design ID:** `10675117566042067319`\
**Platform:** Android\
**Product:** Offline-first personal productivity app

## 1. Product Overview

Sivo Notes combines:

-   Notes
-   Important Points
-   Daily Todos
-   Reminders
-   Custom Folders
-   Streaks and Progress
-   Secure Vault for passwords and private notes
-   Global Search
-   Settings
-   Local Backup/Export

Core product idea:

> Capture information quickly, organize it naturally, remember important
> things, complete daily tasks, and protect sensitive information.

The app must work offline for all core features. No account or internet
connection should be required for normal use.

## 2. UI/UX Source of Truth

Use **Stitch Design ID `10675117566042067319`** as the primary UI/UX
reference.

The implementation should preserve the Stitch visual language:

-   Soft warm-white/lavender foundation
-   Soft pastel functional accents
-   Dark charcoal typography
-   Large readable headings
-   Rounded cards
-   Subtle shadows
-   Generous whitespace
-   Clean modern Android UI
-   Rounded search fields
-   Floating bottom navigation
-   Soft active navigation state
-   Friendly premium appearance
-   Clear visual hierarchy

Do not introduce neon colors, heavy gradients, excessive glassmorphism,
dense dashboards, tiny text, or a completely different design system.

## 3. UX Principles

1.  **Fast:** creating a note, point, todo, or reminder should take
    seconds.
2.  **Simple:** avoid unnecessary fields and options.
3.  **Organized:** users create their own folders.
4.  **Offline-first:** core features work without internet.
5.  **Private:** vault data is protected and hidden from normal search
    while locked.
6.  **Consistent:** all screens use the same design system.
7.  **One-handed:** important actions should have comfortable touch
    targets.

The desired feeling is:

**OPEN → CAPTURE → ORGANIZE → DONE**

## 4. Navigation

Primary bottom navigation:

`Home | Notes | Todos | Folders`

Vault is not a permanent bottom-nav destination. Access it from Home or
other secure entry points.

High-level structure:

``` text
SIVO NOTES
├── Home
│   ├── Quick Note
│   ├── Today's Todos
│   ├── Important Points
│   ├── Reminders
│   ├── Streak
│   └── Vault
├── Notes
│   └── Note Editor
├── Todos
│   ├── Todo Create/Edit
│   └── Streak & Progress
├── Folders
│   └── Folder View
└── Vault
    ├── Unlock
    ├── Setup
    ├── Password Entry
    └── Private Note
```

## 5. Home Dashboard

Home should provide an immediate overview without becoming crowded.

Include:

-   Greeting
-   Notification button
-   Global search
-   Quick actions
-   Today's todo progress
-   Current streak
-   Upcoming reminder
-   Recent notes
-   Vault shortcut
-   Floating add button
-   Bottom navigation

Example content:

``` text
Good evening, Anant
Let's get things done.

Search anything...

Quick Actions
Quick Note
Today's Todos
Important Points
Reminders

Today's Progress
6 of 8 completed

🔥 7 day streak

Next Reminder
Submit assignment — 10:00 AM

Recent Notes
Java Important Questions
SIH Project Ideas

🔐 Private Vault
```

Do not put every feature on the dashboard. Use hierarchy and whitespace.

## 6. Notes

Users can:

-   Create
-   Edit
-   Delete
-   Pin
-   Search
-   Filter
-   Sort
-   Move to folder
-   Add tags

Filters:

`All | Pinned | Recent`

Example note card:

``` text
Java Important Questions
ArrayList, HashMap, inheritance...
College • 2h ago
```

## 7. Note Editor

Make this distraction-free, not a form.

Include:

-   Back
-   Auto-save status
-   Title
-   Content editor
-   Bold/italic
-   Bullets
-   Checklist
-   Attachment option
-   Tags
-   Folder
-   Pin
-   More menu

Auto-save is preferred. Do not require an annoying Save button.

## 8. Important Points

This is intentionally different from normal Notes.

Purpose: store small facts or items that the user wants to remember.

Example:

``` text
⭐ Exam Important

☐ Java String is immutable
☐ Array index starts from 0
☑ HashMap allows one null key
☐ OSI model has 7 layers
```

Completed points remain visible with a checkmark, subtle strikethrough,
and reduced emphasis.

Adding a point should be extremely fast.

## 9. Todos

Daily actionable tasks.

Example:

``` text
TODAY

☑ Complete DSA questions
☑ Read 10 pages
☐ Study Java — 1 hour
☐ Work on project
☐ Buy groceries
```

Completed tasks remain visible:

`☑ ~~Complete DSA questions~~`

Todo properties:

-   Title
-   Description
-   Date
-   Time
-   Repeat
-   Priority
-   Folder
-   Optional reminder
-   Completion state

## 10. Recurring Todos

Support:

-   Once
-   Daily
-   Weekdays
-   Weekly
-   Custom

Examples:

``` text
Study DSA — Every day
Workout — Mon/Wed/Fri
Weekly revision — Every Sunday
```

Recurring task instances must be generated correctly and historical
completions must remain accurate.

## 11. Todo Create/Edit

Fields:

-   Task
-   Description
-   Date
-   Time
-   Repeat
-   Priority
-   Folder
-   Reminder

Only the task title should normally be required.

## 12. Streak & Progress

Display:

``` text
🔥 7
Current Streak

Best Streak
21 days
```

Also show:

-   Monthly calendar
-   Weekly completion
-   Tasks completed
-   Completion percentage
-   Current streak
-   Best streak

Example:

``` text
24 / 30
Tasks completed

80%
Completion rate
```

For the initial implementation, a simple rule can be used:

**Completing at least one todo on a day makes that day active.**

Keep the best streak historically. Do not make the UX excessively
punishing.

## 13. Reminders

Reminders are local time-based notifications.

Example:

``` text
TODAY
🔔 Submit assignment — 10:00 AM
🔔 Buy groceries — 6:00 PM

TOMORROW
🔔 College meeting — 9:30 AM
```

Actions:

-   Complete
-   Snooze
-   Edit
-   Delete

Core reminders must work offline.

## 14. Reminder Create/Edit

Fields:

-   Reminder title
-   Date
-   Time
-   Repeat
-   Optional note

Repeat:

-   Never
-   Daily
-   Weekly
-   Monthly
-   Custom

Primary action: `Set Reminder`

Use local Android scheduling.

## 15. Folders

Folders are fully user-defined.

Examples:

``` text
📚 College
🏠 Home
🛒 Grocery
💻 Projects
⭐ Important
📖 Learning
```

Users can:

-   Create
-   Rename
-   Delete
-   Choose icon
-   Choose subtle accent
-   Move content

A folder can contain multiple content types, not only notes.

## 16. Folder View

Example:

``` text
College

NOTES
Java Important Questions
DBMS Notes

TODOS
Complete Assignment
Revise DBMS

IMPORTANT POINTS
Normalization
ACID Properties
```

Keep content type hierarchy clear.

## 17. Global Search

Search normal app content:

-   Notes
-   Todos
-   Important Points
-   Reminders
-   Folder names

Example query `java`:

``` text
NOTES
Java Important Questions

TODOS
Study Java — 1 hour

IMPORTANT POINTS
Java String is immutable

REMINDERS
Java Exam

FOLDERS
College
```

**Critical security rule:** locked Vault content must never appear in
global search.

Vault has its own secure search after unlocking.

## 18. Secure Vault

Vault contains:

1.  Passwords
2.  Private Notes

It must use real security, not simply a hidden folder.

Requirements:

-   Master PIN/password
-   Optional biometric unlock
-   Auto-lock
-   Encrypted local storage
-   Android Keystore for key protection
-   No sensitive data in logs
-   No vault content in normal search while locked
-   No sensitive content in notifications

Never store the master credential as plaintext.

## 19. Vault Locked Screen

Show:

``` text
🔐
Vault Locked

Your private information is protected.

[ Unlock Vault ]

Use Biometrics
```

Never display private entries before authentication.

## 20. Vault Setup

First-time flow:

``` text
Secure Your Vault

Create Master PIN
••••

Confirm Master PIN
••••

Enable biometric unlock

Your vault is encrypted and
stored locally.

[ Create Vault ]
```

Explain that losing the master credential may prevent recovery of
encrypted data.

## 21. Password Entry

Fields:

-   Website/App
-   Username
-   Password
-   Website URL
-   Notes

Password is masked by default.

Support:

-   Show/hide
-   Copy
-   Generate password

Never log passwords. Avoid unnecessary clipboard persistence.

## 22. Private Note

Private notes use the normal note-editor philosophy but live inside the
encrypted Vault.

Show:

`🔐 Encrypted`

Include:

-   Auto-save
-   Text editing
-   Basic formatting
-   Tags
-   Attachments later

Content remains protected while Vault is locked.

## 23. Settings

### General

-   Appearance
-   Default folder
-   Start screen
-   Date format

### Notifications

-   Todo notifications
-   Reminder notifications
-   Notification sound

### Security

-   Change PIN
-   Biometric unlock
-   Auto-lock
-   Vault settings

### Data

-   Backup
-   Export
-   Import
-   Restore

### About

-   Version
-   Privacy
-   Licenses

Keep settings grouped and simple.

## 24. Onboarding

Maximum 3 short onboarding states.

### Welcome

``` text
SIVO NOTES

Your notes.
Your tasks.
Your private space.

[ Get Started ]
```

### Offline

``` text
Everything Offline

Notes, todos and reminders
work without internet.

[ Continue ]
```

### Security

``` text
Private by Design

Keep passwords and private
notes inside your encrypted vault.

[ Start Using Sivo Notes ]
```

No long tutorial.

## 25. Universal Add Action

Use a consistent global `+` action.

Options:

``` text
+ New

📝 Note
☑ Todo
🔔 Reminder
⭐ Important Point
📁 Folder
🔐 Vault Entry
```

Use a bottom sheet or expansion consistent with the Stitch design.

## 26. Suggested Android Stack

Recommended:

-   Kotlin
-   Jetpack Compose
-   Room
-   ViewModel
-   Repository pattern
-   Kotlin Coroutines / Flow
-   AlarmManager
-   Android Notifications
-   Android Keystore
-   BiometricPrompt

Architecture:

``` text
Compose UI
    ↓
ViewModel
    ↓
Repository
    ↓
Local Data Source
    ↓
Room
```

Vault:

``` text
Vault UI
    ↓
Vault Repository
    ↓
Encryption Layer
    ↓
Android Keystore / secure key storage
    ↓
Encrypted local data
```

Do not put business logic directly into Compose UI.

## 27. Offline Requirements

These must work without internet:

-   Create/edit/delete notes
-   Search notes
-   Create/complete todos
-   Recurring todos
-   Streak calculation
-   Create/edit reminders
-   Local notifications
-   Create/manage folders
-   Move content
-   Vault authentication
-   Password entries
-   Private notes
-   Normal local search

Do not make a network request part of any core workflow.

## 28. Data Model

Conceptual entities:

``` text
AppSettings
Folder
Note
ImportantPoint
Todo
TodoOccurrence
Reminder
Vault
VaultEntry
PrivateNote
Tag
```

The app does not need a traditional online user account for the core
MVP.

## 29. Backup / Export

Later MVP capability:

-   JSON export for structured normal data
-   Markdown/text export for normal notes
-   Encrypted backup for sensitive data

Never export vault passwords as unencrypted plain text by default.

## 30. Notifications

Todo/reminder notifications are local Android notifications.

Never put private information in notifications.

Good:

`Private Vault reminder`

Bad:

`Your GitHub password is ...`

## 31. Accessibility

Implement:

-   Readable text
-   Good contrast
-   44dp+ touch targets
-   Content descriptions
-   Screen-reader labels
-   Proper focus
-   System font scaling
-   Do not rely on color alone

## 32. Empty States

Notes:

``` text
No notes yet.
Capture your first thought.
[ Create Note ]
```

Todos:

``` text
You're all caught up.
Add a task for today.
[ Add Todo ]
```

Reminders:

``` text
No upcoming reminders.
[ Add Reminder ]
```

Folders:

``` text
No folders yet.
Create one to organize your content.
[ Create Folder ]
```

Vault:

``` text
Your private space is empty.
Add a password or private note.
```

## 33. Error Handling

Examples:

Save failure:

``` text
Couldn't save changes.
Try again.
```

Reminder failure:

``` text
Couldn't schedule reminder.
Please check notification permissions.
```

Authentication failure:

``` text
Incorrect PIN.
Try again.
```

Never show technical stack traces to users.

## 34. Performance

Priorities:

-   Fast startup
-   Immediate local note opening
-   Responsive local search
-   Non-blocking database operations
-   Smooth large lists
-   Lazy lists where appropriate
-   Avoid unnecessary Compose recompositions

## 35. Development Rules for Antigravity

Before modifying code:

1.  Inspect the existing project structure.
2.  Inspect dependencies.
3.  Inspect navigation.
4.  Inspect database/data layer.
5.  Inspect resources.
6.  Identify existing functionality.
7.  Reuse working code where appropriate.
8.  Do not blindly rewrite the project.

Use Stitch Design ID:

`10675117566042067319`

as the visual source of truth.

If a component is not explicitly shown, create it so that it matches the
existing Sivo Notes design system.

Do not add features just because they are technically possible.

Keep Notes, Todos, Reminders, Folders and Vault logically separated.

## 36. Implementation Priority

### Phase 1 --- Foundation

-   Android project
-   Compose design system
-   Navigation
-   Room
-   Folder model
-   Note model
-   Todo model
-   Reminder model

### Phase 2 --- Core UX

-   Home
-   Notes
-   Note editor
-   Important Points
-   Todos
-   Todo creation
-   Folders
-   Folder view

### Phase 3 --- Productivity

-   Reminders
-   Local notifications
-   Recurring todos
-   Streaks
-   Progress screen
-   Search

### Phase 4 --- Security

-   Vault setup
-   PIN authentication
-   Biometrics
-   Encryption
-   Password entries
-   Private notes
-   Auto-lock

### Phase 5 --- Data

-   Export
-   Import
-   Backup
-   Restore

## 37. Do Not Build Initially

Do not add:

-   Social features
-   Collaboration
-   Public profiles
-   Chat
-   AI assistant
-   Cloud sync
-   Complex accounts
-   Web app
-   Desktop app
-   Payments/subscriptions
-   Complex rich media editor
-   Excessive animations

Focus on the offline core.

## 38. Definition of Done

### Notes

-   Create
-   Edit
-   Auto-save
-   Delete
-   Search
-   Folder organization

### Important Points

-   Create
-   Complete
-   Strikethrough
-   Organize

### Todos

-   Create
-   Complete
-   Strikethrough
-   Due date
-   Repeat
-   Notification
-   Streak

### Reminders

-   Create
-   Schedule
-   Repeat
-   Notification
-   Complete
-   Snooze

### Folders

-   Create
-   Rename
-   Delete
-   Organize mixed content
-   Open folder

### Vault

-   Setup
-   PIN
-   Biometrics
-   Encryption
-   Password entry
-   Private notes
-   Auto-lock
-   Locked-state protection

### General

-   Data persists after restart
-   Core features work offline
-   Search is local
-   UI follows Stitch
-   No obvious crashes
-   Sensitive information is not logged or exposed

## 39. Final Product Vision

Sivo Notes should feel like:

> **My personal offline space for everything I need to remember, do,
> organize and protect.**

The product priorities are:

**Fast → Simple → Offline → Organized → Private**

Build these extremely well before adding advanced features.

## 40. Instruction to Antigravity

Treat this document as the **product and implementation specification**.

Do not start by generating random screens or rewriting the entire
project.

First inspect the project, compare its current state against this
specification, identify what already exists, and then implement the
missing functionality incrementally.

The Stitch design with ID `10675117566042067319` is the primary UI/UX
reference.

Maintain one coherent Sivo Notes design system across the entire
application.
