# Task Stopwatch / Focus Timer — Implementation Plan

> **App:** SivoNotes (Android · Kotlin · Jetpack Compose · Room)
> **Feature:** Attach a countdown timer to any Todo. Start it, run it, and when it finishes the session is automatically counted toward the streak.

---

## Feature Summary

The user picks a todo, sets a target duration (e.g. "Study Java — 45 min"), starts the stopwatch, and when the session ends the app:
1. Marks a **focus session** as complete for that day.
2. Contributes to the **streak** — same rules as completing a todo.
3. Shows the session in the **Streak & Progress** screen under a new "Focus Sessions" section.

---

## How It Works (User Flow)

```
Todos screen
  └── tap ⏱ icon on a todo card
        └── Focus Timer bottom sheet opens
              ├── Task name shown at top
              ├── Duration picker (5 / 15 / 25 / 30 / 45 / 60 min or custom)
              ├── [Start Timer] button
              └── Timer running state
                    ├── Large countdown display  HH:MM:SS
                    ├── Progress ring (animated)
                    ├── [Pause]  [Stop & Save]  [Abandon] buttons
                    └── On completion → celebration + session saved
```

---

## Phases

---

### Phase 1 — Database Layer

**Files to create / modify:**

#### 1A. New Entity — `FocusSessionEntity.kt`
`app/src/main/java/com/anant/sivonotes/data/local/entity/FocusSessionEntity.kt`

Fields:
| Column | Type | Notes |
|---|---|---|
| `id` | Long (PK) | auto-generated |
| `todoId` | Long? | nullable — session can be independent |
| `todoTitle` | String | snapshot of task name at session time |
| `targetDurationMillis` | Long | what the user set |
| `actualDurationMillis` | Long | how long they actually ran |
| `startedAt` | Long | epoch millis |
| `completedAt` | Long? | null if abandoned |
| `isCompleted` | Boolean | true = ran to end / saved early |
| `isAbandoned` | Boolean | true = user hit abandon |
| `dateKey` | Long | epoch day (startOfDay millis) — used for streak |

#### 1B. New DAO — `FocusSessionDao.kt`
`app/src/main/java/com/anant/sivonotes/data/local/dao/FocusSessionDao.kt`

Queries needed:
- `insertSession(session): Long`
- `getAllSessions(): Flow<List<FocusSessionEntity>>`
- `getSessionsByDate(startOfDay, endOfDay): Flow<List<FocusSessionEntity>>`
- `getCompletedSessionsHistory(): Flow<List<FocusSessionEntity>>` — for streak
- `deleteSession(id)`

#### 1C. Update `AppDatabase.kt`
- Add `FocusSessionEntity` to `@Database(entities = [...])`
- Add `focusSessionDao(): FocusSessionDao` abstract function
- **Bump `version` by 1** and add a migration (or `fallbackToDestructiveMigration` during dev)

---

### Phase 2 — Domain Layer

**Files to create:**

#### 2A. `FocusSessionRepository.kt`
`app/src/main/java/com/anant/sivonotes/data/repository/FocusSessionRepository.kt`

Wraps the DAO. Exposes:
- `insertSession(session: FocusSessionEntity): Long`
- `getAllCompletedSessions(): Flow<List<FocusSessionEntity>>`
- `getSessionsForDay(dayMillis: Long): Flow<List<FocusSessionEntity>>`
- `deleteSession(id: Long)`

#### 2B. Update `StreakEngine.kt`
`app/src/main/java/com/anant/sivonotes/domain/streak/StreakEngine.kt`

Current rule: a day is "active" if at least one todo was completed.

**New rule:** a day is "active" if at least one todo was completed **OR** at least one focus session was completed.

- Add a new `calculateStats(todos, focusSessions)` overload (keep the old one for backward compat or migrate callers).
- Merge both lists into a unified set of "active days" before calculating streak.

#### 2C. `FocusTimerState.kt` (pure domain model, no Android deps)
`app/src/main/java/com/anant/sivonotes/domain/timer/FocusTimerState.kt`

```
sealed class FocusTimerState {
    object Idle : FocusTimerState()
    data class Running(remainingMillis: Long, totalMillis: Long) : FocusTimerState()
    data class Paused(remainingMillis: Long, totalMillis: Long) : FocusTimerState()
    object Completed : FocusTimerState()
    object Abandoned : FocusTimerState()
}
```

---

### Phase 3 — ViewModel Layer

#### 3A. `FocusTimerViewModel.kt`
`app/src/main/java/com/anant/sivonotes/ui/timer/FocusTimerViewModel.kt`

Responsibilities:
- Hold the selected `todoId` and `todoTitle`
- Hold the selected `targetDurationMillis`
- Run a `CountDownTimer` (or `ticker flow` using `kotlinx.coroutines`) inside `viewModelScope`
- Expose `timerState: StateFlow<FocusTimerState>`
- On `start()` → launch coroutine, emit `Running` every second
- On `pause()` → suspend the coroutine ticker
- On `resume()` → restart ticker from remaining
- On `stopAndSave()` → insert a completed session with actual duration
- On `abandon()` → insert an abandoned session (no streak credit)
- On natural `Completed` → auto-insert completed session → emit `Completed`

> Use Kotlin's `tickerFlow` pattern (`flow { while(true) { delay(1000); emit(Unit) } }`) instead of `CountDownTimer` — cleaner cancellation.

---

### Phase 4 — UI Layer

#### 4A. `FocusTimerBottomSheet.kt`
`app/src/main/java/com/anant/sivonotes/ui/timer/FocusTimerBottomSheet.kt`

States to render (driven by `FocusTimerState`):

**Idle / Setup state:**
```
[ Study Java — 1 hour ]          ← todo title

Set duration
 [5m] [15m] [25m] [30m] [45m] [60m]   ← chip row
 [Custom...]                           ← opens time picker dialog

        [ Start Timer ]
```

**Running state:**
```
[ Study Java — 1 hour ]

        00:44:59
     ┌────────────┐
     │  progress  │   ← animated circular ring
     └────────────┘
   45 min focus session

   [⏸ Pause]   [⏹ Stop & Save]   [✕ Abandon]
```

**Paused state:**
```
        00:44:59   ← greyed / pulsing
   [▶ Resume]   [⏹ Stop & Save]   [✕ Abandon]
```

**Completed state:**
```
        🎉  Session Complete!

   You focused for 45 minutes on
   "Study Java — 1 hour"

        [ Done ]   [ Start Another ]
```

Design language: match existing SivoNotes soft lavender/white palette, rounded corners, `ModalBottomSheet`.

#### 4B. Wire timer icon into `TodosScreen.kt`
- Add a `⏱` icon button on each todo card (next to the checkbox and delete button).
- On click → set `selectedTodoId` in shared state / navArgs → open `FocusTimerBottomSheet`.

#### 4C. Update `StreakProgressScreen.kt`
- Add a new section **"Focus Sessions"** below the existing stats.
- Show: total sessions this week, total focus minutes this week, list of recent sessions (task name + duration + date).

---

### Phase 5 — Notifications (Optional but polished)

#### 5A. `FocusTimerNotification.kt`
`app/src/main/java/com/anant/sivonotes/notification/FocusTimerNotification.kt`

- Show a persistent foreground notification while timer is running (so it survives screen-off).
- Notification shows: task name + remaining time.
- Actions: **Pause** / **Stop**.
- Use an `Android ForegroundService` or `WorkManager` if the timer needs to survive process kill (advanced — mark as v2).
- For v1: keep the timer in-process (ViewModel) and only show a notification as a visual aid — acceptable for most users.

> [!NOTE]
> For MVP, in-process timer is fine. If the user backgrounds the app the timer keeps running since `viewModelScope` is tied to the ViewModel lifecycle, not the Activity. Add the foreground service in a follow-up.

---

### Phase 6 — Dependency Injection

`app/src/main/java/com/anant/sivonotes/di/`

- In the existing DI module, provide `FocusSessionDao` from `AppDatabase`.
- Provide `FocusSessionRepository`.
- The `FocusTimerViewModel` will be created with a factory, same pattern as existing ViewModels.

---

### Phase 7 — Navigation

`app/src/main/java/com/anant/sivonotes/navigation/`

- No new nav destination needed — timer is a `ModalBottomSheet`, not a full screen.
- Bottom sheet is triggered by a state variable in `TodosScreen` (`showTimerSheet: Boolean`, `selectedTodoForTimer: TodoEntity?`).

---

## File Checklist

```
NEW FILES
├── data/local/entity/FocusSessionEntity.kt
├── data/local/dao/FocusSessionDao.kt
├── data/repository/FocusSessionRepository.kt
├── domain/timer/FocusTimerState.kt
├── ui/timer/FocusTimerViewModel.kt
├── ui/timer/FocusTimerBottomSheet.kt
└── notification/FocusTimerNotification.kt   (Phase 5)

MODIFIED FILES
├── data/local/AppDatabase.kt                (add entity + DAO + bump version)
├── domain/streak/StreakEngine.kt            (merge focus sessions into streak)
├── ui/streak/StreakProgressViewModel.kt     (feed focus sessions into engine)
├── ui/streak/StreakProgressScreen.kt        (show focus session section)
└── ui/todos/TodosScreen.kt                  (add ⏱ button, bottom sheet trigger)
```

---

## Streak Integration Logic

```
A day is "active" if:
  - At least 1 todo was completed on that day
  OR
  - At least 1 focus session was completed on that day (isCompleted = true, isAbandoned = false)
```

The `dateKey` column on `FocusSessionEntity` stores the start-of-day timestamp so the streak engine can group by day the same way it does for todos using `completedAt`.

---

## Data Flow Summary

```
TodosScreen
  └── ⏱ tap
        └── FocusTimerBottomSheet
              └── FocusTimerViewModel
                    ├── (ticker coroutine) → timerState StateFlow
                    └── on complete/save → FocusSessionRepository.insertSession()
                                                  └── FocusSessionDao → Room → DB

StreakProgressViewModel
  ├── TodosRepository.getCompletedTodosHistory()
  └── FocusSessionRepository.getAllCompletedSessions()
        └── StreakEngine.calculateStats(todos, sessions)
              └── StreakProgressUiState → StreakProgressScreen
```

---

## Execution Order

1. Phase 1 — DB (Entity + DAO + AppDatabase migration)
2. Phase 2 — Domain (Repository + StreakEngine update + FocusTimerState)
3. Phase 3 — ViewModel
4. Phase 4 — UI (BottomSheet → wire into TodosScreen → update StreakScreen)
5. Phase 5 — Notification (optional, can be v2)
6. Phase 6 — DI wiring
7. Phase 7 — Navigation check / integration test

> [!IMPORTANT]
> Follow the existing pattern throughout: ViewModel factory companion objects, Repository wrapping DAO, no business logic in Composables.
