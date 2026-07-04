package com.mooncowpines.kinostats.ui.screens.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mooncowpines.kinostats.domain.model.MovieList
import com.mooncowpines.kinostats.domain.repository.AuthRepository
import com.mooncowpines.kinostats.domain.repository.AuthState
import com.mooncowpines.kinostats.domain.repository.ListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListsScreenViewModel @Inject constructor(
    private val listRepository: ListRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ListsScreenState())
    val state: StateFlow<ListsScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.authState.collect { authState ->
                if (authState == AuthState.LOGGED_OUT) {
                    _state.value = ListsScreenState()
                }
            }
        }

        loadLists()
    }

    fun loadLists() {
        viewModelScope.launch {
            val showLoadingSpinner = _state.value.lists.isEmpty()

            _state.update { it.copy(isLoading = showLoadingSpinner, errorMsg = null) }

            val user = authRepository.getCurrentUser()
            val userId = user?.id

            if (userId == null) {
                _state.update { it.copy(isLoading = false, errorMsg = "User not found. Please log in again.") }
                return@launch
            }

            val userLists = listRepository.getListsByUser(userId)

            if (userLists != null) {
                _state.update { it.copy(isLoading = false, lists = userLists) }
            } else {
                _state.update { it.copy(isLoading = false, errorMsg = "Failed to load lists.") }
            }
        }
    }

    fun onConfirmDeleteIntent(movieList: MovieList) {
        _state.update { it.copy(listToDelete = movieList) }
    }

    fun onDismissDeleteDialog() {
        _state.update { it.copy(listToDelete = null) }
    }

    fun setShowCreateDialog(show: Boolean) {
        _state.update { it.copy(showCreateDialog = show, newListName = "", errorMsg = null) }
    }

    fun onNewListNameChange(name: String) {
        _state.update { it.copy(newListName = name) }
    }

    fun createList() {
        val listName = _state.value.newListName
        if (listName.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isCreating = true, errorMsg = null) }

            val user = authRepository.getCurrentUser()
            val userId = user?.id

            if (userId != null) {
                val success = listRepository.createList(userId, listName)

                if (success) {
                    _state.update {
                        it.copy(isCreating = false, showCreateDialog = false, newListName = "")
                    }
                    loadLists()
                } else {
                    _state.update { it.copy(isCreating = false, errorMsg = "Failed to create list") }
                }
            } else {
                _state.update { it.copy(isCreating = false, errorMsg = "User not found") }
            }
        }
    }

    fun onRenameIntent(movieList: MovieList) {
        _state.update {
            it.copy(
                listToRename = movieList,
                renameText = movieList.name,
                errorMsg = null
            )
        }
    }

    fun onRenameTextChange(text: String) {
        _state.update { it.copy(renameText = text) }
    }

    fun onDismissRenameDialog() {
        _state.update { it.copy(listToRename = null, renameText = "") }
    }

    fun confirmRename() {
        val list = _state.value.listToRename ?: return
        val newName = _state.value.renameText

        if (newName.isBlank() || newName == list.name) return

        viewModelScope.launch {
            _state.update { it.copy(isRenaming = true, errorMsg = null) }

            val userId = authRepository.getCurrentUser()?.id

            if (userId != null) {
                val success = listRepository.updateList(list.id, userId, newName)

                if (success) {
                    _state.update {
                        it.copy(isRenaming = false, listToRename = null, renameText = "")
                    }
                    loadLists()
                } else {
                    _state.update { it.copy(isRenaming = false, errorMsg = "Failed to rename list") }
                }
            } else {
                _state.update { it.copy(isRenaming = false, errorMsg = "User not found") }
            }
        }
    }

    fun deleteList() {
        val listId = _state.value.listToDelete?.id ?: return

        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            val success = listRepository.deleteList(listId)

            if (success) {
                _state.update { it.copy(listToDelete = null, isDeleting = false) }
                loadLists()
            } else {
                _state.update {
                    it.copy(
                        isDeleting = false,
                        errorMsg = "Could not delete list",
                        listToDelete = null
                    )
                }
            }
        }
    }
}