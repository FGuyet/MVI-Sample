package com.fguyet.sample.mvi.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Base class for MVI ViewModels.
 *
 * - [State]  : Immutable snapshot of the UI, continuously observed via [uiState].
 * - [Action] : View-triggered actions dispatched through the single [handle] entry point.
 */
abstract class MviViewModel<State, Action>(initialState: State) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)

    /** Current UI state, exposed read-only. */
    val uiState = _uiState.asStateFlow()

    private val mutex = Mutex()

    /**
     * Thread-safe state reducer.
     * Suspend function – must be called from a coroutine (e.g. inside viewModelScope).
     */
    protected suspend fun updateState(reducer: State.() -> State) = mutex.withLock {
        _uiState.value = reducer(uiState.value)
    }

    /** Single entry point for all UI actions. */
    abstract fun handle(action: Action)
}


