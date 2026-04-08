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

package com.movtery.zalithlauncher.components.lwjgl

import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.path.PathManager
import java.io.File

enum class LWJGL(
    val component: String,
    val version: String,
    val summary: Int
) {
    LWJGL3_3_3(
        component = "LWJGL 3.3.3",
        version = "3.3.3",
        summary = R.string.unpack_screen_lwjgl
    ),

    LWJGL3_4_1(
        component = "LWJGL 3.4.1",
        version = "3.4.1",
        summary = R.string.unpack_screen_lwjgl
    );

    fun assetsDirName(): String = "components"

    fun getPath(): String = "lwjgl3/$version"

    fun getRoot() = File(PathManager.DIR_COMPONENTS, getPath())

    fun getNativesDirName(): String = "lwjgl-$version-natives"

    fun getNativesDir(): File = File(getRoot(), "natives")
}