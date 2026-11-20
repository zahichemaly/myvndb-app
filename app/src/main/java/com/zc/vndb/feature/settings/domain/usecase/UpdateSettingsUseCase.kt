package com.zc.vndb.feature.settings.domain.usecase

import com.zc.vndb.common.domain.model.AppSettings
import com.zc.vndb.common.domain.repository.SettingsRepository
import org.koin.core.annotation.Factory

/**
 * Changes settings. New & Trending keeps at least one language, and the card languages keep at
 * most [AppSettings.MAX_CARD_LANGUAGES].
 */
@Factory
class UpdateSettingsUseCase(private val repository: SettingsRepository) {

    suspend operator fun invoke(transform: (AppSettings) -> AppSettings) =
        repository.update { current ->
            val updated = transform(current)
            updated.copy(
                trendingLanguages = updated.trendingLanguages.ifEmpty { current.trendingLanguages },
                cardLanguages = updated.cardLanguages.distinct().take(
                    AppSettings.MAX_CARD_LANGUAGES,
                ),
            )
        }
}
