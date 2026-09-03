package com.anant.sivonotes.ui.main

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.anant.sivonotes.SivoNotesApplication
import com.anant.sivonotes.navigation.Screen
import com.anant.sivonotes.ui.components.FloatingBottomBar
import com.anant.sivonotes.ui.components.UniversalAddSheet
import com.anant.sivonotes.ui.folders.FolderDetailScreen
import com.anant.sivonotes.ui.folders.FolderDetailViewModel
import com.anant.sivonotes.ui.folders.FoldersScreen
import com.anant.sivonotes.ui.folders.FoldersViewModel
import com.anant.sivonotes.ui.folders.components.CreateFolderDialog
import com.anant.sivonotes.ui.home.HomeScreen
import com.anant.sivonotes.ui.home.HomeViewModel
import com.anant.sivonotes.ui.notes.NotesScreen
import com.anant.sivonotes.ui.notes.NotesViewModel
import com.anant.sivonotes.ui.notes.editor.NoteEditorScreen
import com.anant.sivonotes.ui.notes.editor.NoteEditorViewModel
import com.anant.sivonotes.ui.points.ImportantPointsScreen
import com.anant.sivonotes.ui.points.ImportantPointsViewModel
import com.anant.sivonotes.ui.reminders.RemindersScreen
import com.anant.sivonotes.ui.reminders.RemindersViewModel
import com.anant.sivonotes.ui.reminders.components.CreateReminderDialog
import com.anant.sivonotes.ui.search.SearchScreen
import com.anant.sivonotes.ui.search.SearchViewModel
import com.anant.sivonotes.ui.streak.StreakProgressScreen
import com.anant.sivonotes.ui.streak.StreakProgressViewModel
import com.anant.sivonotes.ui.todos.TodosScreen
import com.anant.sivonotes.ui.todos.TodosViewModel
import com.anant.sivonotes.ui.todos.components.CreateTodoDialog

@Composable
fun MainAppScaffold() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val context = LocalContext.current
    val app = context.applicationContext as SivoNotesApplication
    val container = app.container

    var showUniversalAddSheet by remember { mutableStateOf(false) }
    var showQuickTodoDialog by remember { mutableStateOf(false) }
    var showQuickReminderDialog by remember { mutableStateOf(false) }
    var showQuickFolderDialog by remember { mutableStateOf(false) }

    // Check if current route is a root navigation tab where bottom bar should be shown
    val isBottomBarVisible = when (currentRoute) {
        Screen.Home.route,
        Screen.Notes.route,
        Screen.Todos.route,
        Screen.Folders.route -> true
        else -> false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() },
            modifier = Modifier.fillMaxSize()
        ) {
            // Home Dashboard
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.provideFactory(
                        notesRepository = container.notesRepository,
                        todosRepository = container.todosRepository,
                        remindersRepository = container.remindersRepository,
                        foldersRepository = container.foldersRepository,
                        pointsRepository = container.importantPointsRepository
                    )
                )
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToNotes = { navController.navigate(Screen.Notes.route) },
                    onNavigateToTodos = { navController.navigate(Screen.Todos.route) },
                    onNavigateToPoints = { navController.navigate(Screen.ImportantPoints.createRoute()) },
                    onNavigateToReminders = { navController.navigate(Screen.Reminders.route) },
                    onNavigateToVault = { navController.navigate(Screen.VaultLocked.route) },
                    onNavigateToStreak = { navController.navigate(Screen.StreakProgress.route) },
                    onNavigateToSearch = { navController.navigate(Screen.GlobalSearch.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNoteClick = { noteId -> navController.navigate(Screen.NoteEditor.createRoute(noteId = noteId)) }
                )
            }

            // Notes List
            composable(Screen.Notes.route) {
                val notesViewModel: NotesViewModel = viewModel(
                    factory = NotesViewModel.provideFactory(
                        notesRepository = container.notesRepository,
                        foldersRepository = container.foldersRepository
                    )
                )
                NotesScreen(
                    viewModel = notesViewModel,
                    onNoteClick = { noteId -> navController.navigate(Screen.NoteEditor.createRoute(noteId = noteId)) },
                    onCreateNoteClick = { navController.navigate(Screen.NoteEditor.createRoute()) }
                )
            }

            // Note Editor
            composable(
                route = Screen.NoteEditor.route,
                arguments = listOf(
                    navArgument("noteId") {
                        type = NavType.StringType
                        defaultValue = "-1"
                    },
                    navArgument("folderId") {
                        type = NavType.StringType
                        defaultValue = "-1"
                    }
                )
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getString("noteId")?.toLongOrNull()?.takeIf { it > 0 } ?: 0L
                val folderId = backStackEntry.arguments?.getString("folderId")?.toLongOrNull()?.takeIf { it > 0 }
                val editorViewModel: NoteEditorViewModel = viewModel(
                    factory = NoteEditorViewModel.provideFactory(
                        noteId = noteId,
                        folderId = folderId,
                        notesRepository = container.notesRepository,
                        foldersRepository = container.foldersRepository
                    )
                )
                NoteEditorScreen(
                    viewModel = editorViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // Todos List
            composable(Screen.Todos.route) {
                val todosViewModel: TodosViewModel = viewModel(
                    factory = TodosViewModel.provideFactory(
                        todosRepository = container.todosRepository,
                        foldersRepository = container.foldersRepository
                    )
                )
                TodosScreen(
                    viewModel = todosViewModel,
                    onNavigateToStreak = { navController.navigate(Screen.StreakProgress.route) }
                )
            }

            // Folders List
            composable(Screen.Folders.route) {
                val foldersViewModel: FoldersViewModel = viewModel(
                    factory = FoldersViewModel.provideFactory(
                        foldersRepository = container.foldersRepository,
                        notesRepository = container.notesRepository,
                        todosRepository = container.todosRepository,
                        pointsRepository = container.importantPointsRepository
                    )
                )
                FoldersScreen(
                    viewModel = foldersViewModel,
                    onFolderClick = { folderId ->
                        navController.navigate(Screen.FolderDetail.createRoute(folderId))
                    }
                )
            }

            // Folder Detail
            composable(
                route = Screen.FolderDetail.route,
                arguments = listOf(navArgument("folderId") { type = NavType.LongType })
            ) { backStackEntry ->
                val folderId = backStackEntry.arguments?.getLong("folderId") ?: 0L
                val detailViewModel: FolderDetailViewModel = viewModel(
                    factory = FolderDetailViewModel.provideFactory(
                        folderId = folderId,
                        foldersRepository = container.foldersRepository,
                        notesRepository = container.notesRepository,
                        todosRepository = container.todosRepository,
                        pointsRepository = container.importantPointsRepository
                    )
                )
                FolderDetailScreen(
                    viewModel = detailViewModel,
                    onBack = { navController.popBackStack() },
                    onNoteClick = { noteId -> navController.navigate(Screen.NoteEditor.createRoute(noteId = noteId)) }
                )
            }

            // Important Points
            composable(
                route = Screen.ImportantPoints.route,
                arguments = listOf(
                    navArgument("folderId") {
                        type = NavType.StringType
                        defaultValue = "-1"
                    }
                )
            ) { backStackEntry ->
                val folderId = backStackEntry.arguments?.getString("folderId")?.toLongOrNull()?.takeIf { it > 0 }
                val pointsViewModel: ImportantPointsViewModel = viewModel(
                    factory = ImportantPointsViewModel.provideFactory(
                        folderId = folderId,
                        pointsRepository = container.importantPointsRepository,
                        foldersRepository = container.foldersRepository
                    )
                )
                ImportantPointsScreen(
                    viewModel = pointsViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // Reminders
            composable(Screen.Reminders.route) {
                val remindersViewModel: RemindersViewModel = viewModel(
                    factory = RemindersViewModel.provideFactory(
                        remindersRepository = container.remindersRepository,
                        context = context
                    )
                )
                RemindersScreen(
                    viewModel = remindersViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // Streak & Progress
            composable(Screen.StreakProgress.route) {
                val streakViewModel: StreakProgressViewModel = viewModel(
                    factory = StreakProgressViewModel.provideFactory(
                        todosRepository = container.todosRepository
                    )
                )
                StreakProgressScreen(
                    viewModel = streakViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // Global Search
            composable(Screen.GlobalSearch.route) {
                val searchViewModel: SearchViewModel = viewModel(
                    factory = SearchViewModel.provideFactory(
                        notesRepository = container.notesRepository,
                        todosRepository = container.todosRepository,
                        pointsRepository = container.importantPointsRepository,
                        foldersRepository = container.foldersRepository,
                        remindersRepository = container.remindersRepository
                    )
                )
                SearchScreen(
                    viewModel = searchViewModel,
                    onBack = { navController.popBackStack() },
                    onNoteClick = { noteId -> navController.navigate(Screen.NoteEditor.createRoute(noteId = noteId)) },
                    onFolderClick = { folderId -> navController.navigate(Screen.FolderDetail.createRoute(folderId)) }
                )
            }

            // Vault Screens
            composable(Screen.VaultLocked.route) {
                val vaultViewModel: com.anant.sivonotes.ui.vault.VaultViewModel = viewModel(
                    factory = com.anant.sivonotes.ui.vault.VaultViewModel.provideFactory(
                        vaultRepository = container.vaultRepository,
                        vaultManager = container.vaultManager
                    )
                )
                com.anant.sivonotes.ui.vault.VaultLockedScreen(
                    viewModel = vaultViewModel,
                    onUnlocked = {
                        navController.navigate(Screen.VaultHome.route) {
                            popUpTo(Screen.VaultLocked.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.VaultHome.route) {
                val vaultViewModel: com.anant.sivonotes.ui.vault.VaultViewModel = viewModel(
                    factory = com.anant.sivonotes.ui.vault.VaultViewModel.provideFactory(
                        vaultRepository = container.vaultRepository,
                        vaultManager = container.vaultManager
                    )
                )
                com.anant.sivonotes.ui.vault.VaultHomeScreen(
                    viewModel = vaultViewModel,
                    onBack = { navController.popBackStack() },
                    onAddPassword = { navController.navigate(Screen.VaultPasswordEditor.createRoute(-1L)) },
                    onEditPassword = { entryId -> navController.navigate(Screen.VaultPasswordEditor.createRoute(entryId)) },
                    onAddPrivateNote = { navController.navigate(Screen.VaultNoteEditor.createRoute(-1L)) },
                    onEditPrivateNote = { noteId -> navController.navigate(Screen.VaultNoteEditor.createRoute(noteId)) }
                )
            }

            composable(
                route = Screen.VaultPasswordEditor.route,
                arguments = listOf(
                    navArgument("entryId") {
                        type = NavType.StringType
                        defaultValue = "-1"
                    }
                )
            ) { backStackEntry ->
                val entryId = backStackEntry.arguments?.getString("entryId")?.toLongOrNull()?.takeIf { it > 0 } ?: -1L
                val vaultViewModel: com.anant.sivonotes.ui.vault.VaultViewModel = viewModel(
                    factory = com.anant.sivonotes.ui.vault.VaultViewModel.provideFactory(
                        vaultRepository = container.vaultRepository,
                        vaultManager = container.vaultManager
                    )
                )
                com.anant.sivonotes.ui.vault.editor.VaultPasswordEditorScreen(
                    entryId = entryId,
                    viewModel = vaultViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.VaultNoteEditor.route,
                arguments = listOf(
                    navArgument("noteId") {
                        type = NavType.StringType
                        defaultValue = "-1"
                    }
                )
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getString("noteId")?.toLongOrNull()?.takeIf { it > 0 } ?: -1L
                val vaultViewModel: com.anant.sivonotes.ui.vault.VaultViewModel = viewModel(
                    factory = com.anant.sivonotes.ui.vault.VaultViewModel.provideFactory(
                        vaultRepository = container.vaultRepository,
                        vaultManager = container.vaultManager
                    )
                )
                com.anant.sivonotes.ui.vault.editor.VaultNoteEditorScreen(
                    noteId = noteId,
                    viewModel = vaultViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // Settings Placeholder (Configured in Phase 6)
            composable(Screen.Settings.route) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Configured in Phase 6
                }
            }
        }

        // Floating Bottom Bar on Root Screens
        if (isBottomBarVisible) {
            FloatingBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onFabClick = { showUniversalAddSheet = true },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    // Universal Quick Add Bottom Sheet
    if (showUniversalAddSheet) {
        UniversalAddSheet(
            onDismiss = { showUniversalAddSheet = false },
            onAddNote = {
                navController.navigate(Screen.NoteEditor.createRoute())
            },
            onAddTodo = {
                showQuickTodoDialog = true
            },
            onAddReminder = {
                showQuickReminderDialog = true
            },
            onAddPoint = {
                navController.navigate(Screen.ImportantPoints.createRoute())
            },
            onAddFolder = {
                showQuickFolderDialog = true
            },
            onAddVaultEntry = {
                navController.navigate(Screen.VaultLocked.route)
            }
        )
    }

    // Fast Add Dialogs triggered from Bottom Sheet
    if (showQuickTodoDialog) {
        val todosVm: TodosViewModel = viewModel(
            factory = TodosViewModel.provideFactory(container.todosRepository, container.foldersRepository)
        )
        val uiState = todosVm.uiState.value
        CreateTodoDialog(
            allFolders = uiState.allFolders,
            onDismiss = { showQuickTodoDialog = false },
            onConfirm = { title, desc, dueDate, dueTime, priority, repeat, folderId ->
                todosVm.createTodo(title, desc, dueDate, dueTime, priority, repeat, folderId)
                showQuickTodoDialog = false
            }
        )
    }

    if (showQuickReminderDialog) {
        val remVm: RemindersViewModel = viewModel(
            factory = RemindersViewModel.provideFactory(container.remindersRepository, context)
        )
        CreateReminderDialog(
            onDismiss = { showQuickReminderDialog = false },
            onConfirm = { title, note, time, repeat ->
                remVm.createReminder(title, note, time, repeat)
                showQuickReminderDialog = false
            }
        )
    }

    if (showQuickFolderDialog) {
        val foldVm: FoldersViewModel = viewModel(
            factory = FoldersViewModel.provideFactory(
                container.foldersRepository,
                container.notesRepository,
                container.todosRepository,
                container.importantPointsRepository
            )
        )
        CreateFolderDialog(
            onDismiss = { showQuickFolderDialog = false },
            onConfirm = { name, icon, color ->
                foldVm.createFolder(name, icon, color)
                showQuickFolderDialog = false
            }
        )
    }
}
