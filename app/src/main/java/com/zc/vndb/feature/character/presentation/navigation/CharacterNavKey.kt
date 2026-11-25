package com.zc.vndb.feature.character.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** A character's page. */
@Serializable
data class CharacterNavKey(val id: String) : NavKey
