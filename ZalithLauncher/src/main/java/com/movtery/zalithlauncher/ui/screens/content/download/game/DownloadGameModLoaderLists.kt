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

package com.movtery.zalithlauncher.ui.screens.content.download.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.addons.modloader.ModLoader
import com.movtery.zalithlauncher.game.addons.modloader.ResponseTooShortException
import com.movtery.zalithlauncher.game.addons.modloader.cleanroom.CleanroomVersion
import com.movtery.zalithlauncher.game.addons.modloader.fabriclike.fabric.FabricVersion
import com.movtery.zalithlauncher.game.addons.modloader.fabriclike.quilt.QuiltVersion
import com.movtery.zalithlauncher.game.addons.modloader.forgelike.forge.ForgeVersion
import com.movtery.zalithlauncher.game.addons.modloader.forgelike.neoforge.NeoForgeVersion
import com.movtery.zalithlauncher.game.addons.modloader.modlike.ModVersion
import com.movtery.zalithlauncher.game.addons.modloader.optifine.OptiFineVersion
import com.movtery.zalithlauncher.game.version.installed.utils.isBiggerVer
import com.movtery.zalithlauncher.utils.logging.Logger.lError
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.SerializationException
import java.net.ConnectException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException

class AddonList {
    //版本列表
    var optifineList by mutableStateOf<List<OptiFineVersion>?>(null)
    var forgeList by mutableStateOf<List<ForgeVersion>?>(null)
    var neoforgeList by mutableStateOf<List<NeoForgeVersion>?>(null)
    var fabricList by mutableStateOf<List<FabricVersion>?>(null)
    var fabricAPIList by mutableStateOf<List<ModVersion>?>(null)
    var quiltList by mutableStateOf<List<QuiltVersion>?>(null)
    var quiltAPIList by mutableStateOf<List<ModVersion>?>(null)
    var cleanroomList by mutableStateOf<List<CleanroomVersion>?>(null)
}

class CurrentAddon {
    //当前选择版本
    var optifineVersion by mutableStateOf<OptiFineVersion?>(null)
    var forgeVersion by mutableStateOf<ForgeVersion?>(null)
    var neoforgeVersion by mutableStateOf<NeoForgeVersion?>(null)
    var fabricVersion by mutableStateOf<FabricVersion?>(null)
    var fabricAPIVersion by mutableStateOf<ModVersion?>(null)
    var quiltVersion by mutableStateOf<QuiltVersion?>(null)
    var quiltAPIVersion by mutableStateOf<ModVersion?>(null)
    var cleanroomVersion by mutableStateOf<CleanroomVersion?>(null)

    //加载状态
    var optifineState by mutableStateOf<AddonState>(AddonState.None)
    var forgeState by mutableStateOf<AddonState>(AddonState.None)
    var neoforgeState by mutableStateOf<AddonState>(AddonState.None)
    var fabricState by mutableStateOf<AddonState>(AddonState.None)
    var fabricAPIState by mutableStateOf<AddonState>(AddonState.None)
    var quiltState by mutableStateOf<AddonState>(AddonState.None)
    var quiltAPIState by mutableStateOf<AddonState>(AddonState.None)
    var cleanroomState by mutableStateOf<AddonState>(AddonState.None)

    //不兼容列表 利用Set集合不可重复
    var incompatibleWithOptiFine by mutableStateOf<Set<ModLoader>>(emptySet())
    var incompatibleWithForge by mutableStateOf<Set<ModLoader>>(emptySet())
    var incompatibleWithNeoForge by mutableStateOf<Set<ModLoader>>(emptySet())
    var incompatibleWithFabric by mutableStateOf<Set<ModLoader>>(emptySet())
    var incompatibleWithFabricAPI by mutableStateOf<Set<ModLoader>>(emptySet())
    var incompatibleWithQuilt by mutableStateOf<Set<ModLoader>>(emptySet())
    var incompatibleWithQuiltAPI by mutableStateOf<Set<ModLoader>>(emptySet())
    var incompatibleWithCleanroom by mutableStateOf<Set<ModLoader>>(emptySet())
}

/**
 * 加载器对于Minecraft版本的支持情况信息
 */
data class LoaderVerSupports(
    val isNeoForgeSupports: Boolean,
    val isFabricSupports: Boolean,
    val isQuiltSupports: Boolean,
    val isCleanroomSupports: Boolean,
)

@Composable
fun rememberLoaderVerSupports(mcVer: String) = remember(mcVer) {
    val fabricLike = mcVer.isBiggerVer("1.13.2", "19w14b")
    LoaderVerSupports(
        isNeoForgeSupports = mcVer.isBiggerVer("1.20", "23w18a"),
        isFabricSupports = fabricLike,
        isQuiltSupports = fabricLike,
        isCleanroomSupports = mcVer == "1.12.2"
    )
}

/**
 * 在 ViewModel 中运行任务并更新附加内容的状态
 */
suspend fun <T> ViewModel.runWithState(
    updateState: (AddonState) -> Unit,
    block: suspend () -> T?
): T? {
    updateState(AddonState.Loading)
    return runCatching {
        block().also {
            updateState(AddonState.None)
        }
    }.onFailure { e ->
        val state = when (e) {
            is ResponseTooShortException -> {
                //忽略，判定为不可用
                AddonState.None
            }
            is HttpRequestTimeoutException -> AddonState.Error(R.string.error_timeout)
            is UnknownHostException, is UnresolvedAddressException -> {
                AddonState.Error(R.string.error_network_unreachable)
            }
            is ConnectException -> {
                AddonState.Error(R.string.error_connection_failed)
            }
            is SerializationException -> {
                AddonState.Error(R.string.error_parse_failed)
            }
            is ResponseException -> {
                val statusCode = e.response.status
                val res = when (statusCode) {
                    HttpStatusCode.Unauthorized -> R.string.error_unauthorized
                    HttpStatusCode.NotFound -> R.string.error_notfound
                    else -> R.string.error_client_error
                }
                AddonState.Error(res, arrayOf(statusCode))
            }
            else -> {
                lError("An unknown exception was caught!", e)
                val errorMessage = e.localizedMessage ?: e.message ?: e::class.qualifiedName ?: "Unknown error"
                AddonState.Error(R.string.error_unknown, arrayOf(errorMessage))
            }
        }
        updateState(state)
    }.getOrNull()
}

@Composable
fun OptiFineList(
    modifier: Modifier = Modifier,
    currentAddon: CurrentAddon,
    addonList: AddonList,
    error: String? = null,
    onValueChanged: () -> Unit = {},
    onReload: () -> Unit = {}
) {
    val items = remember(addonList.optifineList, currentAddon.forgeVersion) {
        addonList.optifineList?.filter { version ->
            currentAddon.forgeVersion?.let { forgeVersion ->
                isOptiFineCompatibleWithForge(version, forgeVersion)
            } ?: true
        }
    }

    AddonListLayout(
        modifier = modifier,
        state = currentAddon.optifineState,
        title = ModLoader.OPTIFINE.displayName,
        error = error,
        iconPainter = painterResource(R.drawable.img_loader_optifine),
        items = items,
        current = currentAddon.optifineVersion,
        incompatibleSet = currentAddon.incompatibleWithOptiFine,
        checkIncompatible = {
            val ofType = listOf(ModLoader.OPTIFINE)
            currentAddon.optifineVersion?.let { version ->
                val forgeVersion = currentAddon.forgeVersion
                //检查与 Forge 的兼容性
                if (forgeVersion != null) {
                    if (isOptiFineCompatibleWithForge(version, forgeVersion)) {
                        currentAddon.incompatibleWithForge -= ofType
                    } else {
                        currentAddon.incompatibleWithForge += ofType
                        currentAddon.forgeVersion = null
                    }
                } else {
                    if (isOptiFineCompatibleWithForgeList(version, addonList.forgeList)) {
                        currentAddon.incompatibleWithForge -= ofType
                    } else {
                        currentAddon.incompatibleWithForge += ofType
                        currentAddon.forgeVersion = null
                    }
                }
                currentAddon.neoforgeVersion = null
                currentAddon.fabricVersion = null
                currentAddon.fabricAPIVersion = null
                currentAddon.quiltVersion = null
                currentAddon.quiltAPIVersion = null
                currentAddon.cleanroomVersion = null
                currentAddon.incompatibleWithNeoForge += ofType
                currentAddon.incompatibleWithFabric += ofType
                currentAddon.incompatibleWithFabricAPI += ofType
                currentAddon.incompatibleWithQuilt += ofType
                currentAddon.incompatibleWithQuiltAPI += ofType
                currentAddon.incompatibleWithCleanroom += ofType
            } ?: run {
                currentAddon.incompatibleWithForge -= ofType
                currentAddon.incompatibleWithNeoForge -= ofType
                currentAddon.incompatibleWithFabric -= ofType
                currentAddon.incompatibleWithFabricAPI -= ofType
                currentAddon.incompatibleWithQuilt -= ofType
                currentAddon.incompatibleWithQuiltAPI -= ofType
                currentAddon.incompatibleWithCleanroom -= ofType
            }
        },
        triggerCheckIncompatible = arrayOf(currentAddon.forgeState),
        getItemText = { it.displayName },
        summary = { OptiFineVersionSummary(it) },
        onValueChange = { version ->
            currentAddon.optifineVersion = version
            onValueChanged()
        },
        onReload = onReload
    )
}

@Composable
fun ForgeList(
    modifier: Modifier = Modifier,
    currentAddon: CurrentAddon,
    addonList: AddonList,
    error: String? = null,
    onValueChanged: () -> Unit = {},
    onReload: () -> Unit = {}
) {
    val items = addonList.forgeList?.filter { version ->
        //选择 OptiFine 之后，根据 OptiFine 需求的 Forge 版本进行过滤
        currentAddon.optifineVersion?.let { optifineVersion ->
            isOptiFineCompatibleWithForge(optifineVersion, version)
        } ?: true
    }

    AddonListLayout(
        modifier = modifier,
        state = currentAddon.forgeState,
        title = ModLoader.FORGE.displayName,
        iconPainter = painterResource(R.drawable.img_anvil),
        items = items,
        current = currentAddon.forgeVersion,
        incompatibleSet = currentAddon.incompatibleWithForge,
        checkIncompatible = {
            val forgeType = listOf(ModLoader.FORGE)
            currentAddon.forgeVersion?.let { version ->
                val optiFineVersion = currentAddon.optifineVersion
                //检查与 OptiFine 的兼容性
                if (optiFineVersion != null) {
                    if (isOptiFineCompatibleWithForge(optiFineVersion, version)) {
                        currentAddon.incompatibleWithOptiFine -= forgeType
                    } else {
                        currentAddon.incompatibleWithOptiFine += forgeType
                        currentAddon.optifineVersion = null
                    }
                } else {
                    if (isForgeCompatibleWithOptiFineList(version, addonList.optifineList)) {
                        currentAddon.incompatibleWithForge -= forgeType
                    } else {
                        currentAddon.incompatibleWithOptiFine += forgeType
                        currentAddon.optifineVersion = null
                    }
                }
                currentAddon.neoforgeVersion = null
                currentAddon.fabricVersion = null
                currentAddon.fabricAPIVersion = null
                currentAddon.quiltVersion = null
                currentAddon.quiltAPIVersion = null
                currentAddon.cleanroomVersion = null
                currentAddon.incompatibleWithNeoForge += forgeType
                currentAddon.incompatibleWithFabric += forgeType
                currentAddon.incompatibleWithFabricAPI += forgeType
                currentAddon.incompatibleWithQuilt += forgeType
                currentAddon.incompatibleWithQuiltAPI += forgeType
                currentAddon.incompatibleWithCleanroom += forgeType
            } ?: run {
                currentAddon.incompatibleWithOptiFine -= forgeType
                currentAddon.incompatibleWithNeoForge -= forgeType
                currentAddon.incompatibleWithFabric -= forgeType
                currentAddon.incompatibleWithFabricAPI -= forgeType
                currentAddon.incompatibleWithQuilt -= forgeType
                currentAddon.incompatibleWithQuiltAPI -= forgeType
                currentAddon.incompatibleWithCleanroom -= forgeType
            }
        },
        triggerCheckIncompatible = arrayOf(currentAddon.optifineState),
        error = error ?: checkForgeCompatibilityError(addonList.forgeList),
        getItemText = { it.versionName },
        summary = { ForgeVersionSummary(it) },
        onValueChange = { version ->
            currentAddon.forgeVersion = version
            onValueChanged()
        },
        onReload = onReload
    )
}

@Composable
fun NeoForgeList(
    modifier: Modifier = Modifier,
    currentAddon: CurrentAddon,
    addonList: AddonList,
    error: String? = null,
    onValueChanged: () -> Unit = {},
    onReload: () -> Unit = {}
) {
    AddonListLayout(
        modifier = modifier,
        state = currentAddon.neoforgeState,
        title = ModLoader.NEOFORGE.displayName,
        error = error,
        iconPainter = painterResource(R.drawable.img_loader_neoforge),
        items = addonList.neoforgeList,
        current = currentAddon.neoforgeVersion,
        incompatibleSet = currentAddon.incompatibleWithNeoForge,
        checkIncompatible = {
            val neoforgeType = listOf(ModLoader.NEOFORGE)
            currentAddon.neoforgeVersion?.let {
                currentAddon.optifineVersion = null
                currentAddon.forgeVersion = null
                currentAddon.fabricVersion = null
                currentAddon.fabricAPIVersion = null
                currentAddon.quiltVersion = null
                currentAddon.quiltAPIVersion = null
                currentAddon.cleanroomVersion = null
                currentAddon.incompatibleWithOptiFine += neoforgeType
                currentAddon.incompatibleWithForge += neoforgeType
                currentAddon.incompatibleWithFabric += neoforgeType
                currentAddon.incompatibleWithFabricAPI += neoforgeType
                currentAddon.incompatibleWithQuilt += neoforgeType
                currentAddon.incompatibleWithQuiltAPI += neoforgeType
                currentAddon.incompatibleWithCleanroom += neoforgeType
            } ?: run {
                currentAddon.incompatibleWithOptiFine -= neoforgeType
                currentAddon.incompatibleWithForge -= neoforgeType
                currentAddon.incompatibleWithFabric -= neoforgeType
                currentAddon.incompatibleWithFabricAPI -= neoforgeType
                currentAddon.incompatibleWithQuilt -= neoforgeType
                currentAddon.incompatibleWithQuiltAPI -= neoforgeType
                currentAddon.incompatibleWithCleanroom -= neoforgeType
            }
        },
        getItemText = { it.versionName },
        summary = { NeoForgeSummary(it) },
        onValueChange = { version ->
            currentAddon.neoforgeVersion = version
            onValueChanged()
        },
        onReload = onReload
    )
}

@Composable
fun FabricList(
    modifier: Modifier = Modifier,
    currentAddon: CurrentAddon,
    addonList: AddonList,
    error: String? = null,
    onValueChanged: (FabricVersion?) -> Unit = {},
    onReload: () -> Unit = {}
) {
    AddonListLayout(
        modifier = modifier,
        state = currentAddon.fabricState,
        title = ModLoader.FABRIC.displayName,
        error = error,
        iconPainter = painterResource(R.drawable.img_loader_fabric),
        items = addonList.fabricList,
        current = currentAddon.fabricVersion,
        incompatibleSet = currentAddon.incompatibleWithFabric,
        checkIncompatible = {
            val fabricType = listOf(ModLoader.FABRIC)
            currentAddon.fabricVersion?.let {
                currentAddon.optifineVersion = null
                currentAddon.forgeVersion = null
                currentAddon.neoforgeVersion = null
                currentAddon.quiltVersion = null
                currentAddon.quiltAPIVersion = null
                currentAddon.cleanroomVersion = null
                currentAddon.incompatibleWithOptiFine += fabricType
                currentAddon.incompatibleWithForge += fabricType
                currentAddon.incompatibleWithNeoForge += fabricType
                currentAddon.incompatibleWithQuilt += fabricType
                currentAddon.incompatibleWithQuiltAPI += fabricType
                currentAddon.incompatibleWithCleanroom += fabricType
            } ?: run {
                currentAddon.incompatibleWithOptiFine -= fabricType
                currentAddon.incompatibleWithForge -= fabricType
                currentAddon.incompatibleWithNeoForge -= fabricType
                currentAddon.incompatibleWithQuilt -= fabricType
                currentAddon.incompatibleWithQuiltAPI -= fabricType
                currentAddon.incompatibleWithCleanroom -= fabricType
            }
        },
        getItemText = { it.version },
        summary = { FabricLikeSummary(it) },
        onValueChange = { version ->
            currentAddon.fabricVersion = version
            onValueChanged(version)
        },
        onReload = onReload
    )
}

@Composable
fun FabricAPIList(
    modifier: Modifier = Modifier,
    currentAddon: CurrentAddon,
    requestString: String = stringResource(R.string.download_game_addon_request_addon, ModLoader.FABRIC.displayName),
    addonList: AddonList,
    error: String? = null,
    onValueChanged: () -> Unit = {},
    onReload: () -> Unit = {}
) {
    val unSelectedFabric = remember(currentAddon.fabricVersion) {
        when {
            currentAddon.fabricVersion == null -> {
                currentAddon.fabricAPIVersion = null
                requestString
            }
            else -> null
        }
    }

    AddonListLayout(
        modifier = modifier,
        state = currentAddon.fabricAPIState,
        title = ModLoader.FABRIC_API.displayName,
        iconPainter = painterResource(R.drawable.img_loader_fabric),
        items = addonList.fabricAPIList,
        current = currentAddon.fabricAPIVersion,
        incompatibleSet = currentAddon.incompatibleWithFabricAPI,
        checkIncompatible = {
            val fabricType = listOf(ModLoader.FABRIC_API)
            currentAddon.fabricAPIVersion?.let {
                currentAddon.optifineVersion = null
                currentAddon.forgeVersion = null
                currentAddon.neoforgeVersion = null
                currentAddon.quiltVersion = null
                currentAddon.quiltAPIVersion = null
                currentAddon.cleanroomVersion = null
                currentAddon.incompatibleWithOptiFine += fabricType
                currentAddon.incompatibleWithForge += fabricType
                currentAddon.incompatibleWithNeoForge += fabricType
                currentAddon.incompatibleWithQuilt += fabricType
                currentAddon.incompatibleWithQuiltAPI += fabricType
                currentAddon.incompatibleWithCleanroom += fabricType
            } ?: run {
                currentAddon.incompatibleWithOptiFine -= fabricType
                currentAddon.incompatibleWithForge -= fabricType
                currentAddon.incompatibleWithNeoForge -= fabricType
                currentAddon.incompatibleWithQuilt -= fabricType
                currentAddon.incompatibleWithQuiltAPI -= fabricType
                currentAddon.incompatibleWithCleanroom -= fabricType
            }
        },
        error = error ?: unSelectedFabric,
        getItemText = { it.displayName },
        summary = { ModSummary(it) },
        onValueChange = { version ->
            currentAddon.fabricAPIVersion = version
            onValueChanged()
        },
        onReload = onReload
    )
}

@Composable
fun QuiltList(
    modifier: Modifier = Modifier,
    currentAddon: CurrentAddon,
    addonList: AddonList,
    error: String? = null,
    onValueChanged: (QuiltVersion?) -> Unit = {},
    onReload: () -> Unit = {}
) {
    AddonListLayout(
        modifier = modifier,
        state = currentAddon.quiltState,
        title = ModLoader.QUILT.displayName,
        error = error,
        iconPainter = painterResource(R.drawable.img_loader_quilt),
        items = addonList.quiltList,
        current = currentAddon.quiltVersion,
        incompatibleSet = currentAddon.incompatibleWithQuilt,
        checkIncompatible = {
            val quiltType = listOf(ModLoader.QUILT)
            currentAddon.quiltVersion?.let {
                currentAddon.optifineVersion = null
                currentAddon.forgeVersion = null
                currentAddon.neoforgeVersion = null
                currentAddon.fabricVersion = null
                currentAddon.fabricAPIVersion = null
                currentAddon.cleanroomVersion = null
                currentAddon.incompatibleWithOptiFine += quiltType
                currentAddon.incompatibleWithForge += quiltType
                currentAddon.incompatibleWithNeoForge += quiltType
                currentAddon.incompatibleWithFabric += quiltType
                currentAddon.incompatibleWithFabricAPI += quiltType
                currentAddon.incompatibleWithCleanroom += quiltType
            } ?: run {
                currentAddon.incompatibleWithOptiFine -= quiltType
                currentAddon.incompatibleWithForge -= quiltType
                currentAddon.incompatibleWithNeoForge -= quiltType
                currentAddon.incompatibleWithFabric -= quiltType
                currentAddon.incompatibleWithFabricAPI -= quiltType
                currentAddon.incompatibleWithCleanroom -= quiltType
            }
        },
        getItemText = { it.version },
        summary = { FabricLikeSummary(it) },
        onValueChange =  { version ->
            currentAddon.quiltVersion = version
            onValueChanged(version)
        },
        onReload = onReload
    )
}

@Composable
fun QuiltAPIList(
    modifier: Modifier = Modifier,
    currentAddon: CurrentAddon,
    requestString: String = stringResource(R.string.download_game_addon_request_addon, ModLoader.QUILT.displayName),
    addonList: AddonList,
    error: String? = null,
    onValueChanged: () -> Unit = {},
    onReload: () -> Unit = {}
) {
    val unSelectedQuilt = remember(currentAddon.quiltVersion) {
        when {
            currentAddon.quiltVersion == null -> {
                currentAddon.quiltAPIVersion = null
                requestString
            }
            else -> null
        }
    }

    AddonListLayout(
        modifier = modifier,
        state = currentAddon.quiltAPIState,
        title = ModLoader.QUILT_API.displayName,
        iconPainter = painterResource(R.drawable.img_loader_quilt),
        items = addonList.quiltAPIList,
        current = currentAddon.quiltAPIVersion,
        incompatibleSet = currentAddon.incompatibleWithQuiltAPI,
        checkIncompatible = {
            val quiltType = listOf(ModLoader.QUILT_API)
            currentAddon.quiltAPIVersion?.let {
                currentAddon.optifineVersion = null
                currentAddon.forgeVersion = null
                currentAddon.neoforgeVersion = null
                currentAddon.fabricVersion = null
                currentAddon.fabricAPIVersion = null
                currentAddon.cleanroomVersion = null
                currentAddon.incompatibleWithOptiFine += quiltType
                currentAddon.incompatibleWithForge += quiltType
                currentAddon.incompatibleWithNeoForge += quiltType
                currentAddon.incompatibleWithFabric += quiltType
                currentAddon.incompatibleWithFabricAPI += quiltType
                currentAddon.incompatibleWithCleanroom += quiltType
            } ?: run {
                currentAddon.incompatibleWithOptiFine -= quiltType
                currentAddon.incompatibleWithForge -= quiltType
                currentAddon.incompatibleWithNeoForge -= quiltType
                currentAddon.incompatibleWithFabric -= quiltType
                currentAddon.incompatibleWithFabricAPI -= quiltType
                currentAddon.incompatibleWithCleanroom -= quiltType
            }
        },
        error = error ?: unSelectedQuilt,
        getItemText = { it.displayName },
        summary = { ModSummary(it) },
        onValueChange =  { version ->
            currentAddon.quiltAPIVersion = version
            onValueChanged()
        },
        onReload = onReload
    )
}

@Composable
fun CleanroomList(
    modifier: Modifier = Modifier,
    currentAddon: CurrentAddon,
    addonList: AddonList,
    error: String? = null,
    onValueChanged: (CleanroomVersion?) -> Unit = {},
    onReload: () -> Unit = {}
) {
    AddonListLayout(
        modifier = modifier,
        state = currentAddon.cleanroomState,
        title = ModLoader.CLEANROOM.displayName,
        error = error,
        iconPainter = painterResource(R.drawable.img_loader_cleanroom),
        items = addonList.cleanroomList,
        current = currentAddon.cleanroomVersion,
        incompatibleSet = currentAddon.incompatibleWithCleanroom,
        checkIncompatible = {
            val cleanroomType = listOf(ModLoader.CLEANROOM)
            currentAddon.cleanroomVersion?.let {
                currentAddon.optifineVersion = null
                currentAddon.forgeVersion = null
                currentAddon.neoforgeVersion = null
                currentAddon.fabricVersion = null
                currentAddon.fabricAPIVersion = null
                currentAddon.quiltVersion = null
                currentAddon.quiltAPIVersion = null
                currentAddon.incompatibleWithOptiFine += cleanroomType
                currentAddon.incompatibleWithForge += cleanroomType
                currentAddon.incompatibleWithNeoForge += cleanroomType
                currentAddon.incompatibleWithFabric += cleanroomType
                currentAddon.incompatibleWithFabricAPI += cleanroomType
                currentAddon.incompatibleWithQuilt += cleanroomType
                currentAddon.incompatibleWithQuiltAPI += cleanroomType
            } ?: run {
                currentAddon.incompatibleWithOptiFine -= cleanroomType
                currentAddon.incompatibleWithForge -= cleanroomType
                currentAddon.incompatibleWithNeoForge -= cleanroomType
                currentAddon.incompatibleWithFabric -= cleanroomType
                currentAddon.incompatibleWithFabricAPI -= cleanroomType
                currentAddon.incompatibleWithQuilt -= cleanroomType
                currentAddon.incompatibleWithQuiltAPI -= cleanroomType
            }
        },
        getItemText = { it.version },
        summary = { CleanroomSummary(it) },
        onValueChange =  { version ->
            currentAddon.cleanroomVersion = version
            onValueChanged(version)
        },
        onReload = onReload
    )
}

private fun isOptiFineCompatibleWithForge(
    optifine: OptiFineVersion,
    forge: ForgeVersion
): Boolean = optifine.forgeVersion?.let {
    //空字符串表示兼容所有
    it.isEmpty() || forge.forgeBuildVersion.compareOptiFineRequired(it)
} ?: false //没有声明需要的 Forge 版本，视为不兼容

private fun isOptiFineCompatibleWithForgeList(
    optifine: OptiFineVersion,
    forgeList: List<ForgeVersion>?
): Boolean {
    //没有声明需要的 Forge 版本，视为不兼容
    val requiredVersion = optifine.forgeVersion ?: return false
    return when {
        requiredVersion.isEmpty() -> true //为空则表示不要求，兼容
        else -> forgeList?.any {
            it.forgeBuildVersion.compareOptiFineRequired(requiredVersion)
        } == true
    }
}

private fun isForgeCompatibleWithOptiFineList(
    forge: ForgeVersion,
    optifineList: List<OptiFineVersion>?
): Boolean {
    val forgeVersion = forge.forgeBuildVersion

    optifineList?.forEach { optifine ->
        val ofVersion = optifine.forgeVersion ?: return@forEach //null: 不兼容，跳过
        if (ofVersion.isEmpty()) return true    //空字符串表示兼容所有
        if (forgeVersion.compareOptiFineRequired(ofVersion)) return true
    }

    return false //没有匹配项
}

@Composable
private fun checkForgeCompatibilityError(
    forgeList: List<ForgeVersion>?
): String? {
    return when {
        forgeList == null -> null //保持默认的“不可用”
        forgeList.any { forgeVersion -> forgeVersion.category == "universal" || forgeVersion.category == "client" } -> {
            //跳过无法自动安装的版本
            stringResource(R.string.download_game_addon_not_installable)
        }
        else -> null
    }
}