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

package com.movtery.zalithlauncher.ui.base

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.CallSuper
import androidx.annotation.RequiresApi

enum class WindowMode {
    DEFAULT,
    FULL_IMMERSIVE
}

abstract class FullScreenAppCompatActivity : AbstractAppCompatActivity() {
    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        applyFullscreen(getWindowMode())
        super.onCreate(savedInstanceState)
    }

    @CallSuper
    override fun onPostResume() {
        super.onPostResume()
        applyFullscreen(getWindowMode())
    }

    @CallSuper
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            applyFullscreen(getWindowMode())
        }
    }

    abstract fun getWindowMode(): WindowMode

    @Suppress("DEPRECATION")
    private val systemUIVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )

    @Suppress("DEPRECATION")
    private fun applyFullscreen(mode: WindowMode) {
        if (window != null) {
            when (mode) {
                WindowMode.DEFAULT -> {
                    applyDefault()
                }
                WindowMode.FULL_IMMERSIVE -> {
                    if (isInMultiWindowMode) {
                        applyDefault()
                    } else {
                        applyFullImmersive()
                    }
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun applyDefault() {
        if (window != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                applyWindowAttributes(WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER)
            }

            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN) //隐藏状态栏
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }

    @Suppress("DEPRECATION")
    private fun applyFullImmersive() {
        if (window != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                applyWindowAttributes(WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES)
            }

            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.decorView.systemUiVisibility = systemUIVisibility
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun applyWindowAttributes(newParams: Int) {
        val params = window.attributes
        if (params.layoutInDisplayCutoutMode != newParams) {
            params.layoutInDisplayCutoutMode = newParams
            window.attributes = params
        }
    }

    protected fun refreshWindow() {
        applyFullscreen(getWindowMode())
    }
}