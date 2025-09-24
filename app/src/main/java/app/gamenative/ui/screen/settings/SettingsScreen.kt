package app.gamenative.ui.screen.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import app.gamenative.PrefManager
import app.gamenative.enums.AppTheme
import app.gamenative.ui.component.topbar.BackButton
import app.gamenative.ui.theme.PluviaTheme
import com.materialkolor.PaletteStyle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable

// See link for implementation
// https://github.com/alorma/Compose-Settings

@Composable
fun SettingsScreen(
    appTheme: AppTheme,
    paletteStyle: PaletteStyle,
    onAppTheme: (AppTheme) -> Unit,
    onPaletteStyle: (PaletteStyle) -> Unit,
    onBack: () -> Unit,
) {
    SettingsScreenContent(
        appTheme = appTheme,
        paletteStyle = paletteStyle,
        onAppTheme = onAppTheme,
        onPaletteStyle = onPaletteStyle,
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun SettingsScreenContent(
    appTheme: AppTheme,
    paletteStyle: PaletteStyle,
    onAppTheme: (AppTheme) -> Unit,
    onPaletteStyle: (PaletteStyle) -> Unit,
    onBack: () -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val navigator = rememberListDetailPaneScaffoldNavigator()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    var selectedCategory by rememberSaveable { mutableStateOf(SettingsCategory.Emulation) }
    val categories = SettingsCategory.values()

    if (isExpanded) {
        ListDetailPaneScaffold(
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,
            listPane = {
                AnimatedPane(Modifier) {
                    SettingsCategoryList(
                        categories = categories.map { it.name },
                        onCategorySelected = {
                            selectedCategory = SettingsCategory.valueOf(it)
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                        }
                    )
                }
            },
            detailPane = {
                AnimatedPane(Modifier) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        when (selectedCategory) {
                            SettingsCategory.Emulation -> SettingsGroupEmulation()
                            SettingsCategory.Interface -> SettingsGroupInterface(
                                appTheme = appTheme,
                                paletteStyle = paletteStyle,
                                onAppTheme = onAppTheme,
                                onPaletteStyle = onPaletteStyle,
                            )
                            SettingsCategory.Info -> SettingsGroupInfo()
                            SettingsCategory.Debug -> SettingsGroupDebug()
                        }
                    }
                }
            }
        )
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(text = "Settings") },
                    navigationIcon = {
                        BackButton(onClick = onBack)
                    },
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .displayCutoutPadding()
                    .fillMaxSize()
                    .verticalScroll(scrollState),
            ) {
                SettingsGroupEmulation()
                SettingsGroupInterface(
                    appTheme = appTheme,
                    paletteStyle = paletteStyle,
                    onAppTheme = onAppTheme,
                    onPaletteStyle = onPaletteStyle,
                )
                SettingsGroupInfo()
                SettingsGroupDebug()
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview_SettingsScreen() {
    val context = LocalContext.current
    PrefManager.init(context)
    PluviaTheme {
        SettingsScreenContent(
            appTheme = AppTheme.DAY,
            paletteStyle = PaletteStyle.TonalSpot,
            onAppTheme = { },
            onPaletteStyle = { },
            onBack = { },
        )
    }
}
