/*
 * Copyright (C) 2023 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dolby.tile

import android.content.Context
import android.media.audiofx.AudioEffect

import com.dolby.tile.DolbyFragment.Companion.PREF_DOLBY_MODES

import java.util.UUID

object DolbyCore {
    private const val EFFECT_PARAM_PROFILE = 0
    private const val EFFECT_PARAM_EFF_ENAB = 19

    private val EFFECT_TYPE_DAP = UUID.fromString("46d279d9-9be7-453d-9d7c-ef937f675587")

    const val PROFILE_TDS = 0
    const val PROFILE_DUMMY = 1

    private val audioEffect = runCatching {
        AudioEffect(EFFECT_TYPE_DAP, AudioEffect.EFFECT_TYPE_NULL, 0, 0)
    }.getOrNull()

    fun getProfile(): Int {
        val out = intArrayOf(PROFILE_TDS)
        audioEffect?.getParameter(EFFECT_PARAM_PROFILE, out)
        return out.first().coerceIn(PROFILE_TDS, PROFILE_DUMMY)
    }

    fun getProfileName(context: Context): String {
        val profile = getProfile()
        val resourceName = PREF_DOLBY_MODES.filter { it.value == profile }.keys.first()

        return context.resources.getString(context.resources.getIdentifier(
                resourceName, "string", context.packageName
        ))
    }

    fun setProfile(profile: Int) {
        audioEffect?.setParameter(EFFECT_PARAM_EFF_ENAB, 1)
        audioEffect?.setParameter(EFFECT_PARAM_PROFILE, profile)
    }

    fun setEnabled(enabled: Boolean) {
        audioEffect?.enabled = enabled
    }

    fun isEnabled() = audioEffect?.enabled ?: false
}
