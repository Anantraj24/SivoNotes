package com.anant.sivonotes.navigation

sealed class Screen(val route: String) {
    // Bottom Bar Tabs
    object Home : Screen("home")
    object Notes : Screen("notes")
    object Todos : Screen("todos")
    object Folders : Screen("folders")

    // Feature Detail & Editors
    object NoteEditor : Screen("note_editor?noteId={noteId}&folderId={folderId}") {
        fun createRoute(noteId: Long? = null, folderId: Long? = null): String {
            val nId = noteId?.toString() ?: "-1"
            val fId = folderId?.toString() ?: "-1"
            return "note_editor?noteId=$nId&folderId=$fId"
        }
    }

    object FolderDetail : Screen("folder_detail/{folderId}") {
        fun createRoute(folderId: Long): String = "folder_detail/$folderId"
    }

    object ImportantPoints : Screen("important_points?folderId={folderId}") {
        fun createRoute(folderId: Long? = null): String {
            val fId = folderId?.toString() ?: "-1"
            return "important_points?folderId=$fId"
        }
    }

    object StreakProgress : Screen("streak_progress")
    object Reminders : Screen("reminders")
    object GlobalSearch : Screen("global_search")

    // Vault Screens
    object VaultLocked : Screen("vault_locked")
    object VaultSetup : Screen("vault_setup")
    object VaultHome : Screen("vault_home")
    object VaultPasswordEditor : Screen("vault_password_editor?entryId={entryId}") {
        fun createRoute(entryId: Long? = null): String =
            "vault_password_editor?entryId=${entryId ?: -1}"
    }
    object VaultNoteEditor : Screen("vault_note_editor?noteId={noteId}") {
        fun createRoute(noteId: Long? = null): String =
            "vault_note_editor?noteId=${noteId ?: -1}"
    }

    // Settings & Onboarding
    object Settings : Screen("settings")
    object Onboarding : Screen("onboarding")
}
