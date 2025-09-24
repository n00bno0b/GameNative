package app.gamenative.ui.screen.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import app.gamenative.ui.component.dialog.Box64PresetsDialog
import app.gamenative.ui.component.dialog.ContainerConfigDialog
import app.gamenative.ui.component.dialog.OrientationDialog
import app.gamenative.ui.theme.settingsTileColors
import app.gamenative.utils.ContainerUtils
import app.gamenative.ui.component.settings.SettingsGroup
import app.gamenative.ui.component.settings.SettingsMenuLink

@Composable
fun SettingsGroupEmulation() {
    SettingsGroup("Emulation") {
        var showConfigDialog by rememberSaveable { mutableStateOf(false) }
        var showOrientationDialog by rememberSaveable { mutableStateOf(false) }
        var showBox64PresetsDialog by rememberSaveable { mutableStateOf(false) }

        OrientationDialog(
            openDialog = showOrientationDialog,
            onDismiss = { showOrientationDialog = false },
        )

        ContainerConfigDialog(
            visible = showConfigDialog,
            title = "Default Container Config",
            default = true,
            initialConfig = ContainerUtils.getDefaultContainerData(),
            onDismissRequest = { showConfigDialog = false },
            onSave = {
                showConfigDialog = false
                ContainerUtils.setDefaultContainerData(it)
            },
        )

        Box64PresetsDialog(
            visible = showBox64PresetsDialog,
            onDismissRequest = { showBox64PresetsDialog = false },
        )

        SettingsMenuLink(
            title = "Allowed Orientations",
            subtitle = "Choose which orientations can be rotated to when in-game",
            onClick = { showOrientationDialog = true },
        )
        SettingsMenuLink(
            title = "Modify Default Config",
            subtitle = "The initial container settings for each game (does not affect already installed games)",
            onClick = { showConfigDialog = true },
        )
        SettingsMenuLink(
            title = "Box64 Presets",
            subtitle = "View, modify, and create Box64 presets",
            onClick = { showBox64PresetsDialog = true },
        )
    }
}
