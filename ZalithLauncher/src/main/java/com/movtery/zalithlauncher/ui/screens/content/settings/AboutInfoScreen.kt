/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.screens.content.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Copyright
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.movtery.zalithlauncher.BuildConfig
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.plugin.ApkPlugin
import com.movtery.zalithlauncher.game.plugin.PluginLoader
import com.movtery.zalithlauncher.game.plugin.appCacheIcon
import com.movtery.zalithlauncher.info.InfoDistributor
import com.movtery.zalithlauncher.library.LibraryInfo
import com.movtery.zalithlauncher.library.libraryData
import com.movtery.zalithlauncher.path.URL_COMMUNITY
import com.movtery.zalithlauncher.path.URL_MCMOD
import com.movtery.zalithlauncher.path.URL_PROJECT
import com.movtery.zalithlauncher.path.URL_SUPPORT
import com.movtery.zalithlauncher.path.URL_WEBLATE
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.AnimatedLazyColumn
import com.movtery.zalithlauncher.ui.components.CardTitleLayout
import com.movtery.zalithlauncher.ui.components.itemLayoutColor
import com.movtery.zalithlauncher.ui.components.itemLayoutShadowElevation
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.CardPosition
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.SettingsCard

import com.movtery.zalithlauncher.path.URL_BASE_PROJECT
import com.movtery.zalithlauncher.path.URL_DEV_ALT_DISCORD
import com.movtery.zalithlauncher.path.URL_DEV_DISCORD
import com.movtery.zalithlauncher.path.URL_DEV_GITHUB
import com.movtery.zalithlauncher.path.URL_DEV_INSTAGRAM
import com.movtery.zalithlauncher.path.URL_MOVTERY_SUPPORT
import com.movtery.zalithlauncher.path.URL_PROJECT_RELEASES

@Composable
fun AboutInfoScreen(
    key: NestedNavKey.Settings,
    settingsScreenKey: TitledNavKey?,
    mainScreenKey: TitledNavKey?,
    checkUpdate: () -> Unit,
    openLicense: (raw: Int) -> Unit,
    openLink: (url: String) -> Unit
) {
    BaseScreen(
        Triple(key, mainScreenKey, false),
        Triple(NormalNavKey.Settings.AboutInfo, settingsScreenKey, false)
    ) { isVisible ->
        AnimatedLazyColumn(
            modifier = Modifier.fillMaxSize(),
            isVisible = isVisible,
            contentPadding = PaddingValues(all = 12.dp)
        ) { scope ->
            // CARD: FORK BUILD (PRIMARY)
            animatedItem(scope) { yOffset ->
                ChunkLayout(
                    modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                    title = stringResource(R.string.about_fork_primary_title)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ButtonIconItem(
                            icon = painterResource(R.drawable.img_launcher),
                            title = stringResource(R.string.about_fork_name),
                            text = stringResource(R.string.about_fork_maintained_by) + ": " + stringResource(R.string.about_developer_handle_github_value),
                            button = {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(onClick = { openLink(URL_PROJECT) }) {
                                            Text(text = stringResource(R.string.about_launcher_project_link))
                                        }
                                        OutlinedButton(onClick = { openLink(URL_PROJECT_RELEASES) }) {
                                            Text(text = stringResource(R.string.about_fork_releases_link))
                                        }
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(onClick = { openLink(URL_DEV_DISCORD) }) {
                                            Text(text = stringResource(R.string.about_fork_join_discord))
                                        }
                                        OutlinedButton(onClick = { openLink(URL_COMMUNITY) }) {
                                            Text(text = stringResource(R.string.about_acknowledgements_github_community))
                                        }
                                    }
                                }
                            }
                        )
                        Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                            Text(
                                text = stringResource(R.string.about_fork_status),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.about_fork_status_text),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // CARD: DEVELOPER (UNTAMED FURY)
            animatedItem(scope) { yOffset ->
                ChunkLayout(
                    modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                    title = stringResource(R.string.about_developer_title)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ButtonIconItem(
                            icon = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("https://avatars.githubusercontent.com/u/168332215?v=4")
                                    .crossfade(true)
                                    .build()
                            ),
                            title = stringResource(R.string.about_developer_presence),
                            text = "Lead maintainer of the Cracked Fork",
                            button = {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(onClick = { openLink(URL_DEV_GITHUB) }) {
                                            Text(text = stringResource(R.string.about_developer_handle_github))
                                        }
                                        OutlinedButton(onClick = { openLink(URL_DEV_DISCORD) }) {
                                            Text(text = stringResource(R.string.about_developer_handle_discord))
                                        }
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(onClick = { openLink(URL_DEV_INSTAGRAM) }) {
                                            Text(text = stringResource(R.string.about_developer_handle_instagram))
                                        }
                                        OutlinedButton(onClick = { openLink(URL_DEV_ALT_DISCORD) }) {
                                            Text(text = stringResource(R.string.about_developer_alt_discord))
                                        }
                                    }
                                }
                            }
                        )
                        Column(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.about_developer_handles),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            HandleRow(stringResource(R.string.about_developer_handle_github), stringResource(R.string.about_developer_handle_github_value))
                            HandleRow(stringResource(R.string.about_developer_handle_discord), stringResource(R.string.about_developer_handle_discord_value))
                            HandleRow(stringResource(R.string.about_developer_handle_alt_tag), stringResource(R.string.about_developer_handle_alt_tag_value))
                            HandleRow(stringResource(R.string.about_developer_handle_instagram), stringResource(R.string.about_developer_handle_instagram_value))
                        }
                    }
                }
            }

            // CARD: PROJECT ECOSYSTEM
            animatedItem(scope) { yOffset ->
                ChunkLayout(
                    modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                    title = stringResource(R.string.about_ecosystem_title)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ButtonIconItem(
                            icon = painterResource(R.drawable.ic_chat_info),
                            title = stringResource(R.string.about_ecosystem_community),
                            text = stringResource(R.string.about_fork_discord_text),
                            button = {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(onClick = { openLink(URL_COMMUNITY) }) {
                                        Text(text = stringResource(R.string.about_acknowledgements_github_community))
                                    }
                                    OutlinedButton(onClick = { openLink(URL_WEBLATE) }) {
                                        Text(text = "Weblate")
                                    }
                                }
                            }
                        )
                        ButtonIconItem(
                            icon = painterResource(R.drawable.ic_chat_info),
                            title = stringResource(R.string.about_ecosystem_support),
                            text = "Support the development of this fork",
                            button = {
                                OutlinedButton(onClick = checkUpdate) {
                                    Text(text = stringResource(R.string.upgrade_title))
                                }
                            }
                        )
                    }
                }
            }

            // CARD: ORIGINAL PROJECT
            animatedItem(scope) { yOffset ->
                ChunkLayout(
                    modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                    title = stringResource(R.string.about_base_title)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ButtonIconItem(
                            icon = painterResource(R.drawable.img_movtery),
                            title = stringResource(R.string.about_base_name),
                            text = stringResource(R.string.about_base_created_by) + ": MovTery",
                            button = {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(onClick = { openLink(URL_BASE_PROJECT) }) {
                                            Text(text = stringResource(R.string.about_base_github))
                                        }
                                        OutlinedButton(onClick = { openLicense(R.raw.gpl_3_license) }) {
                                            Text(text = "License")
                                        }
                                    }
                                    OutlinedButton(onClick = { openLink(URL_MOVTERY_SUPPORT) }) {
                                        Text(text = stringResource(R.string.about_base_support))
                                    }
                                }
                            }
                        )
                    }
                }
            }

            // CARD: ACKNOWLEDGEMENTS
            animatedItem(scope) { yOffset ->
                ChunkLayout(
                    modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                    title = stringResource(R.string.about_acknowledgements_title)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        AcknowledgementSection(stringResource(R.string.about_acknowledgements_launchers)) {
                            LinkIconItem(
                                icon = painterResource(R.drawable.img_launcher_fcl),
                                title = "Fold Craft Launcher",
                                text = "FCL Team",
                                openLicense = { openLicense(R.raw.fcl_license) },
                                openLink = { openLink("https://github.com/FCL-Team/FoldCraftLauncher") }
                            )
                            LinkIconItem(
                                icon = painterResource(R.drawable.img_launcher_hmcl),
                                title = "Hello Minecraft! Launcher",
                                text = "HMCL-dev",
                                openLicense = { openLicense(R.raw.hmcl_license) },
                                openLink = { openLink("https://github.com/HMCL-dev/HMCL") }
                            )
                            LinkIconItem(
                                icon = painterResource(R.drawable.img_launcher_pcl2),
                                title = "Plain Craft Launcher 2",
                                text = "LTCat",
                                openLink = { openLink("https://github.com/Meloong-Git/PCL") }
                            )
                            LinkIconItem(
                                icon = painterResource(R.drawable.img_launcher_pojav),
                                title = "PojavLauncher",
                                text = "PojavLauncherTeam",
                                openLicense = { openLicense(R.raw.lgpl_3_license) },
                                openLink = { openLink("https://github.com/PojavLauncherTeam/PojavLauncher") }
                            )
                        }

                        AcknowledgementSection(stringResource(R.string.about_acknowledgements_platforms)) {
                            LinkIconItem(
                                icon = painterResource(R.drawable.img_platform_mcmod),
                                title = "MCMod",
                                text = "mcmod.cn",
                                openLink = { openLink(URL_MCMOD) }
                            )
                            LinkIconItem(
                                icon = painterResource(R.drawable.img_mcim),
                                title = "mcmod-info-mirror",
                                text = "mcimirror.top",
                                openLink = { openLink("https://www.mcimirror.top/") }
                            )
                        }

                        AcknowledgementSection(stringResource(R.string.about_acknowledgements_community_title)) {
                            LinkIconItem(
                                icon = painterResource(R.drawable.ic_github),
                                title = "GitHub Contributors",
                                text = "All code and issue contributors",
                                openLink = { openLink(URL_COMMUNITY) },
                                useImage = false
                            )
                            LinkIconItem(
                                icon = painterResource(R.drawable.img_weblate),
                                title = "Weblate",
                                text = "All community translators",
                                openLink = { openLink(URL_WEBLATE) }
                            )
                        }
                    }
                }
            }

            // CARD: LIBRARIES
            animatedItem(scope) { yOffset ->
                ChunkLayout(
                    modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                    title = stringResource(R.string.about_library_title)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        libraryData.forEach { info ->
                            LibraryInfoItem(info = info, openLicense = openLicense, openLink = openLink)
                        }
                    }
                }
            }

            // CARD: PLUGINS
            PluginLoader.allPlugins.takeIf { it.isNotEmpty() }?.let { allPlugins ->
                animatedItem(scope) { yOffset ->
                    ChunkLayout(
                        modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                        title = stringResource(R.string.about_plugin_title)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            allPlugins.forEach { apkPlugin ->
                                PluginInfoItem(apkPlugin = apkPlugin)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HandleRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label + "    :", style = MaterialTheme.typography.bodySmall, modifier = Modifier.alpha(0.7f))
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AcknowledgementSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )
        content()
    }
}

@Composable
private fun ChunkLayout(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit
) {
    SettingsCard(
        modifier = modifier,
        position = CardPosition.Single
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CardTitleLayout {
                Text(
                    modifier = Modifier.padding(all = 16.dp),
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 12.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun LinkIconItem(
    modifier: Modifier = Modifier,
    icon: Painter,
    title: String,
    text: String,
    openLicense: (() -> Unit)? = null,
    openLink: (() -> Unit)? = null,
    color: Color = itemLayoutColor(),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    shadowElevation: Dp = itemLayoutShadowElevation(),
    useImage: Boolean = true
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.large,
        shadowElevation = shadowElevation,
        onClick = {}
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconModifier = Modifier
                .size(34.dp)
                .clip(shape = RoundedCornerShape(6.dp))
            if (useImage) {
                Image(
                    modifier = iconModifier,
                    painter = icon,
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
            } else {
                Icon(
                    modifier = iconModifier,
                    painter = icon,
                    contentDescription = null
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    modifier = Modifier.alpha(0.7f),
                    text = text,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row {
                openLicense?.let {
                    IconButton(
                        onClick = it
                    ) {
                        Icon(
                            modifier = Modifier.size(22.dp),
                            imageVector = Icons.Outlined.Copyright,
                            contentDescription = "License"
                        )
                    }
                }
                openLink?.let {
                    IconButton(
                        onClick = it
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Link,
                            contentDescription = stringResource(R.string.generic_open_link)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ButtonIconItem(
    modifier: Modifier = Modifier,
    icon: Painter,
    title: String,
    text: String,
    button: @Composable RowScope.() -> Unit,
    color: Color = itemLayoutColor(),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    shadowElevation: Dp = itemLayoutShadowElevation()
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.large,
        shadowElevation = shadowElevation,
        onClick = {}
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .size(34.dp)
                    .clip(shape = RoundedCornerShape(6.dp)),
                painter = icon,
                contentDescription = null,
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    modifier = Modifier.alpha(0.7f),
                    text = text,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            button()
        }
    }
}

@Composable
private fun PluginInfoItem(
    apkPlugin: ApkPlugin,
    modifier: Modifier = Modifier,
    color: Color = itemLayoutColor(),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    shadowElevation: Dp = itemLayoutShadowElevation()
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.large,
        shadowElevation = shadowElevation,
        onClick = {}
    ) {
        val context = LocalContext.current
        Row(
            modifier = Modifier
                .padding(all = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val iconFile = appCacheIcon(apkPlugin.packageName)
            if (iconFile.exists()) {
                val model = remember(context, iconFile) {
                    ImageRequest.Builder(context)
                        .data(iconFile)
                        .build()
                }
                AsyncImage(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(shape = RoundedCornerShape(8.dp)),
                    model = model,
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
            } else {
                Image(
                    modifier = Modifier.size(34.dp),
                    painter = painterResource(R.drawable.ic_unknown_icon),
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(
                    text = apkPlugin.appName,
                    style = MaterialTheme.typography.titleSmall
                )
                Row(
                    modifier = Modifier.alpha(0.7f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = apkPlugin.packageName,
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (apkPlugin.appVersion.isNotEmpty()) {
                        Text(
                            text = apkPlugin.appVersion,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryInfoItem(
    info: LibraryInfo,
    modifier: Modifier = Modifier,
    color: Color = itemLayoutColor(),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    shadowElevation: Dp = itemLayoutShadowElevation(),
    openLicense: (Int) -> Unit,
    openLink: (url: String) -> Unit
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.large,
        shadowElevation = shadowElevation,
        onClick = {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = info.name,
                    style = MaterialTheme.typography.titleSmall
                )
                Column(
                    modifier = Modifier.alpha(0.7f)
                ) {
                    info.copyrightInfo?.let { copyrightInfo ->
                        Text(
                            text = copyrightInfo,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(
                        modifier = Modifier.clickable(
                            onClick = {
                                openLicense(info.license.raw)
                            }
                        ),
                        text = "Licensed under the ${info.license.name}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
            }
            IconButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {
                    openLink(info.webUrl)
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Link,
                    contentDescription = null
                )
            }
        }
    }
}