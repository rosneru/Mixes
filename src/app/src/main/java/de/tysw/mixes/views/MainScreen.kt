package de.tysw.mixes.views


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.tysw.mixes.R
import de.tysw.mixes.model.Mix
import de.tysw.mixes.util.highlightMatches
import de.tysw.mixes.viewmodels.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel, onSettingsClick: () -> Unit) {
    val mixes by viewModel.filteredMixes.collectAsState()
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadMixes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearching) {
                        SearchBar(
                            inputField = {
                                SearchBarDefaults.InputField(
                                    query = searchQuery,
                                    onQueryChange = {
                                        searchQuery = it
                                        viewModel.filterMixes(searchQuery)
                                    },
                                    expanded = isSearching,
                                    onExpandedChange = {},
                                    onSearch = { isSearching = false },
                                    placeholder = { Text("Search..") }
                                )
                            },
                            expanded = false,       // Never display a suggestion list
                            onExpandedChange = {}   // So no change behavior needed
                        ) {
                            // No body needed
                            // This would be the place to define the suggestions
                            // and/or search results
                        }
                    } else {
                        Text(stringResource(R.string.app_name))
                    }
                },
                actions = {
                    if (isSearching) {
                        IconButton(onClick = {
                            isSearching = false
                            searchQuery = ""
                            viewModel.resetFilter()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close search")
                        }
                    } else {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = {
                            viewModel.expandAll()
                        }) {
                            Icon(Icons.Default.ExpandMore, contentDescription = "Expand all")
                        }
                        IconButton(onClick = {
                            viewModel.expandNone()
                        }) {
                            Icon(Icons.Default.ExpandLess, contentDescription = "Collapse all")
                        }
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                })
        }) { padding ->
        Box(Modifier.padding(padding)) {
            if (mixes.isEmpty()) {
                EmptyState()
            } else {
                MixesList(
                    mixes = mixes,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun MixesList(
    mixes: List<Mix>,
    viewModel: MainViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(mixes, key = {it.id}) { mix ->
            MixListItem(
                mix = mix,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun MixListItem(
    mix: Mix,
    viewModel: MainViewModel
) {
    val query by viewModel.searchQuery.collectAsState()
    val expandedMixes by viewModel.expandedMixes.collectAsState()

    val isExpanded = expandedMixes.contains(mix.id)

    val backgroundColor = MaterialTheme.colorScheme.primary
    val foregroundColor = MaterialTheme.colorScheme.onPrimary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.toggleMixExpanded(mix)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Card header
            Text(
                text = highlightMatches(
                    text = "${mix.title} — ${mix.created}",
                    query = query,
                    normalColor = foregroundColor,
                    highlightColor = Color.Yellow.copy(alpha = 0.4f)
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = foregroundColor
            )

            // A small distance
            if (isExpanded) Spacer(Modifier.height(12.dp))

            // In expanded mode, also display the list of tracks
            AnimatedVisibility(visible = isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    mix.tracks.forEach { track ->
                        Text(
                            text = highlightMatches(
                                text = "${track.number}. ${track.artist} – ${track.title}",
                                query = query,
                                normalColor = foregroundColor,
                                highlightColor = Color.Yellow.copy(alpha = 0.4f)
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = foregroundColor
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(
            "No mixes available.\n" +
                   "Please configure the URL in the settings screen."
        )
    }
}
