package de.tysw.mixes.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

import de.tysw.mixes.model.Mix
import de.tysw.mixes.util.MixParser
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var _originalMixes: List<Mix> = emptyList()

    private val _filteredMixes = MutableStateFlow<List<Mix>>(emptyList())
    val filteredMixes: StateFlow<List<Mix>> = _filteredMixes

    private val _expandedMixes = MutableStateFlow<Set<UUID>>(emptySet())
    val expandedMixes = _expandedMixes.asStateFlow()


    private val appContext = application.applicationContext

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val expandOnSearch: Boolean = true

    fun loadMixes() {
        viewModelScope.launch {
            val file = File(appContext.filesDir, "mixes.txt")
            if (file.exists()) {
                val text = file.readText()
                _originalMixes = MixParser.parse(text).sortedByDescending { it.created }
            }
            else
            {
                _originalMixes = emptyList()
            }

            _filteredMixes.value = _originalMixes
        }
    }

    fun toggleMixExpanded(mix: Mix) {
        _expandedMixes.update { current ->
            if (mix.id in current)
                current - mix.id
            else
                current + mix.id
        }
    }

    fun expandNone() {
        _expandedMixes.value = emptySet()
    }

    fun expandAll() {
        _expandedMixes.value = _filteredMixes.value.map { it.id }.toSet()
    }

    fun filterMixes(query: String) {
        _searchQuery.value = query
        val q = query.trim().lowercase()

        if (q.isEmpty())
        {
            _filteredMixes.value = _originalMixes
            return
        }

        _filteredMixes.value = _originalMixes.filter { it.matchesQuery(q) }

        if (expandOnSearch && query.isNotBlank()) {
            _expandedMixes.value = _filteredMixes.value.map { it.id }.toSet()
        } else if (query.isBlank()) {
            _expandedMixes.value = emptySet()
        }
    }

    fun resetFilter() {
        _searchQuery.value = ""
        _filteredMixes.value = _originalMixes
    }

    private fun Mix.matchesQuery(q: String): Boolean {
        return listOfNotNull(
            title
        ).any { it.contains(q, ignoreCase = true) }
                ||
                tracks.any { it.title.contains(q, ignoreCase = true)
                        || it.artist.contains(q, ignoreCase = true)}
    }
}
