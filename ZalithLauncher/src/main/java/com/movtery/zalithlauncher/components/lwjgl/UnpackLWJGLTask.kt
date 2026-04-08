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

import android.content.Context
import com.movtery.zalithlauncher.components.UnpackSingleTask
import com.movtery.zalithlauncher.context.copyAssetFile
import com.movtery.zalithlauncher.path.PathManager
import com.movtery.zalithlauncher.utils.device.Architecture
import org.apache.commons.io.FileUtils
import java.io.File

class UnpackLWJGLTask(
    context: Context,
    val lwjgl: LWJGL
): UnpackSingleTask(
    context = context,
    rootDir = PathManager.DIR_COMPONENTS,
    assetsDirName = lwjgl.assetsDirName(),
    fileDirName = lwjgl.getPath()
) {
    override suspend fun run() {
        super.run()
        //解压原生库
        var nativesRoot = "$assetsDirName/${lwjgl.getNativesDirName()}"
        val arch = Architecture.archAsStringAndroid(Architecture.getDeviceArchitecture())
        if (arch == "UNSUPPORTED_ARCH") return
        nativesRoot += "/$arch"

        val localNativesDir = lwjgl.getNativesDir()

        val fileList = am.list(nativesRoot)
        for (fileName in fileList!!) {
            val file = File(localNativesDir, fileName)
            context.copyAssetFile(
                "$nativesRoot/$fileName",
                file,
                true
            )
            moreProgress(file)
        }
    }
}