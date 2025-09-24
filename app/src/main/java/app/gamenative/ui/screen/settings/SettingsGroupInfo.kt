package app.gamenative.ui.screen.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import app.gamenative.Constants
import app.gamenative.PrefManager
import app.gamenative.ui.component.dialog.LibrariesDialog
import app.gamenative.ui.theme.settingsTileColors
import app.gamenative.ui.theme.settingsTileColorsAlt
import app.gamenative.ui.component.settings.SettingsGroup
import app.gamenative.ui.component.settings.SettingsMenuLink
import app.gamenative.ui.component.settings.SettingsSwitch

@Composable
fun SettingsGroupInfo() {
    SettingsGroup("Info") {
        val uriHandler = LocalUriHandler.current
        var askForTip by rememberSaveable { mutableStateOf(!PrefManager.tipped) }
        var showLibrariesDialog by rememberSaveable { mutableStateOf(false) }

        LibrariesDialog(
            visible = showLibrariesDialog,
            onDismissRequest = { showLibrariesDialog = false },
        )

        SettingsMenuLink(
            title = "Send tip",
            subtitle = "Contribute to ongoing development",
            onClick = {
                uriHandler.openUri(Constants.Misc.KO_FI_LINK)
                askForTip = false
                PrefManager.tipped = !askForTip
            },
        )

        SettingsSwitch(
            checked = askForTip,
            title = "Ask for tip on startup",
            subtitle = "Stops the tip message from appearing",
            onCheckedChange = {
                askForTip = it
                PrefManager.tipped = !askForTip
            },
        )

        SettingsMenuLink(
            title = "Source code",
            subtitle = "View the source code of this project",
            onClick = { uriHandler.openUri(Constants.Misc.GITHUB_LINK) },
        )

        SettingsMenuLink(
            title = "Libraries Used",
            subtitle = "See what technologies make GameNative possible",
            onClick = { showLibrariesDialog = true },
        )

        SettingsMenuLink(
            title = "Privacy Policy",
            subtitle = "Opens a link to GameNative's privacy policy",
            onClick = {
                uriHandler.openUri(Constants.Misc.PRIVACY_LINK)
            },
        )
    }
}
