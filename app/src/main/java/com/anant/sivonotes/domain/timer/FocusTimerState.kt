package com.anant.sivonotes.domain.timer

sealed class FocusTimerState {
    object Idle : FocusTimerState()
    data class Running(val remainingMillis: Long, val totalMillis: Long) : FocusTimerState()
    data class Paused(val remainingMillis: Long, val totalMillis: Long) : FocusTimerState()
    data class Completed(val totalMillis: Long, val actualMillis: Long) : FocusTimerState()
    object Abandoned : FocusTimerState()
}
