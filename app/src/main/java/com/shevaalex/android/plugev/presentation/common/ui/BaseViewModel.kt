package com.shevaalex.android.plugev.presentation.common.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<S>(
    initialState: S
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)

    val state: StateFlow<S>
        get() = _state.asStateFlow()

    protected fun setState(newState: S) {
        _state.value = newState
    }

}
