package app.gamenative.ui.screen.library.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.gamenative.data.LibraryItem
import app.gamenative.ui.data.LibraryState
import app.gamenative.ui.enums.AppFilter
import app.gamenative.ui.internal.fakeAppInfo
import app.gamenative.service.DownloadService
import app.gamenative.ui.theme.PluviaTheme
import app.gamenative.ui.component.topbar.AccountButton
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import app.gamenative.PrefManager
import app.gamenative.utils.DeviceUtils
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LibraryListPane(
    state: LibraryState,
    listState: LazyListState,
    sheetState: SheetState,
    onFilterChanged: (AppFilter) -> Unit,
    onModalBottomSheet: (Boolean) -> Unit,
    onPageChange: (Int) -> Unit,
    onIsSearching: (Boolean) -> Unit,
    onLogout: () -> Unit,
    onNavigate: (Int) -> Unit,
    onSearchQuery: (String) -> Unit,
    onNavigateRoute: (String) -> Unit,
) {
    val expandedFab by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }
    val snackBarHost = remember { SnackbarHostState() }
    val installedCount = remember { DownloadService.getDownloadDirectoryApps().count() }

    // Responsive width for better layouts
    val isViewWide = DeviceUtils.isViewWide(currentWindowAdaptiveInfo())

    // Infinite scroll: load next page when scrolled to bottom
    LaunchedEffect(listState, state.appInfoList.size) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex >= state.appInfoList.lastIndex
                    && state.appInfoList.size < state.totalAppsInFilter) {
                    onPageChange(1)
                }
            }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHost) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            // Modern Header with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "GameNative",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            )
                        )
                        Text(
                            text = "${state.totalAppsInFilter} games • $installedCount installed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isViewWide) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 30.dp)
                        ) {
                            LibrarySearchBar(
                                state = state,
                                listState = listState,
                                onSearchQuery = onSearchQuery,
                            )
                        }
                    }

                    // User profile button
                    Row {
                        IconButton(onClick = { onModalBottomSheet(true) }) {
                            Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter")
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                .padding(8.dp)
                        ) {
                            AccountButton(
                                onNavigateRoute = onNavigateRoute,
                                onLogout = onLogout
                            )
                        }
                    }
                }
            }

            if (! isViewWide) {
                // Search bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    LibrarySearchBar(
                        state = state,
                        listState = listState,
                        onSearchQuery = onSearchQuery,
                    )
                }
            }

            // Game list
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyRow(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 72.dp
                    ),
                ) {
                    var focusedIndex by remember { mutableStateOf(-1) }
                    items(items = state.appInfoList, key = { it.index }) { item ->
                        val scale = if (focusedIndex == item.index) 1.1f else 1f
                        GameTile(
                            modifier = Modifier.animateItem(),
                            appInfo = item,
                            onClick = { onNavigate(item.appId) },
                            onFocus = { focusedIndex = item.index },
                            scale = scale
                        )
                    }
                    if (state.appInfoList.size < state.totalAppsInFilter) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxHeight()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }

                if (state.modalBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { onModalBottomSheet(false) },
                        sheetState = sheetState,
                        content = {
                            LibraryBottomSheet(
                                selectedFilters = state.appInfoSortType,
                                onFilterChanged = onFilterChanged,
                            )
                        },
                    )
                }
            }
        }
    }
}

/***********
 * PREVIEW *
 ***********/

@OptIn(ExperimentalMaterial3Api::class)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Preview(device = "spec:width=1920px,height=1080px,dpi=440") // Odin2 Mini
@Composable
private fun Preview_LibraryListPane() {
    val context = LocalContext.current
    PrefManager.init(context)
    val sheetState = rememberModalBottomSheetState()
    var state by remember {
        mutableStateOf(
            LibraryState(
                appInfoList = List(15) { idx ->
                    val item = fakeAppInfo(idx)
                    LibraryItem(
                        index = idx,
                        appId = item.id,
                        name = item.name,
                        iconHash = item.iconHash,
                        headerImageUrl = "https://steamcdn-a.akamaihd.net/steam/apps/400/header.jpg",
                        isShared = idx % 2 == 0,
                    )
                },
            ),
        )
    }
    PluviaTheme {
        Surface {
            LibraryListPane(
                listState = LazyListState(2, 64),
                state = state,
                sheetState = sheetState,
                onFilterChanged = { },
                onPageChange = { },
                onModalBottomSheet = {
                    val currentState = state.modalBottomSheet
                    println("State: $currentState")
                    state = state.copy(modalBottomSheet = !currentState)
                },
                onIsSearching = { },
                onSearchQuery = { },
                onNavigateRoute = { },
                onLogout = { },
                onNavigate = { },
            )
        }
    }
}
